import {Component, OnInit} from '@angular/core';
import {GroupService} from "../../../services/group.service";
import {ActivatedRoute, Router} from "@angular/router";
import {ToastrService} from 'ngx-toastr';
import {NgForm, NgModel} from '@angular/forms';
import {GroupDto} from "../../../dtos/group";
import { BudgetDto } from '../../../dtos/budget';
import {UserSelection} from "../../../dtos/user";
import {map, Observable} from "rxjs";
import {UserService} from "../../../services/user.service";
import {FriendshipService} from "../../../services/friendship.service";


export enum GroupCreateEditMode {
  create,
  edit,
  info,
}

@Component({
  selector: 'app-group-create',
  templateUrl: './group-create.component.html',
  styleUrl: './group-create.component.scss'
})
export class GroupCreateComponent implements OnInit {
  mode: GroupCreateEditMode = GroupCreateEditMode.create;

  group: GroupDto = {
    groupName: '',
    members: []
  };
  members: (UserSelection | null)[] = new Array(0);
  dummyMemberSelectionModel: unknown; // Just needed for the autocomplete
  budgets: BudgetDto[] = [];

  constructor(
      private service: GroupService,
      protected userService: UserService,
      private friendshipService: FriendshipService,
      private router: Router,
      private route: ActivatedRoute,
      private notification: ToastrService,
  ) {
  }

  public get heading(): string {
    switch (this.mode) {
      case GroupCreateEditMode.create:
        return 'Create New Group';
      case GroupCreateEditMode.edit:
        return 'Edit Group';
      case GroupCreateEditMode.info:
        return 'Group Info';
      default:
        return '?';
    }
  }

  public get submitButtonText(): string {
    switch (this.mode) {
      case GroupCreateEditMode.create:
        return 'Create';
      case GroupCreateEditMode.edit:
        return 'Edit';
      case GroupCreateEditMode.info:
        return 'Edit this Group';
      default:
        return '?';
    }
  }

  get modeIsCreate(): boolean {
    return this.mode === GroupCreateEditMode.create;
  }

  get modeIsInfo(): boolean {
    return this.mode === GroupCreateEditMode.info;
  }

  private get modeActionFinished(): string {
    switch (this.mode) {
      case GroupCreateEditMode.create:
        return 'created';
      case GroupCreateEditMode.edit:
        return 'edited';
      default:
        return '?';
    }
  }

  ngOnInit(): void {
    this.route.data.subscribe(data => {
      this.mode = data.mode;
    });

    // add logged in user to group
    let emailString = this.userService.getUserEmail();
    if(emailString === null) {
      this.notification.error(`You need to be logged in to create a group. Please logout and login again.`);
      return;
    }

    let loggedInUser: UserSelection = {
      email: emailString
    };
    this.members.push(loggedInUser);

    if (!this.modeIsCreate) {
      this.getGroup();
      this.getGroupBudgets();
    }
  }
  getGroup(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    this.service.getById(id)
      .subscribe( {
        next: data => {
          this.group = data;
          this.members = data.members;
        },
        error: error => {
          if (error && error.error && error.error.errors) {
            for (let i = 0; i < error.error.errors.length; i++) {
              this.notification.error(`${error.error.errors[i]}`);
            }
          } else if (error && error.error && error.error.message) {
            this.notification.error(`${error.error.message}`);
          } else if (error && error.error && error.error.detail) {
            this.notification.error(`${error.error.detail}`);
          } else {
            console.error('Error getting group', error);
            this.notification.error(`Getting group did not work!`);
          }
        }
      })
  }


  getGroupBudgets(): void{
    const id = Number(this.route.snapshot.paramMap.get('id'));

    this.service.getGroupBudgets(id)
      .subscribe( {
        next: budgets => {
          this.budgets = budgets;
        },
        error: error => {
          if (error && error.error && error.error.errors) {
            for (let i = 0; i < error.error.errors.length; i++) {
              this.notification.error(`${error.error.errors[i]}`);
            }
          } else if (error && error.error && error.error.message) {
            this.notification.error(`${error.error.message}`);
          } else if (error && error.error && error.error.detail) {
            this.notification.error(`${error.error.detail}`);
          } else {
            console.error('Error getting group', error);
            this.notification.error(`Getting group did not work!`);
          }
        }
      })
  }

  // addBudget(): void {
  //   console.log(this.newBudget.name)
  //   console.log(this.newBudget.amount)
  //   if (this.newBudget.name && this.newBudget.amount > 0) {
  //     this.budgets.push(this.newBudget);
  //     this.newBudget = { name: '', amount: 0 };
  //   } else {
  //     this.notification.error('Please provide a valid budget name and amount.');
  //   }
  // }

  // removeBudget(index: number): void {
  //   //this.budgets.splice(index, 1);

  //   this.service.deleteBudget(this.group.id, this.budgets[index].id).subscribe({
  //     next: () => {
  //       this.budgets.splice(index, 1);
  //       this.notification.success('Budget successfully deleted.');
  //     },
  //     error: (err) => {
  //       console.error('Error deleting budget', err);
  //       this.notification.error('Failed to delete budget. Please try again.');
  //     }
  //   });

  // }


  public dynamicCssClassesForInput(input: NgModel): any {
    return {
      'is-invalid': !input.valid && !input.pristine,
    };
  }

  public onSubmit(form: NgForm): void {
    if (this.modeIsInfo) {
      //this.router.navigate([`groups/${(this.route.snapshot.paramMap.get('id'))}/edit`]);
      return;
    }
    if (form.valid) {
      this.group.members = this.members;

      let observable: Observable<GroupDto>;
      switch (this.mode) {
        case GroupCreateEditMode.create:
          observable = this.service.create(this.group);
          break;
        case GroupCreateEditMode.edit:
          observable = this.service.update(this.group);
          break;
        default:
          console.error('Unknown GroupCreateEditMode', this.mode);
          return;
      }
      observable.subscribe({
        next: data => {
          this.notification.success(`Group ${this.group.groupName} successfully ${this.modeActionFinished}.`);
          const groupId = data.id || this.group.id;
          this.budgets.forEach(budget => {
            if (budget.id) {
              console.log("Budget wird geupdated")
              this.service.updateBudget(groupId, budget.id, budget).subscribe();
            } else {
              console.log("Budget wird created")
              this.service.createBudget(groupId, budget).subscribe();
            }
          });


          this.router.navigate(['/groups']);
        },
        error: error => {
          console.log(error);
          if (error && error.error && error.error.errors) {
            //this.notification.error(`${error.error.errors.join('. \n')}`);
            for (let i = 0; i < error.error.errors.length; i++) {
              this.notification.error(`${error.error.errors[i]}`);
            }
          } else if (error && error.error && error.error.message) { // if no detailed error explanation exists. Give a more general one if available.
            this.notification.error(`${error.error.message}`);
          } else if (error && error.error.detail) {
            this.notification.error(`${error.error.detail}`);
          } else {
            switch (this.mode) {
              case GroupCreateEditMode.create:
                console.error('Error creating group', error);
                this.notification.error(`Creation of group did not work!`);
                break;
              case GroupCreateEditMode.edit:
                console.error('Error editing group', error);
                this.notification.error(`Edit of group did not work!`);
                break;
              default:
                console.error('Unknown GroupCreateEditMode. Operation did not work!', this.mode);
            }
          }
        }
      });
    }
  }

  public formatMember(member: UserSelection | null): string {
    return !member
      ? ""
      : `${member.email}`
  }

  public addMember(member: UserSelection | null) {
    if (!member)
      return;
    setTimeout(() => {
      const members = this.members;
      if (members.some(m => m?.email === member.email)) {
        this.notification.error(`${member.email} is already in participant list`, "Duplicate Participant");
        this.dummyMemberSelectionModel = null;
        return;
      }
      members.push(member);
      this.dummyMemberSelectionModel = null;
    });
  }

  memberSuggestions = (input: string): Observable<UserSelection[]> =>
    this.friendshipService.getFriends()
      .pipe(map(members => members.map((h) => ({
        email: h
      }))));

  public removeMember(index: number) {
    if(this.userService.getUserEmail() == this.members[index].email) {
      this.notification.error(`You can't remove yourself from the group.`);
      return;
    }

    this.members.splice(index, 1);
  }


  getSortedMembers(): UserSelection[] {
    return this.members.sort((a, b) => a.email.localeCompare(b.email));
  }


  protected readonly GroupCreateEditMode = GroupCreateEditMode;

  goBack() {
      this.router.navigate(['/group']);
  }
}
