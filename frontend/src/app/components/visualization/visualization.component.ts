import {Component, OnInit} from '@angular/core';
import {GroupService} from "../../services/group.service";
import {UserService} from "../../services/user.service";
import {FriendshipService} from "../../services/friendship.service";
import {ActivatedRoute, Router} from "@angular/router";
import {ConfirmationService, MessageService} from "primeng/api";
import {AuthService} from "../../services/auth.service";
import {ExpenseDetailDto} from "../../dtos/expense";
import {GroupDto} from "../../dtos/group";

@Component({
  selector: 'app-visualization',
  templateUrl: './visualization.component.html',
  styleUrl: './visualization.component.scss'
})
export class VisualizationComponent implements OnInit {
  constructor(
    private service: GroupService,
    protected userService: UserService,
    private friendshipService: FriendshipService,
    private router: Router,
    private route: ActivatedRoute,
    private messageService: MessageService,
    protected authService: AuthService,
    private confirmationService: ConfirmationService,
  ) {
  }

  id: number;
  group: GroupDto;
  expenses: ExpenseDetailDto[];
  documentStyle: any;

  charts: any[] = [
    {
      type: 'bar',
      data: {
        labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul'],
        datasets: [
          {
            label: 'Beispiel Daten',
            data: [65, 59, 80, 81, 56, 55, 40],
            fill: false,
            borderColor: '#4bc0c0'
          }
        ]
      },
      options: {
        responsive: true,
        scales: {
          y: {
            beginAtZero: true
          }
        }
      }
    },
    {
      type: 'line',
      data: {
        labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul'],
        datasets: [
          {
            label: 'Beispiel Daten',
            data: [65, 59, 80, 81, 56, 55, 40],
            fill: false,
            borderColor: '#4bc0c0'
          }
        ]
      },
      options: {
        responsive: true,
        scales: {
          y: {
            beginAtZero: true
          }
        }
      }
    },

  ];

  ngOnInit(): void {
    this.documentStyle = getComputedStyle(document.documentElement);
    this.route.params.subscribe({
      next: params => {
        this.id = +params['id'];
        //load group
        this.service.getById(this.id).subscribe({
          next: res => {
            this.group = res;
          },
          error: error => {
            if (error && error.error && error.error.errors) {
              for (let i = 0; i < error.error.errors.length; i++) {
                this.messageService.add({severity: 'error', summary: 'Error', detail: `${error.error.errors[i]}`});
              }
            } else if (error && error.error && error.error.message) {
              this.messageService.add({severity: 'error', summary: 'Error', detail: `${error.error.message}`});
            } else if (error && error.error && error.error.detail) {
              this.messageService.add({severity: 'error', summary: 'Error', detail: `${error.error.detail}`});
            } else {
              console.error('Could not get group', error);
              this.messageService.add({severity: 'error', summary: 'Error', detail: `Could not load group!`});
            }
          }
        })
        //load expenses
        this.getExpenses()
      },
      error: error => {
        if (error && error.error && error.error.errors) {
          for (let i = 0; i < error.error.errors.length; i++) {
            this.messageService.add({severity: 'error', summary: 'Error', detail: `${error.error.errors[i]}`});
          }
        } else if (error && error.error && error.error.message) {
          this.messageService.add({severity: 'error', summary: 'Error', detail: `${error.error.message}`});
        } else if (error && error.error && error.error.detail) {
          this.messageService.add({severity: 'error', summary: 'Error', detail: `${error.error.detail}`});
        } else {
          console.error('Could not get group ID', error);
          this.messageService.add({severity: 'error', summary: 'Error', detail: `Could not load data!`});
        }
      }
    });
  }

  getExpenses() {

    this.service.getAllExpensesById(this.id).subscribe({
      next: res => {
        this.expenses = res;
        this.formatDataForGraphs();
      },
      error: error => {
        if (error && error.error && error.error.errors) {
          for (let i = 0; i < error.error.errors.length; i++) {
            this.messageService.add({severity: 'error', summary: 'Error', detail: `${error.error.errors[i]}`});
          }
        } else if (error && error.error && error.error.message) {
          this.messageService.add({severity: 'error', summary: 'Error', detail: `${error.error.message}`});
        } else if (error && error.error && error.error.detail) {
          this.messageService.add({severity: 'error', summary: 'Error', detail: `${error.error.detail}`});
        } else {
          console.error('Could not load expenses', error);
          this.messageService.add({severity: 'error', summary: 'Error', detail: `Could not load expenses!`});
        }
      }
    })
  }

  formatDataForGraphs() {
    //Expenses per category
    let graphData: {
      labels: string[],
      datasets: any[]
    }
    let graphOptions: {
      responsive: boolean,
      scales: any
    };
    let type: string = "bar";
    let labels: string[] = [];
    let datasets: {
      label: string,
      data: number[],
      fill: boolean,
      borderColor?: any,
      backgroundColor?: any,
    }[] = [];
    let dataset: {
      label: string,
      data: number[],
      fill: boolean,
      borderColor?: any,
      backgroundColor?: any,
    };
    let label: string = "Expenses";
    let data: number[] = [];

    let categoryExpenseMap = new Map<string, number>();

    for (let expense of this.expenses) {
      //Generate map <category, amount>
      if (categoryExpenseMap.has(expense.category)) {
        let currentValue = categoryExpenseMap.get(expense.category);
        categoryExpenseMap.set(expense.category, currentValue + expense.amount);
      } else {
        categoryExpenseMap.set(expense.category, expense.amount);
      }
    }
    for (const [key, value] of categoryExpenseMap) {
      labels.push(key);
      data.push(value);
    }

    dataset = {
      label: label,
      data: data,
      fill: false,
      borderColor: '4bc0c0',
      backgroundColor: '#0f518a'
    };

    console.log(dataset);
    datasets.push(dataset);
    console.log(datasets)

    graphData = {labels: labels, datasets: datasets};
    graphOptions = {responsive: false, scales: {
        y: {
          beginAtZero: true
        }
      }};

    let finalData: {data: any, options: any, type: any} = {data: graphData, options: graphOptions, type: type};
    this.charts.push(finalData);
  }

  backToGroup() {
    this.router.navigate(['../'], {relativeTo: this.route})
  }
}
