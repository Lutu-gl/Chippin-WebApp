import {Component, OnInit} from '@angular/core';
import { MenuItem } from 'primeng/api';

@Component({
  selector: 'app-group-info',
  templateUrl: './group-info.component.html',
  styleUrl: './group-info.component.scss'
})
export class GroupInfoComponent implements OnInit {
  
  data: any;
  options: any;
  tabMenuItems: MenuItem[] | undefined;
  tabMenuActiveItem: MenuItem | undefined;

  constructor(){

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


    // TODO: change this data
    this.data = {
      labels: ['Emil Hafner', 'Rafael Milchram', 'Markus Berling'],
      datasets: [
        {
          label: 'Total Amount',
          backgroundColor: ['#42A5F5', '#66BB6A', '#FFA726'],
          data: [-29.99, 12.99, 17.00]
        }
      ]
    };

    this.options = {
      scales: {
        yAxes: [{
          ticks: {
            beginAtZero: true
          }
        }]
      }
    };
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
