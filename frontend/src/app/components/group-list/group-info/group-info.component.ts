import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MenuItem } from 'primeng/api';
import { GroupService } from 'src/app/services/group.service';
import { MessageService } from "primeng/api";
import { DebtService } from 'src/app/services/debt.service';
import { GroupDto } from 'src/app/dtos/group';
import { ActivityService } from 'src/app/services/activity.service';
import { DebtGroupDetailDto } from 'src/app/dtos/debt';
import { ActivityDetailDto } from 'src/app/dtos/activity';

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

  constructor(
    private groupService: GroupService,
    private debtService: DebtService,
    private activityService: ActivityService,
    private route: ActivatedRoute,
    private router: Router,
    private messageService: MessageService,
  ){
  }

  ngOnInit(): void {

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
