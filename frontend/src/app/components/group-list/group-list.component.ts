import { Component, OnInit } from '@angular/core';
import {GroupService} from "../../services/group.service";
import {GroupDetailDto, GroupDto, GroupListDto} from "../../dtos/group";
import {ToastrService} from "ngx-toastr";
import {ConfirmationService, MessageService} from "primeng/api";
import {AutoCompleteCompleteEvent, AutoCompleteSelectEvent} from "primeng/autocomplete";
import {UserService} from "../../services/user.service";
import {FriendshipService} from "../../services/friendship.service";
import {ActivatedRoute, Router} from "@angular/router";
import {AuthService} from "../../services/auth.service";
import {NgForm} from "@angular/forms";
import {Observable} from "rxjs";
import {group} from "@angular/animations";

@Component({
  selector: 'app-group-list',
  templateUrl: './group-list.component.html',
  styleUrl: './group-list.component.scss'
})
export class GroupListComponent implements OnInit {
  groups: GroupListDto[] = [];
  // map of group id to balance summary
  groupBalanceSummaries: { [p: string]: number } = {};
  responseReceived: boolean = false;

  // for the create group modal
  createNewGroupModalVisible: boolean;
  group: GroupDto = {
    groupName: '',
    members: []
  };
  filteredFriends: any[] | undefined;
  friends: any[] | undefined;

  filteredFriendsEdit: any[] | undefined;
  friendsEdit: any[] | undefined;

  protected membersEmails: string[] = [];
  protected membersEmailsEdit: string[] = [];
  currentlySelected: any;

  constructor(
    private groupService: GroupService,
    private messageService: MessageService,
    protected userService: UserService,
    private friendshipService: FriendshipService,
    private router: Router,
    private route: ActivatedRoute,
    protected authService: AuthService,
    private confirmationService: ConfirmationService,
) { }

  ngOnInit(): void {
    this.groupService.getGroupsWithDebtInfos().subscribe({
       next: data => {
         this.groups = data.sort((a, b) => a.groupName.localeCompare(b.groupName))
          this.groups.forEach(group => {
            this.groupBalanceSummaries[group.id] = this.calculateBalanceSummary(group.membersDebts);
          });
         this.responseReceived = true;
       },
      error: error => {
        if (error && error.error && error.error.errors) {
          for (let i = 0; i < error.error.errors.length; i++) {
            this.messageService.add({severity:'error', summary:'Error', detail:`${error.error.errors[i]}`});
          }
        } else if (error && error.error && error.error.message) {
          this.messageService.add({severity:'error', summary:'Error', detail:`${error.error.message}`});
        } else if (error && error.error && error.error.detail) {
          this.messageService.add({severity:'error', summary:'Error', detail:`${error.error.detail}`});
        } else {
          console.error('Error getting group', error);
          this.messageService.add({severity:'error', summary:'Error', detail:`Getting group did not work!`});
        }
      }
    });
  }

  protected calculateBalanceSummary(membersDebts: { [p: string]: number }
  ): number {
    let balanceSummary = 0;
    for (let membersDebtsKey in membersDebts) {
      let memberDebt = membersDebts[membersDebtsKey];
      if (memberDebt) {
        balanceSummary += memberDebt;
      }
    }
    return balanceSummary;
  }

  // Methods for the create group modal
  private createGroupModalNgOnInit() {
    this.friendshipService.getFriends().subscribe({
      next: data => {
        this.friends = data;
        this.friends.sort((a, b) => a.localeCompare(b));
      },
      error: error => {
        if (error && error.error && error.error.errors) {
          for (let i = 0; i < error.error.errors.length; i++) {
            this.messageService.add({severity:'error', summary:'Error', detail:`${error.error.errors[i]}`});
          }
        } else if (error && error.error && error.error.message) {
          this.messageService.add({severity:'error', summary:'Error', detail:`${error.error.message}`});
        } else if (error && error.error && error.error.detail) {
          this.messageService.add({severity:'error', summary:'Error', detail:`${error.error.detail}`});
        } else {
          console.error('Error getting friends', error);
          this.messageService.add({severity:'error', summary:'Error', detail:`Getting friends did not work!`});
        }
      }
    });
  }

  public onSubmitModal(form: NgForm): void {
    var memberGroupSaved = JSON.parse(JSON.stringify(this.group.members));

    if (form.valid) {
      let observable: Observable<GroupDto>;


      this.membersEmails.forEach(member => {
        this.group.members.push(member)
      });

      observable = this.groupService.create(this.group);

      console.log("final: ")
      console.log(this.group.members)
      observable.subscribe({
        next: data => {
          this.messageService.add({severity:'success', summary:'Success', detail:`Group ${this.group.groupName} successfully created`});
          if (this.group.id) {
            this.router.navigate(['/group/' + this.group.id]);
          } else if (data.id) {
            this.router.navigate(['/group/' + data.id]);
          } else {
            this.router.navigate(['/home/groups']);
          }
        },
        error: error => {
          this.group.members = memberGroupSaved;
          console.log(error);
          if (error && error.error && error.error.errors) {
            //this.notification.error(`${error.error.errors.join('. \n')}`);
            for (let i = 0; i < error.error.errors.length; i++) {
              this.messageService.add({severity:'error', summary:'Error', detail:`${error.error.errors[i]}`});
            }
          } else if (error && error.error && error.error.message) { // if no detailed error explanation exists. Give a more general one if available.
            this.messageService.add({severity:'error', summary:'Error', detail:`${error.error.message}`});
          } else if (error && error.error.detail) {
            this.messageService.add({severity:'error', summary:'Error', detail:`${error.error.detail}`});
          } else {
            console.error('Error creating group', error);
            this.messageService.add({severity:'error', summary:'Error', detail:`Creation of group did not work!`});
          }
        }
      });
    }
  }
  public addMember(member: AutoCompleteSelectEvent) {
    setTimeout(() => {
      this.currentlySelected = ""
    });

    if (!member.value) return;
    if (this.membersEmails.includes(member.value)) {
      this.messageService.add({
        severity: 'error',
        summary: 'Error',
        detail: `${member.value} is already in participant list`
      });
      return;
    }
    this.membersEmails.push(member.value);
  }


  public removeMember(index: number) {
    if (this.authService.getEmail() == this.membersEmails[index]) {
      this.messageService.add({severity:'error', summary:'Error', detail:`You can't remove yourself from the group.`});
      return;
    }

    this.membersEmails.splice(index, 1);
  }


  filterMembers(event: AutoCompleteCompleteEvent) {

    let filtered: any[] = [];
    let query = event.query;

    for (let i = 0; i < (this.friends as any[]).length; i++) {
      let friend = (this.friends as any[])[i];
      if (friend.toLowerCase().indexOf(query.toLowerCase()) == 0) {
        filtered.push(friend);
      }
    }

    this.filteredFriends = filtered;
  }

  getMembersEmail(): string[] {
    return this.membersEmails;
  }

  getSortedMembersEmail(): string[] {
    return this.membersEmails.sort((a, b) => a.localeCompare(b));
  }

  getSortedGroupMembersEmail(): string[] {
    return this.group.members.sort((a, b) => a.localeCompare(b));
  }

  createNewGroupModalOpen() {
    this.createGroupModalNgOnInit();

    this.group.members = [];
    this.group.groupName = undefined;
    this.membersEmails = [];
    this.membersEmails.push(this.authService.getEmail());
    this.createNewGroupModalVisible = true
  }
  goBack($event: MouseEvent) {
    this.group.members = [];
    this.group.groupName = undefined;
    this.createNewGroupModalVisible = false;

    // this.confirmationService.confirm({
    //   message: 'Are you sure you want to cancel the creation of the group ?',
    //   header: 'Confirm',
    //   icon: 'pi pi-exclamation-triangle',
    //   accept: () => {
    //       this.messageService.add({ severity: 'info', summary: 'Confirmed', detail: 'Group creation canceled' });
    //       this.group.members = [];
    //       this.group.groupName = undefined;
    //       this.createNewGroupModalVisible = false;
    //   }
    // });
  }
}
