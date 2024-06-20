import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {ConfirmationService, MessageService} from 'primeng/api';
import { AutoCompleteCompleteEvent } from 'primeng/autocomplete';
import { Category } from 'src/app/dtos/category';
import { ExpenseCreateDto } from 'src/app/dtos/expense';
import { GroupDto } from 'src/app/dtos/group';
import { ExpenseService } from 'src/app/services/expense.service';
import { GroupService } from 'src/app/services/group.service';
import { BudgetDto } from '../../../dtos/budget';

export enum ExpenseCreateEditMode {
  create,
  edit,
  info
}

@Component({
  selector: 'app-expense-create',
  templateUrl: './expense-create.component.html',
  styleUrl: './expense-create.component.scss'
})
export class ExpenseCreateComponent {

  @Input() mode: ExpenseCreateEditMode = ExpenseCreateEditMode.create;
  @Input() groupId!: number;
  @Input() expenseId?: number;
  @Output() closeDialog = new EventEmitter<void>();

  expenseName: string;
  expenseAmount: number;
  expenseDeleted = false;
  expenseArchived = false;
  group: GroupDto = { groupName: '', members: [] };
  members: any[] = [];
  budgets: BudgetDto[] = [];

  allCategories: any[] = Object.values(Category).map(category => ({name: category}));
  selectedCategory: any = { name: '' };
  filteredCategories: any[] = this.allCategories;

  allPayers: any[] = [];
  selectedPayer: any = { name: '' };
  filteredPayers: any[] = this.allPayers;

  isDeleteDialogVisible: boolean = false;

  imageUrl: string | ArrayBuffer | null = null;
  isDragging: boolean = false;
  selectedFile: File | null = null;

  constructor(
    private groupService: GroupService,
    private expenseService: ExpenseService,
    private confirmationService: ConfirmationService,
    private route: ActivatedRoute,
    private messageService: MessageService
  ) {
  }
  

  resetMode(): void {
    if (this.mode === ExpenseCreateEditMode.edit) {
      this.mode = ExpenseCreateEditMode.info;
    }
  }

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

  switchToEditMode(): void {
    this.mode = ExpenseCreateEditMode.edit;
  }

  openDeleteDialog(): void {
    this.confirmationService.confirm({
      message: 'Are you sure you want to delete this expense ?',
      header: 'Confirm',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.deleteExistingExpense()
      }
    });
    //this.isDeleteDialogVisible = true;
  }

  closeDeleteDialog(): void {
    this.isDeleteDialogVisible = false;
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

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file && this.isValidImage(file)) {
      const reader = new FileReader();
      reader.onload = () => {
        this.imageUrl = reader.result;
        this.selectedFile = file;
      };
      reader.readAsDataURL(file);
    } else {
      this.messageService.add({severity:'warn', summary:'Invalid File', detail:'Please upload a valid image file (PNG, JPG, GIF).'});
    }
  }

  onDragOver(event: DragEvent): void {
    if (!this.imageUrl) {
      event.preventDefault();
      this.isDragging = true;
    }
  }

  onDragLeave(event: DragEvent): void {
    if (!this.imageUrl) {
      event.preventDefault();
      this.isDragging = false;
    }
  }

  onDrop(event: DragEvent): void {
    if (!this.imageUrl) {
      event.preventDefault();
      this.isDragging = false;
      const file = event.dataTransfer?.files[0];
      if (file && this.isValidImage(file)) {
        const reader = new FileReader();
        reader.onload = () => {
          this.imageUrl = reader.result;
          this.selectedFile = file;
        };
        reader.readAsDataURL(file);
      } else {
        this.messageService.add({severity:'warn', summary:'Invalid File', detail:'Please upload a valid image file (PNG, JPG, GIF).'});
      }
    }
  }

  removeImage(): void {
    this.imageUrl = null;
    this.selectedFile = null;
  }

  isValidImage(file: File): boolean {
    return file.type.startsWith('image/');
  }


  customInit(changes: any): void {

    console.log("CUSTOM INIT!");
    console.log(changes);

    this.groupId = changes.groupId;
    this.expenseId = changes.expenseId;

    console.log("REACHES HERE!");
    if (changes.mode === ExpenseCreateEditMode.create) {
      this.prepareGroupOnCreate();
    } else if (changes.mode === ExpenseCreateEditMode.info || changes.mode === ExpenseCreateEditMode.edit) {
      this.prepareWholeExpense();
    }


    // if (this.mode === ExpenseCreateEditMode.create) {
    //   this.prepareGroupOnCreate();
    // } else if (this.mode === ExpenseCreateEditMode.info || this.mode === ExpenseCreateEditMode.edit) {
    //   this.prepareWholeExpense();
    // }
  }

  private prepareGroupOnCreate(): void {
    console.log("prepareGroupOnCreate")

    this.expenseDeleted = false;
    this.expenseArchived = false;
    this.expenseName = undefined;
    this.expenseAmount = undefined;
    this.selectedCategory = { name: '' };
    this.selectedPayer = { name: '' };
    this.imageUrl = null;
    this.selectedFile = null;

    const groupId = Number(this.route.snapshot.paramMap.get('id'));
    if (groupId) {
      this.groupService.getById(groupId).subscribe({
        next: data => {
          this.expenseDeleted = false;
          this.expenseName = undefined;
          this.expenseAmount = undefined;
          this.selectedCategory = { name: '' };
          this.selectedPayer = { name: '' };
          this.imageUrl = null;
          this.selectedFile = null;
          this.group = data;
          this.members = this.group.members
            .map(member => ({email: member, isParticipating: true, amount: undefined}))
            .sort((a, b) => a.email.localeCompare(b.email));
          this.allPayers = this.group.members.map(member => ({name: member}))
            .sort((a, b) => a.name.localeCompare(b.name));
        },
        error: error => {
          console.error(error);
          this.messageService.add({severity:'error', summary:'Error', detail:'Could not find group!'});
          this.closeDialog.emit();
        }
      })
    }
  }

  private prepareWholeExpense(): void {

    if (!this.expenseId) {
      return;
    }

    this.expenseDeleted = false;
    this.expenseArchived = false;
    this.expenseName = undefined;
    this.expenseAmount = undefined;
    this.selectedCategory = { name: '' };
    this.selectedPayer = { name: '' };
    this.imageUrl = null;
    this.selectedFile = null;

    this.expenseService.getExpenseById(this.expenseId).subscribe({
      next: data => {
        this.group = data.group;
        this.expenseName = data.name;
        this.expenseAmount = data.amount;
        this.selectedCategory = {name: data.category};
        this.selectedPayer = {name: data.payerEmail};
        this.expenseDeleted = data.deleted;
        this.expenseArchived = data.archived;
        this.members = Object.entries(data.participants)
          .map(([email, percentage]) => ({
            email: email,
            isParticipating: this.roundDownTo2Decimals(percentage) > 0,
            amount: this.roundDownTo2Decimals(percentage * data.amount) || undefined
          }))
          .sort((a, b) => a.email.localeCompare(b.email));
        this.allPayers = this.group.members.map(member => ({name: member["email"]}))
          .sort((a, b) => a.name.localeCompare(b.name));

        if (data.containsBill) {
          this.expenseService.getExpenseBillById(this.expenseId).subscribe({
            next: blob => {
              const reader = new FileReader();
              reader.onload = () => {
                if (this.mode !== ExpenseCreateEditMode.create) {
                  this.imageUrl = reader.result;
                  this.selectedFile = new File([blob], 'bill.png', {type: 'image/png'});
                }
              };
              reader.readAsDataURL(blob);
            },
            error: error => {
              console.error(error);
              this.messageService.add({severity:'error', summary:'Error', detail:'Could not get expense bill!'});
            }
          });
        }

      },
      error: error => {
        console.error(error);
        this.messageService.add({severity:'error', summary:'Error', detail:'Could not get expense!'});
        this.closeDialog.emit();
      }
    })

  }

  getGroupBudgets(): BudgetDto[] {
    const id = this.groupId;
    this.groupService.getGroupBudgets(id)
      .subscribe(budgets => {
        this.budgets =  budgets;
      }, error => {
        console.error('Failed to load budgets', error);
      });
      return [];
  }

  // checkIfExceedsBudget(): Promise<boolean> {
  //   console.log("checkIfExceedsBudget")
  //   console.log(this.groupId);
  //   try {
  //     let budgets = this.getGroupBudgets();
  //     console.log(budgets)
  //     return null; // oder return true/false basierend auf Ihrer Logik
  //   } catch (error) {
  //     console.error('Failed to get budgets', error);
  //     return false;
  //   }
  // }

  submitValidation(): boolean {
    let returnValue = true;
    if (!this.expenseName || /^[a-zA-Z][a-zA-Z0-9 ]{0,254}$/.test(this.expenseName) === false) {
      this.messageService.add({severity:'warn', summary:'Invalid Expense', detail:'Name must be between 1 and 255 characters long, start with a letter and only contain letters, numbers and spaces!'});
      returnValue = false;
    }

    if (!this.expenseAmount || this.expenseAmount < 0.01 || this.expenseAmount > 9999999) {
      this.messageService.add({severity:'warn', summary:'Invalid Expense', detail:'Amount must be between 0.01 and 9999999!'});
      returnValue = false;
    }

    if (!this.selectedCategory) {
      this.selectedCategory = {name: "Other"};
    }

    if (!this.selectedPayer || !this.selectedPayer.name) {
      this.messageService.add({severity:'warn', summary:'Invalid Expense', detail:'Payer must be selected!'});
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

  public finishSubmit(): void {
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
        }, {}),
      bill: this.selectedFile
    };

    // const participant = Object.keys(submitExpense.participants)[0];
    // submitExpense.participants[participant] = this.roundDownTo2Decimals(submitExpense.participants[participant] + (1 - Object.values(submitExpense.participants).reduce((a, b) => a + b, 0)));

    if (this.mode === ExpenseCreateEditMode.create) {
      this.createNewExpense(submitExpense);
    } else if (this.mode === ExpenseCreateEditMode.edit) {
      this.editExistingExpense(this.expenseId, submitExpense);
    }

  }


  public onSubmit(): void {

    if (!this.submitValidation()) {
      return;
    }

    this.groupService.getGroupBudgets(this.groupId)
    .subscribe(budgets => {
      // console.log(budgets)
      this.budgets =  budgets;

      for(let budget of this.budgets){
        if(this.selectedCategory != null && budget.category != this.selectedCategory.name){
          continue;
        }else{
          if(budget.alreadySpent + this.expenseAmount > budget.amount){
            this.messageService.add({severity:'warn', summary:'Budget Warning', detail:`Limit of Budget ${budget.name} has been exeeded` });
          }
        }
      }

      this.finishSubmit();

    }, error => {
      this.messageService.add({severity:'warn', summary:'Budget Warning', detail:`Error loading the budgets` });
    });

  }

  private createNewExpense(expense: ExpenseCreateDto): void {
    this.expenseService.createExpense(expense).subscribe({
      next: data => {
        this.messageService.add({severity:'success', summary:'Success', detail:'Expense created  successfully!'});
        this.expenseName = undefined;
        this.expenseAmount = undefined;
        this.selectedCategory = {name: ''};
        this.selectedPayer = {name: ''};
        this.members = this.group.members.map(member => ({email: member, isParticipating: true, amount: undefined}));
        this.closeDialog.emit();
      },
      error: error => {
        this.printError(error);
      }
    });
  }

  private editExistingExpense(expenseId: number, expense: ExpenseCreateDto): void {
    this.expenseService.updateExpense(expenseId, expense).subscribe({
      next: data => {
        this.messageService.add({severity:'success', summary:'Success', detail:'Expense successfully updated!'});
        this.mode = ExpenseCreateEditMode.info;
        this.closeDialog.emit();
      },
      error: error => {
        this.printError(error);
      }
    })
  }

  public deleteExistingExpense(): void {
    this.expenseService.deleteExpense(this.expenseId).subscribe({
      next: data => {
        this.messageService.add({severity:'success', summary:'Success', detail:'Expense successfully deleted!'});
        this.expenseDeleted = true;
        this.closeDeleteDialog();
        this.closeDialog.emit();
      },
      error: error => {
        this.closeDeleteDialog();
        this.printError(error);
      }
    })
  }

  public recoverDeletedExpense(): void {
    this.expenseService.recoverExpense(this.expenseId).subscribe({
      next: data => {
        this.messageService.add({severity:'success', summary:'Success', detail:'Expense successfully recovered!'});
        this.expenseDeleted = false;
        this.closeDialog.emit();
      },
      error: error => {
        this.printError(error);
      }
    })
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

  public modeIsInfo(): boolean {
    return this.mode === ExpenseCreateEditMode.info;
  }

  public modeIsCreate(): boolean {
    return this.mode === ExpenseCreateEditMode.create;
  }

  public modeIsEdit(): boolean {
    return this.mode === ExpenseCreateEditMode.edit;
  }

  public expenseIsDeleted(): boolean {
    return this.expenseDeleted === true;
  }

  public expenseIsArchived(): boolean {
    return this.expenseArchived === true;
  }
}
