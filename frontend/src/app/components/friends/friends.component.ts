import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { lastValueFrom } from 'rxjs';
import { AcceptFriendRequest } from 'src/app/dtos/friend-request';
import { AuthService } from 'src/app/services/auth.service';
import { DebtService } from 'src/app/services/debt.service';
import { FriendshipService } from 'src/app/services/friendship.service';
import { GroupService } from 'src/app/services/group.service';

interface FriendInfo {
  email: string,
  debt: number
}

@Component({
  selector: 'app-friends',
  templateUrl: './friends.component.html',
  styleUrl: './friends.component.scss'
})
export class FriendsComponent implements OnInit {
  incomingFriendRequests: string[] = [];
  friends: FriendInfo[] = [];

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

    this.friendshipService.getFriends().subscribe({
      next: async (data) => {
        for (let friend of data) {
          let totalDebt = 0.0;
          const groups = await lastValueFrom(this.groupService.getGroups());
          for (let group of groups) {
            const detailedGroup = await lastValueFrom(this.groupService.getById(group.id))
            if (detailedGroup.members.some(member => member === friend)) {
              totalDebt += (await lastValueFrom(this.debtService.getDebtById(group.id))).membersDebts[friend];
            }
          }
          this.friends.push({ email: friend, debt: totalDebt });
        }
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
        this.friends.push({ email: email, debt: 0 });
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
