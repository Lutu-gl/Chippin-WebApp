import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { lastValueFrom } from 'rxjs';
import { FriendInfoDto } from 'src/app/dtos/friend';
import { AcceptFriendRequest } from 'src/app/dtos/friend-request';
import { AuthService } from 'src/app/services/auth.service';
import { DebtService } from 'src/app/services/debt.service';
import { FriendshipService } from 'src/app/services/friendship.service';
import { GroupService } from 'src/app/services/group.service';


@Component({
  selector: 'app-friends',
  templateUrl: './friends.component.html',
  styleUrl: './friends.component.scss'
})
export class FriendsComponent implements OnInit {
  incomingFriendRequests: string[] = [];
  outgoingFriendRequests: string[] = [];
  friends: FriendInfoDto[] = [];

  constructor(
    public authService: AuthService,
    private friendshipService: FriendshipService,
    private groupService: GroupService,
    private debtService: DebtService,
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

    this.friendshipService.getOutgoingFriendRequests().subscribe({
      next: (data) => {
        this.outgoingFriendRequests = data;
      },
      error: (e) => {
        this.notification.error("Failed to load outgoing friend requests!");
      }
    });

    this.friendshipService.getFriendsWithDebtInfos().subscribe({
      next: async (data) => {
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
        this.friends.push({ email: email, totalAmount: 0, groupAmounts: {} });
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

  retractFriendRequest(email: string): void {
    this.friendshipService.retractFriendRequest(email).subscribe({
      next: () => {
        this.notification.success("Retracted friend request successfully!");
        this.outgoingFriendRequests = this.outgoingFriendRequests.filter(receiverEmail => receiverEmail !== email);
      },
      error: (error) => {
        this.notification.error(error.error.detail);
      }
    })
  }

}
