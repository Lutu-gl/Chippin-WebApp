import {Component, OnInit} from '@angular/core';
import {GroupService} from "../../services/group.service";
import {UserService} from "../../services/user.service";
import {FriendshipService} from "../../services/friendship.service";
import {ActivatedRoute, Router} from "@angular/router";
import {ConfirmationService, MessageService} from "primeng/api";
import {AuthService} from "../../services/auth.service";
import {ExpenseDetailDto} from "../../dtos/expense";
import {GroupDto} from "../../dtos/group";
import defaultCallbacks from "chart.js/dist/plugins/plugin.tooltip";
import _default from "chart.js/dist/plugins/plugin.legend";

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

  personExpenseMapCash = new Map<string, number>();

  personExpenseFoodMapCash = new Map<string, number>();
  personExpenseTravelMapCash = new Map<string, number>();
  personExpenseEntertainmentMapCash = new Map<string, number>();
  personExpenseOtherMapCash = new Map<string, number>();
  personExpenseHealthMapCash = new Map<string, number>();
  personExpenseShoppingMapCash = new Map<string, number>();

  categoryExpenseMap = new Map<string, number>();


  charts: any[] = [];

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
    const colorsCat = this.getCategoryColor(0.8)

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


    let finalData = { data: graphData, options: graphOptions, type: "bar", description: this.getDescriptionForExpensesMadePerPerson()};
    this.charts.push(finalData);
  }

  private getDescriptionForExpensesMadePerPerson() {
    let map = this.personExpenseMap;

    let string = "This graph shows the amount of expenses each person has made in each category <br>" +
      "The person who has made the most expenses is <strong>" + [...map].reduce((a, b) => a[1] > b[1] ? a : b)[0] + "</strong> with <strong>" + Math.max(...map.values()) + "</strong> expenses."

    return string;
  }

  formatDataExpensesMadePerPersonCash() {
    const labelsCat = ["Food", "Travel", "Entertainment", "Health", "Shopping", "Other"];
    const colorsCat = this.getCategoryColor(0.8)

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

    let finalData = { data: graphData, options: graphOptions, type: "bar", description: this.getDescriptionForExpensesMadePerPersonCash() };
    this.charts.push(finalData);
  }

  private getDescriptionForExpensesMadePerPersonCash() {
    let map = this.personExpenseMapCash;

    let string = "This graph shows the amount of money each person has spent in each category <br>" +
      "The person who has spent the most money in total is <strong>" + [...map].reduce((a, b) => a[1] > b[1] ? a : b)[0] + "</strong> with <strong>" + Math.max(...map.values()) + "€</strong> spent."

    return string;
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
    let graphOptions: any;
    let type: string = "pie";
    let labels: string[] = [];
    let datasets: {
      label: string,
      data: number[],
      fill: boolean,
      borderColor?: any,
      // add 6 colors for the 6 categories
      backgroundColor: any[]
      hoverBackgroundColor: any[],
    }[] = [];

    let dataset: {
      label: string,
      data: number[],
      fill: boolean,
      borderColor?: any,
      backgroundColor: any[],
      hoverBackgroundColor: any[],
    };
    let label: string = "Expenses";
    let data: number[] = [];


    for (const [key, value] of this.categoryExpenseMap) {
      labels.push(key);
      data.push(value);
    }

    dataset = {
      backgroundColor: this.getCategoryColor(0.8),
      hoverBackgroundColor: this.getCategoryColor(1),
      label: label,
      data: data,
      fill: false
    };

    console.log(dataset);
    datasets.push(dataset);
    console.log(datasets)

    graphData = {labels: labels, datasets: datasets};
    graphOptions = {
      maintainAspectRatio: false,
      responsive: true,
      plugins: {
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

    let finalData: {data: any, options: any, type: any, description: any} = {data: graphData, options: graphOptions, type: type, description: this.getDescriptionForSpendEuroInCategory(data, labels)

    };
    this.charts.push(finalData);
  }

  getDescriptionForSpendEuroInCategory(data, labels) {
    let string =         "The group has spent a most money on <strong>" + labels[data.indexOf(Math.max(...data))] + "</strong>. Too be more precise, you spent <strong>"
      + Math.max(...data) + "€</strong> on this category. " + "<br>The least money was spent on <strong>" + labels[data.indexOf(Math.min(...data))] + "</strong> with <strong>" + Math.min(...data) + "€</strong>."
      + "<br> The total amount of money spent is <strong>" + data.reduce((a, b) => a + b, 0) + "€</strong>."

    return string;
  }



  backToGroup() {
    this.router.navigate(['../'], {relativeTo: this.route})
  }

  private formatDataForGraphs(): void {
    this.initAllMaps();

    for (let expense of this.expenses) {
      // Update category expense map
      this.updateMap(this.categoryExpenseMap, expense.category, expense.amount);

      // Update person expense map for num expenses
      this.updateMap(this.personExpenseMap, expense.payerEmail, 1);

      // Update person expense map for cash
      this.updateMap(this.personExpenseMapCash, expense.payerEmail, expense.amount);


      // Update category payer expense map based on the category
      this.fillInCategoryPayerExpenseMap(expense);

      // Update category payer expense map based on the category
      this.fillInCategoryPayerExpenseMapCash(expense);
    }
  }

  // this initialization is necessary to keep colors of category the same for each chart
  private initAllMaps() {
    this.categoryExpenseMap = new Map<string, number>();
    this.categoryExpenseMap.set('Food', 0);
    this.categoryExpenseMap.set('Travel', 0);
    this.categoryExpenseMap.set('Entertainment', 0);
    this.categoryExpenseMap.set('Health', 0);
    this.categoryExpenseMap.set('Shopping', 0);
    this.categoryExpenseMap.set('Other', 0);

    this.personExpenseMap = new Map<string, number>();
    for (let member of this.group.members) {
      this.personExpenseMap.set(member, 0);
    }

    this.personExpenseMapCash = new Map<string, number>();
    for (let member of this.group.members) {
      this.personExpenseMapCash.set(member, 0);
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

  private getCategoryColor(alpha): string[] {
    return ['rgba(15, 81, 138, ' + alpha + ')', 'rgba(75, 192, 192, ' + alpha + ')', 'rgba(54, 162, 235, ' + alpha + ')', 'rgba(255, 205, 86, ' + alpha + ')', 'rgba(255, 99, 132, ' + alpha + ')', 'rgba(255, 159, 64, ' + alpha + ')'];
  }
}
