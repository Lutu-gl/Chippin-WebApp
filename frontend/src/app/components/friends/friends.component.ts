import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { AcceptFriendRequest } from 'src/app/dtos/friend-request';
import { AuthService } from 'src/app/services/auth.service';
import { FriendshipService } from 'src/app/services/friendship.service';

@Component({
  selector: 'app-friends',
  templateUrl: './friends.component.html',
  styleUrl: './friends.component.scss'
})
export class FriendsComponent implements OnInit {
  incomingFriendRequests: string[] = [];
  friends: string[] = [];

  constructor(
    public authService: AuthService,
    private friendshipService: FriendshipService,
    private notification: ToastrService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.friendshipService.getIncomingFriendRequests().subscribe({
      next: (data) => {
        this.incomingFriendRequests = data;
      },
      error: (e) => {
        this.notification.error("Failed to load incoming friend requests!");
      }
    });

    this.friendshipService.getFriends().subscribe({
      next: (data) => {
        this.friends = data;
      },
      error: (e) => {
        this.notification.error("Failed to load friends!");
      }
    });

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
