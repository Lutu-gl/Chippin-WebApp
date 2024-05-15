import { Component, Input, OnInit } from '@angular/core';
import { NgForm, NgModel } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { Observable, of } from 'rxjs';
import { Category } from 'src/app/dtos/category';
import { ExpenseCreateDto } from 'src/app/dtos/expense';
import { GroupDto } from 'src/app/dtos/group';
import { UserSelection } from 'src/app/dtos/user';
import { ExpenseService } from 'src/app/services/expense.service';
import { GroupService } from 'src/app/services/group.service';
import { UserService } from 'src/app/services/user.service';

export enum ExpenseCreateEditMode {
  create,
  edit,
  info
}

interface ExpenseParticipant {
  name: string,
  isParticipating: boolean,
  percentage: number
}

@Component({
  selector: 'app-expense-create',
  templateUrl: './expense-create.component.html',
  styleUrl: './expense-create.component.scss'
})
export class ExpenseCreateComponent implements OnInit {

  mode: ExpenseCreateEditMode = ExpenseCreateEditMode.create;
  expense: ExpenseCreateDto = {
    name: undefined,
    category: undefined,
    amount: undefined,
    payerEmail: undefined,
    groupId: undefined,
    participants: undefined
  };
  group: GroupDto = {
    groupName: '',
    members: []
  };
  members: ExpenseParticipant[] = new Array(0);
  dummyCategorySelectionModel: unknown;
  dummyGroupSelectionModel: unknown; // Just needed for the autocomplete
  dummyPayerSelectionModel: unknown;

  constructor(
    private userService: UserService,
    private groupService: GroupService,
    private expenseService: ExpenseService,
    private route: ActivatedRoute,
    private notification: ToastrService
  ) {
  }


  ngOnInit(): void {
    this.route.data.subscribe(data => {
      this.mode = data.mode;
      if (this.mode === ExpenseCreateEditMode.create) {
        this.prepareGroupOnCreate();
      } else if (this.mode === ExpenseCreateEditMode.info) {
        this.prepareWholeExpense();
      }
    
    });
    
  }

  private prepareGroupOnCreate(): void {
    const groupId = Number(this.route.snapshot.paramMap.get('id'));
    if (groupId) {
      this.groupService.getById(groupId).subscribe({
        next: data => {
          this.group = data;
          this.members = this.group.members.map(member => ({
            name: member.email,
            isParticipating: true,
            percentage: 100 / this.group.members.length
          }));
          this.dummyGroupSelectionModel = this.group;
        },
        error: error => {
          console.error(error);
          this.notification.error("Could not get members of selected group!");
        }
      })
    }
  }

  private prepareWholeExpense(): void {
    const expenseId = Number(this.route.snapshot.paramMap.get('id'));
    if (expenseId) {
      this.expenseService.getExpenseById(expenseId).subscribe({
        next: data => {
          this.expense.name = data.name;
          this.expense.category = data.category;
          this.expense.amount = data.amount;
          this.expense.payerEmail = data.payerEmail;
          this.expense.groupId = data.group.id;
          this.expense.participants = data.participants;
          
          this.group = data.group;

          this.dummyCategorySelectionModel = data.category;
          this.dummyPayerSelectionModel = data.payerEmail;
          this.dummyGroupSelectionModel = {onInit: true, ...data.group};

          this.members = data.group.members.map(member => {
            const formattedMember: ExpenseParticipant = { name: member.email, isParticipating: false, percentage: null }
            const percentage = data.participants[member.email];
            if (percentage) {
              formattedMember.isParticipating = true;
              formattedMember.percentage = percentage * 100;
            }
            return formattedMember;
          });
          
        },
        error: error => {
          console.error(error);
          this.notification.error("Could not get expense!");
        }
      })
    }
  }

  public get heading(): string {
    switch (this.mode) {
      case ExpenseCreateEditMode.create:
        return "Create new Expense";
      case ExpenseCreateEditMode.edit:
        return "Edit expense";
      case ExpenseCreateEditMode.info:
        return "Expense";
      default:
        return '?';
    }
  }

  public get submitButtonText(): string {
    switch (this.mode) {
      case ExpenseCreateEditMode.create:
        return 'Create';
      case ExpenseCreateEditMode.edit:
        return 'Edit';
      default:
        return '?';
    }
  }

  public modeIsInfo(): boolean {
    return this.mode === ExpenseCreateEditMode.info;
  }

  public modeIsCreate(): boolean {
    return this.mode === ExpenseCreateEditMode.create;
  }

  public formatMember(member: UserSelection | null): string {
    return !member
      ? ""
      : `${member.email}`
  }

  public dynamicCssClassesForInput(input: NgModel): any {
    return {
      'is-invalid': !input.valid && !input.pristine,
    };
  }

  public groupSelected(group: GroupDto) {
    if (group && group['onInit']) { // special case: do nothing if autocomplete was changed by onInit method in info/edit mode
      return;
    }

    if (!group || !group.id) {
      this.group = undefined;
      this.members = undefined;
      return;
    }

    this.groupService.getById(group.id).subscribe({
      next: data => {
        this.group = data;
        this.members = this.group.members.map(member => ({
          name: member.email,
          isParticipating: true,
          percentage: 100 / this.group.members.length
        }));
      },
      error: error => {
        console.error(error);
        this.notification.error("Could not get members of selected group!");
      }
    })
  }

  public categorySelected(category: Category) {
    if (!category) {
      this.expense.category = undefined;
    } else {
      this.expense.category = category;
    }
  }

  public payerSelected(payerEmail: string) {
    if (payerEmail) {
      this.expense.payerEmail = payerEmail;
    } else {
      this.expense.payerEmail = undefined;
    }
  }

  public onSubmit(form: NgForm): void {

    const participants = {};
    this.members.filter(member => member.isParticipating).forEach(member => participants[member.name] = member.percentage / 100);

    const submitExpense: ExpenseCreateDto = {
      name: this.expense.name,
      category: this.expense.category || null,
      amount: this.expense.amount,
      payerEmail: this.expense.payerEmail,
      groupId: this.group.id,
      participants: participants
    };

    this.expenseService.createExpense(submitExpense).subscribe({
      next: data => {
        this.notification.success("Created expense successfully!");
      },
      error: error => {
        console.error(error);
        this.notification.error("Could not create expense!");
      }
    })
  }

  groupSuggestions = (input: string): Observable<GroupDto[]> => 
    this.userService.getUserGroups();

  categorySuggestions = (input: string): Observable<Category[]> =>
    of(Object.values(Category));

  payerSuggestions = (input: string): Observable<string[]> =>
    of(this.group.members.map(member => member.email));
  

  public formatGroup(group: GroupDto | null): string {
    return !group
      ? ""
      : `${group.groupName}`
  }

  public formatCategory(category: Category | null): string {
    return !category ? "" : category;
  }

  public formatPayer(payer: string | null): string {
    return !payer ? "" : payer;
  }


}
