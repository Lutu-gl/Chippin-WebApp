import { Component, EventEmitter, Input, OnChanges, Output } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { Category } from '../../../dtos/category';
import { BudgetDto } from '../../../dtos/budget';
import { GroupService } from '../../../services/group.service';
import { AutoCompleteCompleteEvent } from 'primeng/autocomplete';
import { ResetFrequency } from 'src/app/dtos/ResetFrequency';
import {ConfirmationService, MenuItem} from 'primeng/api';
export enum BudgetCreateEditMode {
  create,
  edit,
  info
}

@Component({
  selector: 'app-budget-create',
  templateUrl: './budget-create.component.html',
  styleUrls: ['./budget-create.component.scss']
})
export class BudgetCreateComponent implements OnChanges {
  @Input() mode: BudgetCreateEditMode;
  @Input() groupId!: number;
  @Input() budgetId?: number;
  @Output() closeDialog = new EventEmitter<void>();

  newBudget: BudgetDto = { name: '', amount: undefined, category: '', alreadySpent: 0, resetFrequency: '' };
  loadedBudget: BudgetDto;
  categories2: { label: string, value: Category }[] = [];
  selectedCategory: any;
  allCategories: any[] = Object.values(Category).map(category => ({name: category}));
  filteredCategories: any[] = this.allCategories;
  isDeleteDialogVisible: boolean = false;
  selectedFrequency: ResetFrequency;
  ResetFrequency = ResetFrequency;

  constructor(
    private groupService: GroupService,
    private route: ActivatedRoute,
    private router: Router,
    private confirmationService: ConfirmationService,
    private messageService: MessageService
  ) {
    this.categories2 = Object.keys(Category).map(key => ({ label: key, value: Category[key] }));
  }

  filterCategory(event: AutoCompleteCompleteEvent) {
    let query = event.query;

    this.filteredCategories = this.allCategories.filter(category => {
      return category.name.toLowerCase().includes(query.toLowerCase());
    });
  }

  ngOnChanges(): void {
    if (this.mode === BudgetCreateEditMode.create) {
      this.prepareNewBudget();
    } else if (this.mode === BudgetCreateEditMode.info || this.mode === BudgetCreateEditMode.edit) {
      this.prepareBudget();
    }
  }

  private prepareNewBudget(): void {
    this.newBudget = { name: '', amount: undefined, category: '', alreadySpent: 0, resetFrequency: ResetFrequency.MONTHLY};
    this.selectedCategory = { label: '', value: '' };
  }

  openDeleteDialog(): void {
    this.confirmationService.confirm({
      message: 'Are you sure you want to delete this Budget ?',
      header: 'Confirm',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.deleteExistingBudget()
      }
    });
  }

  closeDeleteDialog(): void {
    this.isDeleteDialogVisible = false;
    this.resetState();
  }

  private prepareBudget(): void {
    if (!this.budgetId) {
      return;
    }

    this.groupService.getByBudgetId(this.groupId, this.budgetId).subscribe({
      next: data => {
        this.newBudget = data;
        this.loadedBudget = {...data};
        this.selectedCategory = this.categories2.find(category => category.value === this.newBudget.category);
        this.selectedFrequency = this.newBudget.resetFrequency;
      },
      error: error => {
        console.error(error);
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Could not get budget!' });
        this.closeDialog.emit();
      }
    });
  }


  submitValidation(): boolean {
    let returnValue = true;
    if (!this.newBudget.name || /^[a-zA-Z][a-zA-Z0-9 ]{0,51}$/.test(this.newBudget.name) === false) {
      this.messageService.add({severity:'warn', summary:'Invalid Budget', detail:'Name must be between 1 and 50 characters long and only contain letters, numbers and spaces!'});
      returnValue = false;
    }

    if (!this.newBudget.amount || this.newBudget.amount < 0.01 || this.newBudget.amount > 9999999) {
      this.messageService.add({severity:'warn', summary:'Invalid Budget', detail:'Amount must be between 0.01 and 9999999!'});
      returnValue = false;
    }

    if(!this.selectedCategory || !this.selectedCategory.value) {
      this.messageService.add({severity:'warn', summary:'Invalid Budget', detail:'Category must be selected!'});
      returnValue = false;
    }

    if (!this.newBudget.resetFrequency) {
      this.messageService.add({ severity: 'warn', summary: 'Invalid Budget', detail: 'Reset Frequency must be selected!' });
      returnValue = false;
    }

    return returnValue;
  }

 


  public onSubmit(): void {

    if (!this.submitValidation()) {
      return;
    }



    if (this.modeIsCreate()) {
      this.createBudget();
    } else if (this.modeIsEdit()) {
      this.updateBudget();
    }
  }

  private createBudget(): void {
    this.newBudget.category = this.selectedCategory.value;
    this.groupService.createBudget(this.groupId, this.newBudget).subscribe({
      next: budget => {
        this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Successfully created budget' });
        this.closeDialog.emit();
      },
      error: error => {
        console.error('Error creating budget:', error);
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Could not create budget!' });
      }
    });
  }

  private updateBudget(): void {
    this.newBudget.category = this.selectedCategory.value;
    // this.newBudget.resetFrequency = this.selectedFrequency;
    this.groupService.updateBudget(this.groupId, this.budgetId, this.newBudget).subscribe({
      next: budget => {
        this.loadedBudget = {...this.newBudget};
        this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Successfully updated budget' });
        this.closeDialog.emit();
      },
      error: error => {
        console.error('Error updating budget:', error);
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Could not update budget!' });
      }
    });
  }

  public deleteExistingBudget(): void {
    this.groupService.deleteBudget(this.groupId, this.budgetId).subscribe({
      next: () => {
        this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Successfully deleted budget' });
        this.closeDeleteDialog();
        this.budgetId = undefined;
        this.closeDialog.emit();
      },
      error: error => {
        console.error('Error deleting budget:', error);
        this.closeDeleteDialog();
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Could not delete budget!' });
      }
    });
  }

  public get heading(): string {
    switch (this.mode) {
      case BudgetCreateEditMode.create:
        return 'Create new Budget';
      case BudgetCreateEditMode.edit:
        return 'Edit Budget';
      case BudgetCreateEditMode.info:
        return 'Budget Details';
      default:
        return '?';
    }
  }



public resetState(): void {
  
  
  if (this.mode === BudgetCreateEditMode.edit) {
    this.mode = BudgetCreateEditMode.info;
  }
  if(this.loadedBudget != null){
    this.newBudget = {...this.loadedBudget};
    this.selectedCategory = this.categories2.find(category => category.value === this.newBudget.category);
    this.selectedFrequency = this.newBudget.resetFrequency;

  }
}


  public modeIsCreate(): boolean {
    return this.mode === BudgetCreateEditMode.create;
  }

  public modeIsInfo(): boolean {
    return this.mode === BudgetCreateEditMode.info;
  }

  public modeIsEdit(): boolean {
    return this.mode === BudgetCreateEditMode.edit;
  }

  switchToEditMode(): void {
    this.mode = BudgetCreateEditMode.edit;
  }
}
