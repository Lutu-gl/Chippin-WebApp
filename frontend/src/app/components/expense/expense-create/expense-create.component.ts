import { Component, OnInit } from '@angular/core';
import { NgForm, NgModel } from '@angular/forms';
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
  edit
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
    private notification: ToastrService
  ) {
  }


  ngOnInit(): void {
    
  }


  public get heading(): string {
    switch (this.mode) {
      case ExpenseCreateEditMode.create:
        return "Create new Expense";
      case ExpenseCreateEditMode.edit:
        return "Edit expense";
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

    console.log(JSON.stringify(submitExpense));
    console.log(submitExpense);

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
