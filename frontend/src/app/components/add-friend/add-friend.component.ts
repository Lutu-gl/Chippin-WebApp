import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { FriendRequest } from 'src/app/dtos/friend-request';
import { AuthService } from 'src/app/services/auth.service';
import { FriendshipService } from 'src/app/services/friendship.service';

@Component({
  selector: 'app-add-friend',
  templateUrl: './add-friend.component.html',
  styleUrl: './add-friend.component.scss'
})
export class AddFriendComponent implements OnInit {
  receiverEmail: string;


  constructor(
    public authService: AuthService,
    private friendshipService: FriendshipService,
    private notification: ToastrService,
    private router: Router
  ) { }

  ngOnInit(): void {
  }

  addFriend(): void {
    if (!this.receiverEmail) {
      this.notification.warning("Please enter an email address");
      return;
    }

    const friendRequest: FriendRequest = new FriendRequest();
    friendRequest.receiverEmail = this.receiverEmail;
    console.log(friendRequest);
    this.friendshipService.sendFriendRequest(friendRequest).subscribe({
      next: () => {
        this.notification.success("Send friend request successfully!");
        this.router.navigate(["/"])
      },
      error: error => {
        if (error && error.error && error.error.errors) {
          //this.notification.error(`${error.error.errors.join('. \n')}`);
          for (let i = 0; i < error.error.errors.length; i++) {
            this.notification.error(`${error.error.errors[i]}`);
          }
        } else if (error && error.error && error.error.message) { // if no detailed error explanation exists. Give a more general one if available.
          this.notification.error(`${error.error.message}`);
        } else if (error && error.error.detail) {
          this.notification.error(`${error.error.detail}`);
        } else if(error && error.error) {
          this.notification.error(`${error.error}`);
        } else {
          this.notification.error('Operation failed');
          console.error('Operation failed');
        }
      }
    });
  }

}
