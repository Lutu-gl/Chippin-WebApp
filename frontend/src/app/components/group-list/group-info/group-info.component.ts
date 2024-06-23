import { Component, OnInit, ViewChild } from '@angular/core';
import { Globals } from '../../../global/globals';
import { ActivatedRoute, Router } from '@angular/router';
import {ConfirmationService, MenuItem} from 'primeng/api';
import { GroupService } from 'src/app/services/group.service';
import { MessageService } from "primeng/api";
import { DebtService } from 'src/app/services/debt.service';
import { GroupDto } from 'src/app/dtos/group';
import { ActivityService } from 'src/app/services/activity.service';
import { DebtGroupDetailDto } from 'src/app/dtos/debt';
import { ActivityDetailDto } from 'src/app/dtos/activity';
import { ExpenseCreateComponent, ExpenseCreateEditMode } from '../../expense/expense-create/expense-create.component';
import {AutoCompleteCompleteEvent, AutoCompleteSelectEvent} from "primeng/autocomplete";
import {PaymentDto} from "../../../dtos/payment";
import { BudgetDto } from '../../../dtos/budget';
import {AuthService} from "../../../services/auth.service";
import {PaymentService} from "../../../services/payment.service";
import {NgForm} from "@angular/forms";
import { BudgetCreateComponent, BudgetCreateEditMode } from '../../budget/budget-create/budget-create.component';
import {PaymentCreateEditMode} from "../../payment-create/payment-create.component";
import {ExpenseDetailDto} from "../../../dtos/expense";
import {ExpenseService} from "../../../services/expense.service";
import { EmailSuggestionsAndContent, ImportDto } from 'src/app/dtos/importExport';
import { ImportExportService } from 'src/app/services/import-export.service';
import {Observable} from "rxjs";
import {FriendshipService} from "../../../services/friendship.service";

@Component({
  selector: 'app-group-info',
  templateUrl: './group-info.component.html',
  styleUrl: './group-info.component.scss'
})
export class GroupInfoComponent implements OnInit {

  chartData: any;
  chartOptions: any;
  tabMenuItems: MenuItem[] | undefined;
  tabMenuActiveItem: MenuItem | undefined;
  budgets: BudgetDto[] = [];

  group: GroupDto = { id: undefined, groupName: '', members: [] };
  debt: DebtGroupDetailDto = { userEmail: '', groupId: 0, membersDebts: {} };
  membersWithDebts: any[] = [];
  membersWithDebtsWithoutEven: any[] = [];
  maxDebt: number = 0;
  transactionsActivities: ActivityDetailDto[] = [];

  payments: PaymentDto[] = [];
  expenses: ExpenseDetailDto[] = [];

  menuitemsButtonMore: MenuItem[] | undefined;

  @ViewChild(ExpenseCreateComponent) expenseCreateComponent!: ExpenseCreateComponent;
  isExpenseDialogVisible: boolean = false;
  expenseDialogMode: ExpenseCreateEditMode;
  expenseDialogExpenseId: number;

  isPaymentDialogVisible: boolean = false;
  paymentDialogMode: PaymentCreateEditMode;
  paymentDialogPaymentId: number;
  paymentForDialog: PaymentDto = undefined;
  amountForPaymentDialog: number;
  payerEmailForPaymentDialog: any;
  receiverEmailForPaymentDialog: any;
  isDeleteDialogVisible: boolean = false;
  paymentDeleted: boolean = false;

  @ViewChild(BudgetCreateComponent) budgetCreateComponent!: BudgetCreateComponent;
  isBudgetDialogVisible: boolean = false;
  budgetDialogMode: BudgetCreateEditMode;
  budgetDialogBudgetId: number;

  isImportDialogVisible: boolean = false;
  importUrl: string = this.globals.backendUri + '/import/splitwise/email-suggestions';
  uploadedFile: any;
  emailSuggestions: object;
  importContent: string[];
  importRequestLoading: boolean = false;

  protected readonly PaymentCreateEditMode = PaymentCreateEditMode;
  transactionsPaginationCount: number = 10;
  transactionsPage: number = 0;
  transactionsSearchFilter: string = '';

  activitiesPaginationCount: number = 10;
  activitiesPage: number = 0;
  activitiesSearchFilter: string = '';

  // for edit group modal
  editNewGroupModalVisible: boolean;
  groupForEditModal: GroupDto = {
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
    private debtService: DebtService,
    private activityService: ActivityService,
    private friendshipService: FriendshipService,
    private route: ActivatedRoute,
    private router: Router,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    protected authService: AuthService,
    private paymentService: PaymentService,
    private expenseService: ExpenseService,
    private importExportService: ImportExportService,
    private globals: Globals
  ){
  }



  closeImportDialog(): void {
    this.isImportDialogVisible = false;
    this.uploadedFile = null;
    this.emailSuggestions = null;
    this.importContent = null;
    this.importRequestLoading = false;
  }

  importSuccessful(event): void {
    const response: EmailSuggestionsAndContent = event.originalEvent.body;
    this.importContent = response.content;
    this.emailSuggestions = Object.entries(response.emailSuggestions).map(([name, email]) => ({name, email, filteredSuggestions: this.group.members.slice()}));
  }

  importError(event): void {
    if (event.error && event.error && event.error.error.errors) {
      this.messageService.add({severity: 'error', summary: 'Error', detail: `${event.error.error.message}: ${event.error.error.errors.join('; ')}`});
    } else {
      this.messageService.add({severity: 'error', summary: 'Error', detail: `Import did not work!`});
    }
  }

  filterImportEmail(event, entry): void {
    entry.filteredSuggestions = this.group.members.filter(member => member.includes(event.query));
  }

  importData(): void {
    this.importRequestLoading = true;

    const keys = Object.keys(this.emailSuggestions);
    for (let key of keys) {
      const obj = this.emailSuggestions[key];
      this.importContent[0] = this.importContent[0].replace(`,${obj.name}`, `,${obj.email}`);
    }
    const importDto: ImportDto = {groupId: this.group.id, content: this.importContent.join('\n')};

    this.importExportService.importData(importDto).subscribe({
      next: () => {
        this.messageService.add({severity: 'success', summary: 'Success', detail: 'Import successful!'});
        this.closeImportDialog();
        this.ngOnInit();
      },
      error: error => {
        if (error && error.error && error.error.errors) {
          this.messageService.add({severity: 'error', summary: 'Error', detail: `${error.error.errors.join('; ')}`});
        } else {
          this.messageService.add({severity: 'error', summary: 'Error', detail: `Import did not work!`});
        }
        this.closeImportDialog();
      }
    });
  }

  budgetModalClose() {
    this.budgetDialogMode = BudgetCreateEditMode.info;
    this.isBudgetDialogVisible = false;
    this.getGroupBudgets();

  }

  openCreateExpenseDialog(): void {
    this.isExpenseDialogVisible = true;
    this.expenseDialogMode = ExpenseCreateEditMode.create;
    this.expenseCreateComponent.resetMode();
    this.expenseCreateComponent.customInit({
      mode: ExpenseCreateEditMode.create,
      groupId: this.group.id,
      expenseId: undefined
    });
  }

  closeCreateExpenseDialog(): void {
    this.isExpenseDialogVisible = false;
    this.ngOnInit();
  }

  openInfoExpenseDialog(expenseId: number): void {
    this.expenseDialogExpenseId = expenseId;
    this.isExpenseDialogVisible = true;
    this.expenseDialogMode = ExpenseCreateEditMode.info;
    this.expenseCreateComponent.resetMode();
    this.expenseCreateComponent.customInit({
      mode: ExpenseCreateEditMode.info,
      groupId: this.group.id,
      expenseId: expenseId
    });
  }

  expenseModalHided(): void {
    //this.expenseCreateComponent.resetAllStates();
  }

  openInfoPaymentDialog(paymentId: number): void {
    this.paymentDialogMode = PaymentCreateEditMode.info;
    console.log(this.paymentDialogMode);
    this.paymentDialogPaymentId = paymentId;

    this.paymentService.getPaymentById(paymentId).subscribe(
      payment => {
        this.paymentForDialog = payment
        this.amountForPaymentDialog = payment.amount
        this.payerEmailForPaymentDialog = payment.payerEmail
        this.receiverEmailForPaymentDialog = payment.receiverEmail
      },
      error => {
        if (error && error.error && error.error.errors) {
          //this.notification.error(`${error.error.errors.join('. \n')}`);
          for (let i = 0; i < error.error.errors.length; i++) {
            this.messageService.add({severity: 'error', summary: 'Error', detail: `${error.error.errors[i]}`});
          }
        } else if (error && error.error && error.error.message) { // if no detailed error explanation exists. Give a more general one if available.
          this.messageService.add({severity: 'error', summary: 'Error', detail: `${error.error.message}`});
        } else if (error && error.error.detail) {
          this.messageService.add({severity: 'error', summary: 'Error', detail: `${error.error.detail}`});
        } else if (error && error.error) {
          this.messageService.add({severity: 'error', summary: 'Error', detail: `${error.error}`});
        } else {
          console.error('Error getting payment', error);
          this.messageService.add({severity: 'error', summary: 'Error', detail: `Getting payment did not work!`});
        }
      }
    );
    this.isPaymentDialogVisible = true;
  }


  openInfoBudgetDialog(budgetId: number): void {

    this.budgetDialogMode = BudgetCreateEditMode.info;
    this.budgetCreateComponent.mode = BudgetCreateEditMode.info;

    this.budgetDialogBudgetId = budgetId;
    this.budgetCreateComponent.budgetId = budgetId;
    this.isBudgetDialogVisible = true;
    this.budgetCreateComponent.resetState();
  }

  openCreateBudgetDialog(): void {
    this.budgetDialogMode = BudgetCreateEditMode.create;
    this.budgetCreateComponent.mode = BudgetCreateEditMode.create;
    this.isBudgetDialogVisible = true;
    this.budgetCreateComponent.resetState();
  }

  closeCreateBudgetDialog(): void {
    this.isBudgetDialogVisible = false;
    this.budgetDialogMode = BudgetCreateEditMode.info;
    this.getGroupBudgets();
    // this.ngOnInit();
  }

  public budgetModeIsCreate(): boolean {
    return this.budgetDialogMode === BudgetCreateEditMode.create;
  }


  getBudgetPercentage(budget: any): number {
    if(budget.alreadySpent === 0){
      return 0;
    }
    let ret = Math.round((budget.alreadySpent / budget.amount) * 100);
    if(ret > 100){
      return 100;
    }
    return ret;
  }

  getProgressBarColor(budget: any): string {
    let percentage = (this.getBudgetPercentage(budget));
    if (percentage < 50) {
      return 'green-progress';
    } else if (percentage < 75) {
      return 'yellow-progress';
    } else {
      return 'red-progress';
    }
  }

  ngOnInit(): void {
    this.menuitemsButtonMore = [
      {
        label: 'Edit Group',
        icon: 'pi pi-cog',
        command: () => {
          this.createNewGroupModalOpen();
        }
      },
      {
        label: 'Import Data',
        icon: 'pi pi-file-import',
        command: () => {
          this.isImportDialogVisible = true;
        }
      },
      {
        label: 'Export Data',
        icon: 'pi pi-file-export',
        command: () => {
          this.importExportService.exportData(this.group.id).subscribe({
            next: data => {
              const url = window.URL.createObjectURL(data);
              const a = document.createElement('a');
              a.href = url;
              a.download = 'export.csv';
              a.click();
              window.URL.revokeObjectURL(url);
            },
            error: error => {
              if (error && error.error && error.error.errors) {
                this.messageService.add({severity: 'error', summary: 'Error', detail: `${error.error.errors.join('; ')}`});
              } else {
                this.messageService.add({severity: 'error', summary: 'Error', detail: `Export did not work!`});
              }
            }
          });
        }
      },
      {
        label: 'Leave Group',
        icon: 'pi pi-sign-out',
        command: () => {
          this.confirmToLeaveGroup();
          }
        }
    ]
    // tab Menu
    this.tabMenuItems = [
      { label: 'Transactions', icon: 'pi pi-fw pi-dollar' },
      { label: 'Activities', icon: 'pi pi-fw pi-chart-bar' },
      { label: 'Members', icon: 'pi pi-fw pi-users' },
      { label: 'Pantry', icon: 'pi pi-fw pi-shopping-cart' },
      { label: 'Shopping lists', icon: 'pi pi-fw pi-shopping-cart' },
      { label: 'Budgets', icon: 'pi pi-fw pi-wallet' }
    ];
    this.tabMenuActiveItem = this.tabMenuItems[0];

    this.transactionsActivities = []

    this.getGroup();
    this.getDebt();
    this.getTransactions();
    this.getPayments();
    this.getExpenses();
    this.getGroupBudgets();

    this.paymentForDialog = undefined;
  }

  getGroup(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.groupService.getById(id).subscribe({
      next: group => {
        this.group = group;
      },
      error: error => {
        console.error(error);
        this.messageService.add({severity: 'error', summary: 'Error', detail: error.error});
        this.router.navigate(['/home', 'groups']);
      }
    });
  }

  getDebt(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.debtService.getDebtById(id).subscribe(debt => {
      console.log(debt);
      this.debt = debt;
      this.membersWithDebts = Object.entries(debt.membersDebts);

     // add to debtMembers only the members that have a debt which is negative
      this.debtMembers = this.membersWithDebts.filter(([_, amount]) => amount < 0).map(([member, _]) => member);

      this.membersWithDebtsWithoutEven = this.membersWithDebts.filter(([_, amount]) => amount !== 0);
      this.maxDebt = Math.max(...this.membersWithDebtsWithoutEven.map(([_, amount]) => Math.abs(amount)));
    });
  }

  getTransactions(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.activityService.getExpenseActivitiesFromGroup(id, {search: '', from: undefined, to: undefined}).subscribe(transactions => {
      transactions.forEach(transaction => {
        this.transactionsActivities.push(transaction);
      })
    });

    this.activityService.getPaymentActivitiesFromGroup(id, {search: '', from: undefined, to: undefined}).subscribe(payments => {
      payments.forEach(payment => {
        this.transactionsActivities.push(payment);
      })
   });
  }

  getPayments(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    this.paymentService.getPaymentsByGroupId(id).subscribe(payments => {
      this.payments = payments;
    });
  }

  getExpenses(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.expenseService.getExpensesByGroupId(id).subscribe(expenses => {
      this.expenses = expenses;
    });
  }

  getGroupBudgets(): void {

    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.groupService.getGroupBudgets(id)
      .subscribe(budgets => {
        this.budgets = budgets;
        this.budgets.forEach(budget => {
          budget.daysUntilReset = this.calculateDaysUntilReset(budget.timestamp);
        });
      }, error => {
        console.error('Failed to load budgets', error);
      });
  }

  getRemainingBudget(budget: any): number {

    const remaining = budget.amount - budget.alreadySpent;

    if (remaining < 0) {
        return 0;
    }

    return remaining; 
  }

  calculateDaysUntilReset(timestamp: string): number {
    if(timestamp === null) return 0;

    const now = new Date();
    const resetDate = new Date(timestamp);
    const timeDiff = resetDate.getTime() - now.getTime();
    return Math.ceil(timeDiff / (1000 * 3600 * 24));
  }

  getMembersWithDebtsSorted() {
    return this.membersWithDebts.sort((a, b) => {
      if (a[1] < 0 && b[1] > 0) {
        return -1;
      } else if (a[1] > 0 && b[1] < 0) {
        return 1;
      } else if (a[1] === 0 && b[1] !== 0) {
        return 1;
      } else if (a[1] !== 0 && b[1] === 0) {
        return -1;
      } else {
        return a[0].localeCompare(b[0]);
      }
    });
  }

  getMembersOnlyNegativeDebts() {
    return this.membersWithDebts.filter(([_, amount]) => amount < 0);
  }

  calcChartHeight(amount): string {
    if (this.maxDebt === 0) {
      return '100px';
    }
    return Math.min((Math.abs(amount) / this.maxDebt) * 100, 100) + 'px';
  }

  onActiveItemChange(event: MenuItem) {
    this.tabMenuActiveItem = event;
  }

  isTransactionsSelected(): boolean {
    return this.tabMenuActiveItem === this.tabMenuItems[0];
  }

  isActivitiesSelected(): boolean {
    return this.tabMenuActiveItem === this.tabMenuItems[1];
  }

  isMembersSelected(): boolean {
    return this.tabMenuActiveItem === this.tabMenuItems[2];
  }

  isPantrySelected(): boolean {
    return this.tabMenuActiveItem === this.tabMenuItems[3];
  }

  isShoppingListsSelected(): boolean {
    return this.tabMenuActiveItem === this.tabMenuItems[4];
  }
  isBudgetSelected(): boolean {
    return this.tabMenuActiveItem === this.tabMenuItems[5];
  }


  getActivityColor(expenseCategory: string) {
    switch (expenseCategory) {
      case 'EXPENSE':
        return 'bg-blue-50';
      case 'EXPENSE_UPDATE':
        return 'bg-yellow-50';
      case 'EXPENSE_DELETE':
        return 'bg-red-50';
      case 'EXPENSE_RECOVER':
        return 'bg-green-50';
      case 'PAYMENT':
        return 'bg-blue-50';
      case 'PAYMENT_UPDATE':
        return 'bg-yellow-50';
      case 'PAYMENT_DELETE':
        return 'bg-red-50';
      case 'PAYMENT_RECOVER':
        return 'bg-green-50';
      default:
        return '';
    }
  }

  getPaymentColor() {
    return 'bg-green-50';
  }

  getTransactionActivitiesVarSorted():ActivityDetailDto[] {
    let activities = this.transactionsActivities.sort((a, b) => {
      let aTime = a.timestamp instanceof Date ? a.timestamp.getTime() : new Date(a.timestamp).getTime();
      let bTime = b.timestamp instanceof Date ? b.timestamp.getTime() : new Date(b.timestamp).getTime();
      return bTime - aTime;
    });

    activities = activities.filter(activity => activity.description.toLowerCase().includes(this.activitiesSearchFilter.toLowerCase()));

    return activities;
  }

  getTransactionActivitiesVarSortedAndPaginated():ActivityDetailDto[] {
    return this.getTransactionActivitiesVarSorted()
      .slice(this.activitiesPage * this.activitiesPaginationCount, (this.activitiesPage + 1) * this.activitiesPaginationCount);
  }

  // combine expeneses and payments in one array and sort them by the date
  getTransactionVarSorted() {
    let transactions: (ExpenseDetailDto | PaymentDto)[] = [...this.expenses, ...this.payments];

    transactions.sort((a, b) => {
      let aTime = a.date instanceof Date ? a.date.getTime() : new Date(a.date).getTime();
      let bTime = b.date instanceof Date ? b.date.getTime() : new Date(b.date).getTime();
      return bTime - aTime;
    });
    console.log(transactions)
    return transactions;
  }

  getTransactionVarSortedWithoutDeleted() {
    let transactions: (ExpenseDetailDto | PaymentDto)[] = [...this.expenses, ...this.payments];

    if (!transactions || transactions.length === 0) {
      return [];
    }

    transactions = transactions.filter(transaction => !transaction.deleted)

    transactions = transactions.filter((transaction: any) => {
      if (transaction.name && transaction.name.toLowerCase().includes(this.transactionsSearchFilter.toLowerCase())) {
        return true;
      }
      if (transaction.payerEmail && transaction.payerEmail.toLowerCase().includes(this.transactionsSearchFilter.toLowerCase())) {
        return true;
      }
      if (transaction.receiverEmail && transaction.receiverEmail.toLowerCase().includes(this.transactionsSearchFilter.toLowerCase())) {
        return true;
      }

      return false;
    });

    transactions.sort((a, b) => {
      let aTime = a.date instanceof Date ? a.date.getTime() : new Date(a.date).getTime();
      let bTime = b.date instanceof Date ? b.date.getTime() : new Date(b.date).getTime();
      return bTime - aTime;
    });
    //console.log(transactions)
    return transactions;
  }

  getTransactionVarSortedWithoutDeletedAndPaginated() {
    return this.getTransactionVarSortedWithoutDeleted()
      .slice(this.transactionsPage * this.transactionsPaginationCount, (this.transactionsPage + 1) * this.transactionsPaginationCount);
  }


  isExpense(transaction: any): boolean {
    return transaction.hasOwnProperty('category');
  }

  // Sorts the members with debts by the amount of debt
  // First the members you own are shown, then the members that own you
  // Lastly the members that have no debt are shown
  // If members cant be sorted by debt, then they are sorted by name
  visibleModalSettleDebts: boolean;
  filteredDebtMembers: any[];
  selectedDebtMemberVar: any;
  debtMembers: any[];
  amountOfSelectedDebtMember: number;

  showDialogSettleDebts() {
    this.selectedDebtMemberVar = undefined;
    this.amountOfSelectedDebtMember = undefined;
    this.visibleModalSettleDebts = true;
  }

  filterDebtMembers(event: AutoCompleteCompleteEvent) {
    console.log("filterDebtMembers")
    console.log(this.debtMembers)

    let filtered: any[] = [];
    let query = event.query;

    for (let i = 0; i < (this.debtMembers as any[]).length; i++) {
      let friend = (this.debtMembers as any[])[i];
      if (friend.toLowerCase().indexOf(query.toLowerCase()) == 0) {
        filtered.push(friend);
      }
    }

    this.filteredDebtMembers = filtered;
  }

  // When a member is selected from the autocomplete dropdown
  // Add the amount from the debt variable to the label amount of the modal
  selectedDebtMember(event: AutoCompleteSelectEvent) {

    this.amountOfSelectedDebtMember = -this.debt.membersDebts[event.value];
  }

  goBackFromSettleDebts($event: MouseEvent) {

    this.confirmationService.confirm({
      key: 'SettleDebtsConfirmDialog',
      target: event.target as EventTarget,
      message: 'Do you want to cancel the payment creation?',
      header: 'Cancel Confirmation',
      icon: 'pi pi-info-circle',
      acceptButtonStyleClass:"p-button-danger p-button-text",
      rejectButtonStyleClass:"p-button-text p-button-text",
      acceptIcon:"none",
      rejectIcon:"none",

      accept: () => {
        this.messageService.add({ severity: 'info', summary: 'Confirmed', detail: 'Payment creation canceled' });
        this.visibleModalSettleDebts = false;
        this.selectedDebtMemberVar = undefined;
        this.amountOfSelectedDebtMember = undefined;
        this.ngOnInit();
      },
      reject: () => {
        // this.messageService.add({ severity: 'info', summary: 'Cancel', detail: 'You have rejected' });
      }
    });
  }

  paymentCreationSave(form: NgForm) {
    if(form.invalid) {
      this.messageService.add({severity: 'error', summary: 'Error', detail: `Please fill in all required fields`});
      return;
    }

    let payment: PaymentDto = {
      amount: this.amountOfSelectedDebtMember,
      payerEmail: this.authService.getEmail(),
      receiverEmail: this.selectedDebtMemberVar,
      deleted: false,
      groupId: this.group.id,
      archived: false,
      date: null
    }

    this.paymentService.createPayment(payment).subscribe({
      next: data => {
        this.getDebt();
        this.messageService.add({severity: 'success', summary: 'Success', detail: `Payment successfully created`});
        this.ngOnInit();
      },
      error: error => {
        if (error && error.error && error.error.errors) {
          //this.notification.error(`${error.error.errors.join('. \n')}`);
          for (let i = 0; i < error.error.errors.length; i++) {
            this.messageService.add({severity: 'error', summary: 'Error', detail: `${error.error.errors[i]}`});
          }
        } else if (error && error.error && error.error.message) { // if no detailed error explanation exists. Give a more general one if available.
          this.messageService.add({severity: 'error', summary: 'Error', detail: `${error.error.message}`});
        } else if (error && error.error.detail) {
          this.messageService.add({severity: 'error', summary: 'Error', detail: `${error.error.detail}`});
        } else if (error && error.error) {
          this.messageService.add({severity: 'error', summary: 'Error', detail: `${error.error}`});
        } else {
          console.error('Error making payment', error);
          this.messageService.add({severity: 'error', summary: 'Error', detail: `Creation of payment did not work!`});
        }
      }
    });

    this.visibleModalSettleDebts = false;
    this.selectedDebtMemberVar = undefined;
    this.amountOfSelectedDebtMember = undefined;
  }

  confirmToLeaveGroup() {
    this.confirmationService.confirm({
      key: 'leaveGroup',
      header: 'Attention!',
      message: `All the Expenses and Payments get archived and will not be editable again.<br>This action cannot be undone.<br>Do you really want to leave the group?`,
      acceptIcon: 'pi pi-check mr-2',
      rejectIcon: 'pi pi-times mr-2',
      rejectButtonStyleClass: 'p-button-sm',
      acceptButtonStyleClass: 'p-button-sm',
      accept: () => {
        this.groupService.leaveGroup(this.group).subscribe({
          next: () => {
            this.messageService.add({severity:'success', summary:'Success', detail:`You left the group ${this.group.groupName} successfully!`});
            this.router.navigate(['/home', 'groups']);
          },
          error: error => {
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
              console.error(`Leaving group did not work!`, error);
              this.messageService.add({severity:'error', summary:'Error', detail:`Leaving group did not work!`});
            }
          }
        });
        },
      reject: () => {
        this.messageService.add({ severity: 'info', summary: 'Rejected', detail: 'You did not leave the group', life: 3000 });
      }
    });
  }

  paymentModeIsEdit(): boolean {
    return this.paymentDialogMode === PaymentCreateEditMode.edit;
  }
  paymentModeIsInfo() {
    return this.paymentDialogMode === PaymentCreateEditMode.info;
  }

  paymentIsDeleted(): boolean {
    if(this.paymentForDialog) {
      return this.paymentForDialog.deleted;
    }
    return false;
  }

  paymentIsArchived(): boolean {
    if(this.paymentForDialog) {
      return this.paymentForDialog.archived;
    }
    return false;
  }

  openDeleteDialog(): void {
    this.confirmationService.confirm({
      message: 'Are you sure you want to delete this payment ?',
      header: 'Confirm',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.deleteExistingPayment()
      }
    });
  }
  closeDeleteDialog(): void {
    this.isDeleteDialogVisible = false;
  }

  deleteExistingPayment() {
    this.paymentService.deletePayment(this.paymentDialogPaymentId).subscribe({
      next: data => {
        this.messageService.add({severity:'success', summary:'Success', detail:'Deleted expense successfully!'});
        this.paymentDeleted = true;
        this.closeDeleteDialog();
      },
      error: error => {
        this.closeDeleteDialog();
        this.printError(error);
      }
    });
    this.isPaymentDialogVisible = false;
    setTimeout(() => {
      this.ngOnInit();
    }, 250);
  }

  recoverDeletedPayment() {
    this.paymentService.recoverPayment(this.paymentDialogPaymentId).subscribe({
      next: data => {
        this.messageService.add({severity:'success', summary:'Success', detail:'Recovered payment successfully!'});
        this.paymentDeleted = false;
      },
      error: error => {
        this.printError(error);
      }
    });
    this.isPaymentDialogVisible = false;
    setTimeout(() => {
      this.ngOnInit();
    }, 250);
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
        console.error('This operation did not work!');
    }
  }

  paymentSwitchToEditMode() {
    this.paymentDialogMode = PaymentCreateEditMode.edit;
  }

  paymentModalSaveChanges() {
    const submitPayment: PaymentDto = {
      amount: this.amountForPaymentDialog,
      payerEmail: this.payerEmailForPaymentDialog,
      receiverEmail: this.receiverEmailForPaymentDialog,
      groupId: this.group.id,
      deleted: false,
      archived: false,
      date: null
    };
    this.paymentService.updatePayment(this.paymentDialogPaymentId, submitPayment).subscribe({
      next: data => {
        this.messageService.add({severity:'success', summary:'Success', detail:'Updated payment successfully!'});
        this.paymentDialogMode = PaymentCreateEditMode.info;
      },
      error: error => {
        this.printError(error);
      }
    })
    this.isPaymentDialogVisible = false;
    setTimeout(() => {
      this.ngOnInit();
    }, 250);
  }

  showVisualizationPage() {
    this.router.navigate(['/group',this.group.id ,'visualization']);
  }

  getLoggedInUserAmountOfExpense(expense: ExpenseDetailDto): string {
    if(expense.participants[this.authService.getEmail()])
      return (expense.participants[this.authService.getEmail()] * expense.amount).toFixed(2);
    return "0";
  }

  getPayerEmailForExpenseDescription(transaction: ExpenseDetailDto) {
    return transaction.payerEmail === this.authService.getEmail() ? "You" : transaction.payerEmail;
  }

  // for edit group modal
  private editGroupModalNgOnInit() {
    this.friendshipService.getFriends().subscribe({
      next: data => {
        this.friends = data;
        this.friends.sort((a, b) => a.localeCompare(b));
        this.friendsEdit = data.filter(friend => !this.membersEmails.includes(friend));
        this.friendsEdit.sort((a, b) => a.localeCompare(b));
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
    var memberGroupSaved = JSON.parse(JSON.stringify(this.groupForEditModal.members));

    if (form.valid) {
      let observable: Observable<GroupDto>;


      this.membersEmailsEdit.forEach(member => {
        this.groupForEditModal.members.push(member)
      });

      observable = this.groupService.update(this.groupForEditModal);

      observable.subscribe({
        next: data => {
          this.messageService.add({severity:'success', summary:'Success', detail:`Group ${this.groupForEditModal.groupName} successfully edited`});
          this.groupForEditModal.members = [];
          this.groupForEditModal.groupName = undefined;
          this.editNewGroupModalVisible = false;
          this.editNewGroupModalVisible = false;
          this.ngOnInit();
        },
        error: error => {
          this.groupForEditModal.members = memberGroupSaved;
          console.log(error);
          if (error && error.error && error.error.errors) {
            for (let i = 0; i < error.error.errors.length; i++) {
              this.messageService.add({severity:'error', summary:'Error', detail:`${error.error.errors[i]}`});
            }
          } else if (error && error.error && error.error.message) { // if no detailed error explanation exists. Give a more general one if available.
            this.messageService.add({severity:'error', summary:'Error', detail:`${error.error.message}`});
          } else if (error && error.error.detail) {
            this.messageService.add({severity:'error', summary:'Error', detail:`${error.error.detail}`});
          } else {
            console.error('Error editing group', error);
            this.messageService.add({severity:'error', summary:'Error', detail:`Edit of group did not work!`});
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

  public addMemberEdit(member: AutoCompleteSelectEvent) {
    setTimeout(() => {
      this.currentlySelected = ""
    });

    if (!member.value) return;
    if (this.membersEmailsEdit.includes(member.value)) {
      this.messageService.add({
        severity: 'error',
        summary: 'Error',
        detail: `${member.value} is already in participant list`
      });
      return;
    }
    if (this.group.members.includes(member.value)) {
      this.messageService.add({
        severity: 'error',
        summary: 'Error',
        detail: `${member.value} is already a member of the group`
      });
      return;
    }

    this.membersEmailsEdit.push(member.value);
  }

  public removeMember(index: number) {
    if (this.authService.getEmail() == this.membersEmails[index]) {
      this.messageService.add({severity:'error', summary:'Error', detail:`You can't remove yourself from the group.`});
      return;
    }

    this.membersEmails.splice(index, 1);
  }

  public removeMemberEdit(index: number) {
    if (this.authService.getEmail() == this.membersEmailsEdit[index]) {
      this.messageService.add({severity:'error', summary:'Error', detail:`You can't remove yourself from the group.`});
      return;
    }

    this.membersEmailsEdit.splice(index, 1);
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

  filterMembersEdit(event: AutoCompleteCompleteEvent) {

    let filtered: any[] = [];
    let query = event.query;

    for (let i = 0; i < (this.friendsEdit as any[]).length; i++) {
      let friend = (this.friendsEdit as any[])[i];
      if (friend.toLowerCase().indexOf(query.toLowerCase()) == 0) {
        filtered.push(friend);
      }
    }

    this.filteredFriendsEdit = filtered;
  }

  getMembersEmail(): string[] {
    return this.membersEmails;
  }

  getSortedMembersEmail(): string[] {
    return this.membersEmails.sort((a, b) => a.localeCompare(b));
  }

  getSortedGroupMembersEmail(): string[] {
    return this.groupForEditModal.members.sort((a, b) => a.localeCompare(b));
  }

  // return only the members that are not in the group yet
  getMembersEmailEdit(): string[] {
    return this.membersEmailsEdit;
  }
  getSortedMembersEmailEdit(): string[] {
    return this.membersEmails.sort((a, b) => a.localeCompare(b)).filter(member => !this.groupForEditModal.members.includes(member));
  }

  createNewGroupModalOpen() {
    this.editGroupModalNgOnInit();

    this.groupForEditModal.members = this.group.members;
    this.groupForEditModal.groupName = this.group.groupName;
    this.groupForEditModal.id = this.group.id;
    this.membersEmailsEdit = [];
    this.editNewGroupModalVisible = true
  }
  goBack($event: MouseEvent) {
    this.groupForEditModal.members = [];
    this.groupForEditModal.groupName = undefined;
    this.editNewGroupModalVisible = false;

    // this.confirmationService.confirm({
    //   message: 'Are you sure you want to cancel the creation of the group ?',
    //   header: 'Confirm',
    //   icon: 'pi pi-exclamation-triangle',
    //   accept: () => {
    //     this.messageService.add({ severity: 'info', summary: 'Confirmed', detail: 'Edit of group canceled' });
    //     this.groupForEditModal.members = [];
    //     this.groupForEditModal.groupName = undefined;
    //     this.editNewGroupModalVisible = false;
    //   }
    // });
  }

  paginateTransactions(event: any) {
    this.transactionsPage = event.page;
  }

  searchTransactionsChanged(event: KeyboardEvent): void {
    this.transactionsSearchFilter = (event.target as HTMLInputElement).value;
  }

  paginateActivities(event: any) {
    this.activitiesPage = event.page;
  }

  searchActivitiesChanged(event: KeyboardEvent): void {
    this.activitiesSearchFilter = (event.target as HTMLInputElement).value;
  }

}