import { Component, OnInit } from '@angular/core';
import { FriendInfoDto } from 'src/app/dtos/friend';
import { AcceptFriendRequest, FriendRequest } from 'src/app/dtos/friend-request';
import { AuthService } from 'src/app/services/auth.service';
import { FriendshipService } from 'src/app/services/friendship.service';
import { MessageService } from "primeng/api";


@Component({
  selector: 'app-friends',
  templateUrl: './friends.component.html',
  styleUrl: './friends.component.scss'
})
export class FriendsComponent implements OnInit {
  incomingFriendRequests: string[] = [];
  outgoingFriendRequests: string[] = [];
  friends: FriendInfoDto[] = [];

  responseReceived: boolean = false;

  friendRequestEmail: string = "";
  isAddFriendDialogVisible: boolean = false;

  friendInfoEmail: string = "";
  friendInfoTotalAmount: number = 0;
  friendInfoGroupAmounts: any = [];
  isFriendInfoDialogVisible: boolean = false;

  constructor(
    public authService: AuthService,
    private friendshipService: FriendshipService,
    private messageService: MessageService,
  ) { }

  ngOnInit(): void {
    this.friendshipService.getIncomingFriendRequests().subscribe({
      next: (data) => {
        this.incomingFriendRequests = data;
      },
      error: (e) => {
        this.messageService.add({severity:'error', summary:'Error', detail: 'Failed to load incoming friend requests!'});
      }
    });

    this.friendshipService.getOutgoingFriendRequests().subscribe({
      next: (data) => {
        this.outgoingFriendRequests = data;
      },
      error: (e) => {
        this.messageService.add({severity:'error', summary:'Error', detail: 'Failed to load outgoing friend requests!'});
      }
    });

    this.friendshipService.getFriendsWithDebtInfos().subscribe({
      next: async (data) => {
        this.friends = data;
        this.responseReceived = true;
      },
      error: (e) => {
        this.messageService.add({severity:'error', summary:'Error', detail: 'Failed to load friends!'});
      }
    });

  }

  openAddFriendDialog() {
    this.friendRequestEmail = "";
    this.isAddFriendDialogVisible = true;
  }

  closeAddFriendDialog() {
    this.isAddFriendDialogVisible = false;
  }

  openFriendInfoDialog(friend: string) {
    this.friendInfoEmail = friend;
    const friendInfo = this.friends.find(f => f.email === friend);
    this.friendInfoTotalAmount = friendInfo.totalAmount;
    console.log(friendInfo.groupAmounts);
    this.friendInfoGroupAmounts = Object.values(friendInfo.groupAmounts);
    this.isFriendInfoDialogVisible = true;
  }

  closeFriendInfoDialog() {
    this.isFriendInfoDialogVisible = false;
  }

  sendFriendRequest() {
    if (!this.friendRequestEmail) {
      this.messageService.add({ severity: 'warn', summary: 'Invalid friend request', detail: 'Please enter an email address!' });
      return;
    }

    const friendRequest: FriendRequest = new FriendRequest();
    friendRequest.receiverEmail = this.friendRequestEmail;
    this.friendshipService.sendFriendRequest(friendRequest).subscribe({
      next: () => {
        this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Sent friend request successfully!' });
        this.outgoingFriendRequests.push(this.friendRequestEmail);
        this.closeAddFriendDialog();
      },
      error: error => {
        if (error && error.error && error.error.errors) {
          for (let i = 0; i < error.error.errors.length; i++) {
            this.messageService.add({severity:'error', summary:'Error', detail: error.error.errors[i]});
          }
        } else if (error && error.error && error.error.message) { // if no detailed error explanation exists. Give a more general one if available.
          this.messageService.add({severity:'error', summary:'Error', detail: error.error.message});
        } else if (error && error.error.detail) {
          this.messageService.add({severity:'error', summary:'Error', detail: error.error.detail});
        } else if(error && error.error) {
          this.messageService.add({severity:'error', summary:'Error', detail: error.error});
        } else {
          this.messageService.add({severity:'error', summary:'Error', detail: 'Operation failed!'});
        }
      }
    });
  }


  acceptFriendRequest(email: string): void {
    const acceptFriendRequest: AcceptFriendRequest = new AcceptFriendRequest();
    acceptFriendRequest.senderEmail = email;
    this.friendshipService.acceptFriendRequest(acceptFriendRequest).subscribe({
      next: () => {
        this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Accepted friend request successfully!' });
        this.incomingFriendRequests = this.incomingFriendRequests.filter(senderEmail => senderEmail !== email);
        this.friends.push({ email: email, totalAmount: 0, groupAmounts: {} });
      },
      error: (error) => {
        this.messageService.add({severity:'error', summary:'Error', detail: error.error.detail});
      }
    })
  }

  rejectFriendRequest(email: string): void {
    this.friendshipService.rejectFriendRequest(email).subscribe({
      next: () => {
        this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Rejected friend request successfully!' });
        this.incomingFriendRequests = this.incomingFriendRequests.filter(senderEmail => senderEmail !== email);
      },
      error: (error) => {
        this.messageService.add({severity:'error', summary:'Error', detail: error.error.detail});
      }
    })
  }

  retractFriendRequest(email: string): void {
    this.friendshipService.retractFriendRequest(email).subscribe({
      next: () => {
        this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Retracted friend request successfully!' });
        this.outgoingFriendRequests = this.outgoingFriendRequests.filter(receiverEmail => receiverEmail !== email);
      },
      error: (error) => {
        this.messageService.add({severity:'error', summary:'Error', detail: error.error.detail});
      }
    })
  }

}
