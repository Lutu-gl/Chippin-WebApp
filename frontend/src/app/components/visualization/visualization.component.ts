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

  documentStyle = getComputedStyle(document.documentElement);
  textColor = this.documentStyle.getPropertyValue('--text-color');
  textColorSecondary = this.documentStyle.getPropertyValue('--text-color-secondary');
  surfaceBorder = this.documentStyle.getPropertyValue('--surface-border');

  id: number;
  group: GroupDto;
  expenses: ExpenseDetailDto[];
  // documentStyle: any;

  // different data sets for visualization
  personExpenseMap = new Map<string, number>();

  personExpenseFoodMap = new Map<string, number>();
  personExpenseTravelMap = new Map<string, number>();
  personExpenseEntertainmentMap = new Map<string, number>();
  personExpenseOtherMap = new Map<string, number>();
  personExpenseHealthMap = new Map<string, number>();
  personExpenseShoppingMap = new Map<string, number>();

  categoryExpenseMap = new Map<string, number>();


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
        this.formatDataForSpendEuroInCategory();
        this.formatDataExpensesMadePerPerson()
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

  formatDataExpensesMadePerPerson() {
    const labelsCat = ["Food", "Travel", "Entertainment", "Health", "Shopping", "Other"];
    const colorsCat = ["#0f518a", "#4bc0c0", "#36a2eb", "#ffcd56", "#ff6384", "#ff9f40"];

    let graphData = {
      labels: [],
      datasets: []
    };

    const sortedMembers = [...this.group.members].sort();

    graphData.labels = sortedMembers;

    labelsCat.forEach((label, index) => {
      let data = new Array(sortedMembers.length).fill(0);
      let map = this.getExpenseMapForCategory(label);

      map.forEach((value, key) => {
        let memberIndex = sortedMembers.findIndex(member => member === key);
        if (memberIndex !== -1) {
          data[memberIndex] = value;
        }
      });

      console.log(data);
      console.log(this.personExpenseMap);

      graphData.datasets.push({
        label: "Expenses Made in " + label + " Category",
        data: data,
        fill: false,
        borderColor: '4bc0c0',
        backgroundColor: colorsCat[index]
      });
    });

    let graphOptions = {
      maintainAspectRatio: true,
      responsive: false,
      plugins: {
        tooltip: {
          mode: 'index',
          intersect: false
        },
        legend: {
          labels: {
            color: this.textColor
          }
        }
      },
      scales: {
        x: {
          stacked: true,
          ticks: {
            color: this.textColorSecondary
          },
          grid: {
            color: this.surfaceBorder,
            drawBorder: false
          }
        },
        y: {
          stacked: true,
          ticks: {
            color: this.textColorSecondary
          },
          grid: {
            color: this.surfaceBorder,
            drawBorder: false
          }
        }
      }
    };

    let finalData = { data: graphData, options: graphOptions, type: "bar" };
    this.charts.push(finalData);
  }

// Hilfsmethode, um den richtigen Map basierend auf der Kategorie zu erhalten
  getExpenseMapForCategory(category: string): Map<string, number> {
    switch (category) {
      case "Food": return this.personExpenseFoodMap;
      case "Travel": return this.personExpenseTravelMap;
      case "Entertainment": return this.personExpenseEntertainmentMap;
      case "Health": return this.personExpenseHealthMap;
      case "Shopping": return this.personExpenseShoppingMap;
      case "Other": return this.personExpenseOtherMap;
      default: return new Map<string, number>(); // Sichere Rückfall-Option
    }
  }


  formatDataForSpendEuroInCategory() {
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


    for (const [key, value] of this.categoryExpenseMap) {
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

  private formatDataForGraphs(): void {
    for (let expense of this.expenses) {
      // Update category expense map
      this.updateMap(this.categoryExpenseMap, expense.category, expense.amount);

      // Update person expense map
      this.updateMap(this.personExpenseMap, expense.payerEmail, 1);

      // Update category payer expense map based on the category
      this.fillInCategoryPayerExpenseMap(expense);
    }
  }

  private fillInCategoryPayerExpenseMap(expense: ExpenseDetailDto): void {
    const map = this.getMapForCategory(expense.category);
    this.updateMap(map, expense.payerEmail, 1);
  }

  private getMapForCategory(category: string): Map<string, number> {
    switch (category) {
      case "Food": return this.personExpenseFoodMap;
      case "Travel": return this.personExpenseTravelMap;
      case "Entertainment": return this.personExpenseEntertainmentMap;
      case "Health": return this.personExpenseHealthMap;
      case "Shopping": return this.personExpenseShoppingMap;
      case "Other": return this.personExpenseOtherMap;
      default: throw new Error(`Unknown category: ${category}`);
    }
  }

  private updateMap(map: Map<string, number>, key: string, increment: number): void {
    const currentValue = map.get(key) || 0;
    map.set(key, currentValue + increment);
  }
}
