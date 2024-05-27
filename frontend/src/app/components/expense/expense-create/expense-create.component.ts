import { Component, Input, OnInit } from '@angular/core';
import { NgForm, NgModel } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
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

enum SplitMode {
  percentage,
  amount
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
  splitMode: SplitMode = SplitMode.percentage;
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

  expenseId: number; // only set in info and edit mode
  expenseDeleted = false; // only set to true if expense is marked as deleted

  constructor(
    private userService: UserService,
    private groupService: GroupService,
    private expenseService: ExpenseService,
    private route: ActivatedRoute,
    private router: Router,
    private notification: ToastrService
  ) {
  }


  ngOnInit(): void {
    this.route.data.subscribe(data => {
      this.mode = data.mode;
      if (this.mode === ExpenseCreateEditMode.create) {
        this.prepareGroupOnCreate();
      } else if (this.mode === ExpenseCreateEditMode.info || this.mode === ExpenseCreateEditMode.edit) {
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
    this.expenseId = expenseId;
    if (expenseId) {
      this.expenseService.getExpenseById(expenseId).subscribe({
        next: data => {
          console.log(data);
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

          this.expenseDeleted = data.deleted;
          
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

  public expenseIsDeleted(): boolean {
    return this.expenseDeleted === true;
  }

  public splitModeIsPercentage(): boolean {
    return this.splitMode === SplitMode.percentage;
  }

  public splitModeIsAmount(): boolean {
    return this.splitMode === SplitMode.amount;
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

  public changeToPercentageMode(event: Event): void {
    event.preventDefault();
    if (this.splitModeIsPercentage()) {
      return;
    }
    this.splitMode = SplitMode.percentage;
    if (!this.expense.amount) {
      return;
    }
    this.members.forEach(member => member.percentage = (member.percentage / this.expense.amount) * 100);
  }

  public changeToAmountMode(event: Event): void {
    event.preventDefault();
    if (this.splitModeIsAmount()) {
      return;
    }
    this.splitMode = SplitMode.amount;
    if (!this.expense.amount) {
      return;
    }
    this.members.forEach(member => member.percentage = this.expense.amount * (member.percentage / 100));
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
    if (this.splitModeIsPercentage()) {
      this.members.filter(member => member.isParticipating).forEach(member => participants[member.name] = member.percentage / 100);
    } else if (this.splitModeIsAmount()) {
      this.members.filter(member => member.isParticipating).forEach(member => participants[member.name] = member.percentage / this.expense.amount);
    }

    const submitExpense: ExpenseCreateDto = {
      name: this.expense.name,
      category: this.expense.category || null,
      amount: this.expense.amount,
      payerEmail: this.expense.payerEmail,
      groupId: this.group.id,
      participants: participants
    };

    if (this.mode === ExpenseCreateEditMode.create) {
      this.createNewExpense(submitExpense);
    } else if (this.mode === ExpenseCreateEditMode.edit) {
      this.editExistingExpense(this.expenseId, submitExpense);
    }
    
  }

  private createNewExpense(expense: ExpenseCreateDto): void {
    this.expenseService.createExpense(expense).subscribe({
      next: data => {
        this.notification.success("Created expense successfully!");
        this.router.navigate(["/group", data.groupId]);
      },
      error: error => {
        this.printError(error);
      }
    });
  }

  private editExistingExpense(expenseId: number, expense: ExpenseCreateDto): void {
    this.expenseService.updateExpense(expenseId, expense).subscribe({
      next: data => {
        this.notification.success("Edited expense successfully!");
        this.router.navigate(['/expenses', 'info', expenseId]);
      },
      error: error => {
        this.printError(error);
      }
    })
  }

  public deleteExistingExpense(): void {
    this.expenseService.deleteExpense(this.expenseId).subscribe({
      next: data => {
        this.notification.success("Deleted expense successfully!");
        this.router.navigate(['/group', this.expense.groupId]);
      },
      error: error => {
        this.printError(error);
      }
    })
  }

  public recoverDeletedExpense(): void {
    this.expenseService.recoverExpense(this.expenseId).subscribe({
      next: data => {
        this.notification.success("Recovered expense successfully!");
        this.expense = data;
        this.expenseDeleted = false;
      },
      error: error => {
        this.printError(error);
      }
    })
  }

  private printError(error): void {
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
    } else if (error && error.error) {
      this.notification.error(`${error.error}`);
    } else {
      switch (this.mode) {
        case ExpenseCreateEditMode.create:
          console.error('Error making expense', error);
          this.notification.error(`Creation of expense did not work!`);
          break;
        case ExpenseCreateEditMode.edit:
          console.error('Error editing expense', error);
          this.notification.error(`Edit of expense did not work!`);
          break;
        case ExpenseCreateEditMode.info:
          console.error('Error on expense', error);
          this.notification.error('Operation on expense did not work!');
        default:
          console.error('Unknown ExpenseCreateEditMode. Operation did not work!', this.mode);
      }
    }
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
