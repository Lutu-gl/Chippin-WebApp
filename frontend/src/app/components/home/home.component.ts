import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {GroupListDto} from "../../dtos/group";
import {GroupService} from "../../services/group.service";
import {ToastrService} from "ngx-toastr";
import { FriendshipService } from 'src/app/services/friendship.service';
import { AcceptFriendRequest } from 'src/app/dtos/friend-request';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

  constructor(
    public authService: AuthService,
    private groupService: GroupService,
    private friendshipService: FriendshipService,
    private notification: ToastrService,
    ) { }
  groups: GroupListDto[] = [];
  incomingFriendRequests: string[] = [];
  friends: string[] = [];

  ngOnInit(): void {
    console.log("logged in? ", this.authService.isLoggedIn());
    if(this.authService.isLoggedIn()){
      this.friendshipService.getIncomingFriendRequests().subscribe({
        next: data => {
          this.incomingFriendRequests = data;
        },
        error: error => {
          this.printError(error);
        }
      });

      this.friendshipService.getFriends().subscribe({
        next: data => {
          this.friends = data;
        },
        error: error => {
          this.printError(error);
        }
      });

      this.groupService.getGroups().subscribe({
        next: data => {
          this.groups = data;
        },
        error: error => {
          this.printError(error);
        }
      });
    }
  }

  printError(error): void {
    if (error && error.error && error.error.errors) {
      for (let i = 0; i < error.error.errors.length; i++) {
        this.notification.error(`${error.error.errors[i]}`);
      }
    } else if (error && error.error && error.error.message) { // if no detailed error explanation exists. Give a more general one if available.
      this.notification.error(`${error.error.message}`);
    } else {
      console.error('Error', error);
      if(error.status !== 401) {
        const errorMessage = error.status === 0
          ? 'Is the backend up?'
          : error.message.message;
        this.notification.error(errorMessage, 'Could not connect to the server.');
      }
    }
  }

  acceptFriendRequest(email: string): void {
    const acceptFriendRequest: AcceptFriendRequest = new AcceptFriendRequest();
    acceptFriendRequest.senderEmail = email;
    this.friendshipService.acceptFriendRequest(acceptFriendRequest).subscribe({
      next: () => {
        this.notification.success("Accepted friend request successfully!");
        this.incomingFriendRequests = this.incomingFriendRequests.filter(senderEmail => senderEmail !== email);
        this.friends.push(email);
      },
      error: (error) => {
        this.notification.error(error.error.detail);
      }
    })
  }

  rejectFriendRequest(email: string): void {
    this.friendshipService.rejectFriendRequest(email).subscribe({
      next: () => {
        this.notification.success("Rejected friend request successfully!");
        this.incomingFriendRequests = this.incomingFriendRequests.filter(senderEmail => senderEmail !== email);
      },
      error: (error) => {
        this.notification.error(error.error.detail);
      }
    })
  }

}
