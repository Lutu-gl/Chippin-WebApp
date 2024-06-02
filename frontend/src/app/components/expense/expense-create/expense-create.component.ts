import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { NgForm, NgModel } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { MessageService } from 'primeng/api';
import { AutoCompleteCompleteEvent } from 'primeng/autocomplete';
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

  @Input() mode: ExpenseCreateEditMode = ExpenseCreateEditMode.create;
  @Input() groupId!: number;
  @Output() closeDialog = new EventEmitter<void>();

  expenseName: string;
  expenseAmount: number;
  group: GroupDto = { groupName: '', members: [] };
  members: any[] = [];

  allCategories: any[] = Object.values(Category).map(category => ({name: category}));
  selectedCategory: any;
  filteredCategories: any[] = this.allCategories;
  
  allPayers: any[] = [];
  selectedPayer: any;
  filteredPayers: any[] = this.allPayers;
  
  filterCategory(event: AutoCompleteCompleteEvent) {
    let query = event.query;

    this.filteredCategories = this.allCategories.filter(category => {
      return category.name.toLowerCase().includes(query.toLowerCase());
    });
  }

  filterPayer(event: AutoCompleteCompleteEvent) {
    let query = event.query;

    this.filteredPayers = this.allPayers.filter(payer => {
      return payer.name.toLowerCase().includes(query.toLowerCase());
    });
  }

  roundDownTo2Decimals(value: number): number {
    return Math.round((value + Number.EPSILON) * 100) / 100
  }

  compareFloats(a: number, b: number): boolean {
    return Math.abs(a - b) < 0.00001;
  }

  expenseAmountChange(event: any) {
    const activeMembers = this.members.filter(member => member.isParticipating)
    const amountActiveMembers = activeMembers.length;
    if (amountActiveMembers === 0 || !this.expenseAmount) {
      return;
    }
    
    if (this.expenseAmount > 9999999) {
      this.expenseAmount = 9999999.99;
    }

    this.members.forEach(member => {
      if (member.isParticipating) {
        member.amount = this.roundDownTo2Decimals(this.expenseAmount / amountActiveMembers);
      } else {
        member.amount = undefined;
      }
    });

    const rest = this.roundDownTo2Decimals(this.expenseAmount - this.roundDownTo2Decimals(activeMembers.map(m => m.amount).reduce((a, b) => a + b, 0)));
    activeMembers[0].amount += rest;
  }


  ngOnInit(): void {
    if (this.mode === ExpenseCreateEditMode.create) {
      this.prepareGroupOnCreate();
    } else if (this.mode === ExpenseCreateEditMode.info || this.mode === ExpenseCreateEditMode.edit) {
      this.prepareWholeExpense();
    }
  }

  private prepareGroupOnCreate(): void {
    const groupId = Number(this.route.snapshot.paramMap.get('id'));
    if (groupId) {
      this.groupService.getById(groupId).subscribe({
        next: data => {
          this.group = data;
          this.members = this.group.members.map(member => ({email: member, isParticipating: true, amount: undefined}));
          this.allPayers = this.group.members.map(member => ({name: member}));
        },
        error: error => {
          console.error(error);
          this.messageService.add({severity:'error', summary:'Error', detail:'Could not find group!'});
          this.router.navigate(['/home', 'groups']);
        }
      })
    }
  }

  submitValidation(): boolean {
    let returnValue = true;
    if (!this.expenseName || /^[a-zA-Z][a-zA-Z0-9 ]{0,254}$/.test(this.expenseName) === false) {
      this.messageService.add({severity:'warn', summary:'Invalid Expense', detail:'Name must be between 1 and 255 characters long and only contain letters, numbers and spaces!'});
      returnValue = false;
    }

    if (!this.expenseAmount || this.expenseAmount < 0.01 || this.expenseAmount > 9999999) {
      this.messageService.add({severity:'warn', summary:'Invalid Expense', detail:'Amount must be between 0.01 and 9999999!'});
      returnValue = false;
    }

    if (!this.selectedPayer || !this.selectedPayer.name || !this.group.members.includes(this.selectedPayer.name)) {
      this.messageService.add({severity:'warn', summary:'Invalid Expense', detail:'Payer must be selected and a member of the group!'});
      returnValue = false;
    }

    if (!this.compareFloats(this.members.filter(m => m.isParticipating).map(m => m.amount).reduce((a, b) => a + b, 0), this.expenseAmount)) {
      this.messageService.add({severity:'warn', summary:'Invalid Expense', detail:'The sum of the amounts of the participating members must be equal to the total amount!'});
      returnValue = false;
    }

    if (this.members.filter(m => m.isParticipating && m.email !== this.selectedPayer?.name).length < 1) {
      this.messageService.add({severity:'warn', summary:'Invalid Expense', detail:'At least one member, which is not the payer, must be participating!'});
      returnValue = false;
    }

    return returnValue;
  }

  public onSubmit(): void {

    if (!this.submitValidation()) {
      return;
    }

    const submitExpense: ExpenseCreateDto = {
      name: this.expenseName,
      category: this.selectedCategory?.name || null,
      amount: this.expenseAmount,
      payerEmail: this.selectedPayer.name,
      groupId: this.group.id,
      participants: this.members
        .filter(m => m.isParticipating)
        .reduce((acc, m) => {
          acc[m.email] = m.amount / this.expenseAmount;
          return acc;
        }, {})
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
        this.messageService.add({severity:'success', summary:'Success', detail:'Created expense successfully!'});
        this.closeDialog.emit();
      },
      error: error => {
        this.printError(error);
      }
    });
  }


  private printError(error): void {
    if (error && error.error && error.error.errors) {
      for (let i = 0; i < error.error.errors.length; i++) {
        this.messageService.add({severity:'error', summary:'Error', detail: error.error.errors[i] });
      }
    } else if (error && error.error && error.error.message) { // if no detailed error explanation exists. Give a more general one if available.
      this.messageService.add({severity:'error', summary:'Error', detail: error.error.message });
    } else if (error && error.error.detail) {
      this.messageService.add({severity:'error', summary:'Error', detail: error.error.detail });
    } else if (error && error.error) {
      this.messageService.add({severity:'error', summary:'Error', detail: error.error });
    } else {
      switch (this.mode) {
        case ExpenseCreateEditMode.create:
          console.error('Error making expense', error);
          this.messageService.add({severity:'error', summary:'Error', detail:'Creation of expense did not work!'});
          break;
        case ExpenseCreateEditMode.edit:
          console.error('Error editing expense', error);
          this.messageService.add({severity:'error', summary:'Error', detail:'Edit of expense did not work!'});
          break;
        case ExpenseCreateEditMode.info:
          console.error('Error on expense', error);
          this.messageService.add({severity:'error', summary:'Error', detail:'Operation on expense did not work!'});
        default:
          console.error('Unknown ExpenseCreateEditMode. Operation did not work!', this.mode);
      }
    }
  }


  // OLD CODE
  
  splitMode: SplitMode = SplitMode.percentage;
  expense: ExpenseCreateDto = {
    name: undefined,
    category: undefined,
    amount: undefined,
    payerEmail: undefined,
    groupId: undefined,
    participants: undefined
  };
  
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
    private notification: ToastrService,
    private messageService: MessageService
  ) {
  }


  // ngOnInit(): void {
  //   this.route.data.subscribe(data => {
  //     this.mode = data.mode;
  //     if (this.mode === ExpenseCreateEditMode.create) {
  //       this.prepareGroupOnCreate();
  //     } else if (this.mode === ExpenseCreateEditMode.info || this.mode === ExpenseCreateEditMode.edit) {
  //       this.prepareWholeExpense();
  //     }

  //   });

  // }

  

  private prepareWholeExpense(): void {
    const expenseId = Number(this.route.snapshot.paramMap.get('id'));
    this.expenseId = expenseId;
    if (expenseId) {
      this.expenseService.getExpenseById(expenseId).subscribe({
        next: data => {
          //console.log(data);
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
            const formattedMember: ExpenseParticipant = { name: member, isParticipating: false, percentage: null }
            const percentage = data.participants[member];
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
    this.members.forEach(member => member.percentage = parseFloat(((member.percentage / this.expense.amount) * 100).toFixed(2)));
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
    this.members.forEach(member => member.percentage = parseFloat((this.expense.amount * (member.percentage / 100)).toFixed(2)));
    this.members[0].percentage = parseFloat((this.members[0].percentage + this.expense.amount - this.members.map(u => u.percentage).reduce((a,b) => a+b, 0)).toFixed(2));

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
          name: member,
          isParticipating: true,
          percentage: parseFloat((100 / this.group.members.length).toFixed(2))
        }));
        this.members[0].percentage += 100 - this.members.map(u => u.percentage).reduce((a,b) => a+b, 0);
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

  

  groupSuggestions = (input: string): Observable<GroupDto[]> =>
    this.userService.getUserGroups();

  categorySuggestions = (input: string): Observable<Category[]> =>
    of(Object.values(Category));

  payerSuggestions = (input: string): Observable<string[]> =>
    of(this.group.members.map(member => member));


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
