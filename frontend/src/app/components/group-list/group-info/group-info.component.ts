import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {ConfirmationService, MenuItem} from 'primeng/api';
import { GroupService } from 'src/app/services/group.service';
import { MessageService } from "primeng/api";
import { DebtService } from 'src/app/services/debt.service';
import { GroupDto } from 'src/app/dtos/group';
import { ActivityService } from 'src/app/services/activity.service';
import { DebtGroupDetailDto } from 'src/app/dtos/debt';
import { ActivityDetailDto } from 'src/app/dtos/activity';
import {AutoCompleteCompleteEvent, AutoCompleteSelectEvent} from "primeng/autocomplete";
import {PaymentDto} from "../../../dtos/payment";
import {AuthService} from "../../../services/auth.service";
import {PaymentService} from "../../../services/payment.service";
import {NgForm} from "@angular/forms";

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

  group: GroupDto = { id: undefined, groupName: '', members: [] };
  debt: DebtGroupDetailDto = { userEmail: '', groupId: 0, membersDebts: {} };
  membersWithDebts: any[] = [];
  membersWithDebtsWithoutEven: any[] = [];
  maxDebt: number = 0;
  transactions: ActivityDetailDto[] = [];
  payments: ActivityDetailDto[] = [];
  menuitemsButtonMore: MenuItem[] | undefined;


  constructor(
    private groupService: GroupService,
    private debtService: DebtService,
    private activityService: ActivityService,
    private route: ActivatedRoute,
    private router: Router,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    private authService: AuthService,
    private paymentService: PaymentService,
  ){
  }

  ngOnInit(): void {
    this.menuitemsButtonMore = [
      {
        label: 'Edit Group',
        icon: 'pi pi-cog',
        routerLink: 'edit'
      },
      {
        label: 'Import Data',
        icon: 'pi pi-file-import',
        command: () => {
          this.authService.logoutUser()
          this.router.navigate(['/login'])
        }
      },
      {
        label: 'Export Data',
        icon: 'pi pi-file-export',
        command: () => {
          this.authService.logoutUser()
          this.router.navigate(['/login'])
        }
      }
    ]
    // tab Menu
    this.tabMenuItems = [
      { label: 'Transactions', icon: 'pi pi-fw pi-dollar' },
      { label: 'Debts', icon: 'pi pi-fw pi-money-bill' },
      { label: 'Members', icon: 'pi pi-fw pi-users' },
      { label: 'Pantry', icon: 'pi pi-fw pi-shopping-cart' },
      { label: 'Shopping lists', icon: 'pi pi-fw pi-shopping-cart' }
    ];
    this.tabMenuActiveItem = this.tabMenuItems[0];

    this.getGroup();
    this.getDebt();
    this.getTransactions();
    this.getPayments();
  }

  getGroup(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.groupService.getById(id).subscribe({
      next: group => {
        this.group = group;
      },
      error: error => {
        console.error(error);
        this.messageService.add({severity: 'error', summary: 'Login failed', detail: error.error});
        this.router.navigate(['/1']);
      }
    });
  }

  getDebt(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.debtService.getDebtById(id).subscribe(debt => {
      this.debt = debt;
      this.membersWithDebts = Object.entries(debt.membersDebts);

     // add to debtMembers only the members that have a debt which is negative
      this.debtMembers = this.membersWithDebts.filter(([_, amount]) => amount < 0).map(([member, _]) => member);

      this.membersWithDebtsWithoutEven = this.membersWithDebts.filter(([_, amount]) => amount !== 0);
      this.maxDebt = Math.max(...this.membersWithDebtsWithoutEven.map(([_, amount]) => amount));
    });
  }

  getTransactions(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.activityService.getExpenseActivitiesFromGroup(id, {search: '', from: undefined, to: undefined}).subscribe(transactions => {
      this.transactions = transactions;
    });
  }

  getPayments(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.activityService.getPaymentActivitiesFromGroup(id, {search: '', from: undefined, to: undefined}).subscribe(payments => {
      console.log(payments);
      this.payments = payments;
    });
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

    return (Math.abs(amount) / this.maxDebt) * 100 + 'px';
  }

  onActiveItemChange(event: MenuItem) {
    this.tabMenuActiveItem = event;
  }

  isTransactionsSelected(): boolean {
    return this.tabMenuActiveItem === this.tabMenuItems[0];
  }

  isDebtsSelected(): boolean {
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
    console.log("selectedDebtMember")
    console.log(event.value)
    console.log(this.debt.membersDebts[event.value])

    this.amountOfSelectedDebtMember = -this.debt.membersDebts[event.value];
  }

  goBackFromSettleDebts($event: MouseEvent) {
    this.confirmationService.confirm({
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
      groupId: this.group.id
    }

    this.paymentService.createPayment(payment).subscribe({
      next: data => {
        this.getDebt();
        this.messageService.add({severity: 'success', summary: 'Success', detail: `Payment successfully created`});
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
}

// import {Component, OnInit} from '@angular/core';
// import {GroupService} from "../../../services/group.service";
// import {ToastrService} from "ngx-toastr";
// import {GroupDto} from "../../../dtos/group";
// import { BudgetDto } from '../../../dtos/budget';
// import {ActivatedRoute, Router} from "@angular/router";
// import {DebtGroupDetailDto} from "../../../dtos/debt";
// import {DebtService} from "../../../services/debt.service";
// import {ActivityType} from "../../expense/expense-list.component";

// @Component({
//   selector: 'app-group-info',
//   templateUrl: './group-info.component.html',
//   styleUrl: './group-info.component.scss'
// })
// export class GroupInfoComponent implements OnInit {

//   group: GroupDto = {
//     groupName: '',
//     members: []
//   };

//   debt: DebtGroupDetailDto

//   constructor(
//     private service: GroupService,
//     private debtService: DebtService,
//     private router: Router,
//     private route: ActivatedRoute,
//     private groupService: GroupService,
//     private notification: ToastrService,
//   ) {
//   }

//   ngOnInit(): void {
//     this.getGroup();
//     this.getDebt();
//   }

//   getGroup(): void {
//     const id = Number(this.route.snapshot.paramMap.get('id'));
//     this.service.getById(id)
//       .subscribe(pGroup => {
//         this.group = pGroup;
//       });
//   }

//   getDebt(): void {
//     const id = Number(this.route.snapshot.paramMap.get('id'));
//     this.debtService.getDebtById(id)
//       .subscribe(debt => {
//         this.debt = debt;
//         console.log(debt);
//       });
//   }

//   objectKeys(obj: any): string[] {
//     return Object.keys(obj).sort();
//   }

//   getSortedMembers(): any[] {
//     return this.group.members.sort((a, b) => a.localeCompare(b));
//   }

//   getBorderColor(value: number): string {
//     return value > 0 ? 'green' : 'red';
//   }

//     protected readonly ActivityType = ActivityType;
//   protected readonly parseFloat = parseFloat;
// }
