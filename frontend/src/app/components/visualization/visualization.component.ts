import {Component, OnInit} from '@angular/core';
import {GroupService} from "../../services/group.service";
import {UserService} from "../../services/user.service";
import {FriendshipService} from "../../services/friendship.service";
import {ActivatedRoute, Router} from "@angular/router";
import {ConfirmationService, MessageService} from "primeng/api";
import {AuthService} from "../../services/auth.service";
import {ExpenseDetailDto} from "../../dtos/expense";
import {GroupDto} from "../../dtos/group";
import {ChartData, ChartOptions} from "chart.js";
import {
  getRandomColor,
  getRandomColorForEmail,
  groupExpensesByUserEmail,
  sumExpensesPerUserPerMonth
} from "./chartHelper";

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

  personExpenseFoodMapCash = new Map<string, number>();
  personExpenseTravelMapCash = new Map<string, number>();
  personExpenseEntertainmentMapCash = new Map<string, number>();
  personExpenseOtherMapCash = new Map<string, number>();
  personExpenseHealthMapCash = new Map<string, number>();
  personExpenseShoppingMapCash = new Map<string, number>();

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
        this.formatDataExpensesMadePerPersonCash()
        this.formatDataForExpensesPerUserPerMonth()
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
          enabled: true,
          mode: 'index',
          intersect: false,
          callbacks: {
            title: function(tooltipItems) {
              return tooltipItems[0].label;
            },
            label: function(context) {
              let label = context.dataset.label;
              let value = context.parsed.y;
              if (value == 0 || value == '0') {
                return ''
              }
              return `${label}: ${value}`;
            }
          }
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

  formatDataExpensesMadePerPersonCash() {
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
      let map = this.getExpenseMapForCategoryCash(label);

      map.forEach((value, key) => {
        let memberIndex = sortedMembers.findIndex(member => member === key);
        if (memberIndex !== -1) {
          data[memberIndex] = value;
        }
      });

      console.log(data);
      console.log(this.personExpenseMap);

      graphData.datasets.push({
        label: "Amount spend in " + label + " Category",
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
          enabled: true,
          mode: 'index',
          intersect: false,
          callbacks: {
            title: function(tooltipItems) {
              return tooltipItems[0].label;
            },
            label: function(context) {
              let label = context.dataset.label;
              let value = context.parsed.y;
              if (value == 0 || value == '0') {
                return ''
              }
              return `${label}: ${value}`;
            }
          }
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

  getExpenseMapForCategory(category: string): Map<string, number> {
    switch (category) {
      case "Food": return this.personExpenseFoodMap;
      case "Travel": return this.personExpenseTravelMap;
      case "Entertainment": return this.personExpenseEntertainmentMap;
      case "Health": return this.personExpenseHealthMap;
      case "Shopping": return this.personExpenseShoppingMap;
      case "Other": return this.personExpenseOtherMap;
      default: return new Map<string, number>(); // Save Fallback-Option
    }
  }

  getExpenseMapForCategoryCash(category: string): Map<string, number> {
    switch (category) {
      case "Food": return this.personExpenseFoodMapCash;
      case "Travel": return this.personExpenseTravelMapCash;
      case "Entertainment": return this.personExpenseEntertainmentMapCash;
      case "Health": return this.personExpenseHealthMapCash;
      case "Shopping": return this.personExpenseShoppingMapCash;
      case "Other": return this.personExpenseOtherMapCash;
      default: return new Map<string, number>(); // Save Fallback-Option
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

  // Add chart for amount of expenses paid per user
  // This will be a stacked bar chart with the x-axis being the months and the y-axis being the amount of expenses paid
  // Each user will have a different color in the bar chart
  // The data will be stacked on top of each other
  formatDataForExpensesPerUserPerMonth() {
    let graphData: ChartData<"bar">;
    let graphOptions: ChartOptions<"bar">;

    let expensesByUser = groupExpensesByUserEmail(this.expenses);

    let expensesPerUserPerMonth = sumExpensesPerUserPerMonth(expensesByUser);
    console.log(expensesPerUserPerMonth);

    // Create the data for the graph
    let labels = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
    let datasets = [];
    let colors = ['#0f518a', '#4bc0c0', '#36a2eb', '#ffcd56', '#ff6384', '#ff9f40', '#ffcd56', '#ff6384', '#ff9f40', '#ffcd56', '#ff6384', '#ff9f40'];

    for (let userEmail in expensesPerUserPerMonth) {
      let data = [];
      for (let i = 0; i < 12; i++) {
        data.push(expensesPerUserPerMonth[userEmail][i] || 0);
      }
      datasets.push({
        label: userEmail,
        data: data,
        backgroundColor: getRandomColorForEmail(userEmail)
      })
    }



    // Mock data
    // const labels = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul'];
    // const datasets = [
    //   {
    //     label: 'User 1',
    //     data: [65, 59, 80, 81, 56, 55, 40],
    //     backgroundColor: '#0f518a'
    //   },
    //   {
    //     label: 'User 2',
    //     data: [28, 48, 40, 19, 86, 27, 90],
    //     backgroundColor: '#4bc0c0'
    //   }
    // ];

    graphData = {labels, datasets};
    graphOptions = {
      responsive: true,
      plugins: {
        tooltip: {
          mode: "index",
          intersect: false
        },
        legend: {
          labels: {
            color: this.textColor
          }
        },
        title: {
          display: true,
          text: 'Expenses per User per Month',
          color: this.textColor
        }
      },
      scales: {
        y: {
          stacked: true,
          beginAtZero: true,
        },
        x: {
          stacked: true
        }
      }
    };

    let finalData: {data: ChartData<"bar">, options: ChartOptions<"bar">, type: "bar"} = {data: graphData, options: graphOptions, type: "bar"};
    this.charts.unshift(finalData);
  }

  backToGroup() {
    this.router.navigate(['../'], {relativeTo: this.route})
  }

  private formatDataForGraphs(): void {
    for (let expense of this.expenses) {
      // Update category expense map
      this.updateMap(this.categoryExpenseMap, expense.category, expense.amount);

      // Update person expense map for num expenses
      this.updateMap(this.personExpenseMap, expense.payerEmail, 1);

      // Update category payer expense map based on the category
      this.fillInCategoryPayerExpenseMap(expense);

      // Update category payer expense map based on the category
      this.fillInCategoryPayerExpenseMapCash(expense);
    }
  }

  private fillInCategoryPayerExpenseMap(expense: ExpenseDetailDto): void {
    const map = this.getMapForCategory(expense.category);
    this.updateMap(map, expense.payerEmail, 1);
  }

  private fillInCategoryPayerExpenseMapCash(expense: ExpenseDetailDto): void {
    const map = this.getExpenseMapForCategoryCash(expense.category);
    this.updateMap(map, expense.payerEmail, expense.amount);
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
