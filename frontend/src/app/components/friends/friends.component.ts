import { Component, OnInit } from '@angular/core';
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

  constructor(public authService: AuthService, private friendshipService: FriendshipService) { }

  ngOnInit(): void {
    this.friendshipService.getIncomingFriendRequests().subscribe({
      next: (data) => {
        this.incomingFriendRequests = data;
      },
      error: (e) => {
        alert("Get incoming friend requests failed!");
      }
    });

    this.friendshipService.getFriends().subscribe({
      next: (data) => {
        this.friends = data;
      },
      error: (e) => {
        alert("Get friends failed!");
      }
    })

  }

  acceptFriendRequest(email: string): void {
    const acceptFriendRequest: AcceptFriendRequest = new AcceptFriendRequest();
    acceptFriendRequest.senderEmail = email;
    this.friendshipService.acceptFriendRequest(acceptFriendRequest).subscribe({
      next: () => {
        alert("it worked!");
      },
      error: () => {
        alert("It did not work!");
      }
    })
  }

  rejectFriendRequest(email: string): void {
    this.friendshipService.rejectFriendRequest(email).subscribe({
      next: () => {
        alert("It worked!");
      },
      error: () => {
        alert("It did not work!");
      }
    })
  }

}
