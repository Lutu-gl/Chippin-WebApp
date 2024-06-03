import { Component } from '@angular/core';
import {CurrencyPipe, NgClass, NgForOf, NgIf} from "@angular/common";
import {AuthService} from "../../../services/auth.service";
import {FriendshipService} from "../../../services/friendship.service";
import {ToastrService} from "ngx-toastr";
import {AcceptFriendRequest} from "../../../dtos/friend-request";
import { RouterLink } from '@angular/router';
import { lastValueFrom } from 'rxjs';
import { GroupService } from 'src/app/services/group.service';
import { DebtService } from 'src/app/services/debt.service';

interface FriendInfo {
  email: string,
  debt: number
}

@Component({
  selector: 'app-friends-info-card-content',
  standalone: true,
  imports: [
    NgForOf,
    NgIf,
    RouterLink,
    NgClass,
    CurrencyPipe
  ],
  templateUrl: './friends-info-card-content.component.html',
  styleUrl: './friends-info-card-content.component.scss'
})
export class FriendsInfoCardContentComponent {

  constructor(
    public authService: AuthService,
    private friendshipService: FriendshipService,
    private groupService: GroupService,
    private debtService: DebtService,
    private notification: ToastrService,
  ) { }
  incomingFriendRequests: string[] = [];
  friends: FriendInfo[] = [];

  ngOnInit(): void {
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
        next: async data => {
          for (let friend of data) {
            let totalDebt = 0.0;
            const groups = await lastValueFrom(this.groupService.getGroups());
            for (let group of groups) {
              const detailedGroup = await lastValueFrom(this.groupService.getById(group.id))
              if (detailedGroup.members.some(member => member.email === friend)) {
                totalDebt += (await lastValueFrom(this.debtService.getDebtById(group.id))).membersDebts[friend];
              }
            }
            this.friends.push({ email: friend, debt: totalDebt });
          }
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
        this.friends.push({ email: email, debt: 0 })
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
