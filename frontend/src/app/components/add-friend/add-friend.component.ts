import { Component, OnInit } from '@angular/core';
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


  constructor(public authService: AuthService, private friendshipService: FriendshipService) { }

  ngOnInit(): void {
  }

  addFriend(): void {
    const friendRequest: FriendRequest = new FriendRequest();
    friendRequest.receiverEmail = this.receiverEmail;
    console.log(friendRequest);
    this.friendshipService.sendFriendRequest(friendRequest).subscribe({
      next: () => {
        alert("It worked!");
      },
      error: () => {
        alert("It did not work!");
      }
    });
  }

}
