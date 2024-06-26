import {Component, OnInit} from '@angular/core';
import {GroupService} from "../../services/group.service";
import {ActivatedRoute, Router} from "@angular/router";
import {MessageService} from "primeng/api";
import {AuthService} from "../../services/auth.service";
import {ExpenseDetailDto} from "../../dtos/expense";
import {GroupDto} from "../../dtos/group";
import {ChartData, ChartOptions} from "chart.js";
import {getRandomColorForEmail, groupExpensesByUserEmail, sumExpensesPerUserPerMonth} from "./chartHelper";
import {PaymentDto} from "../../dtos/payment";
import {PaymentService} from "../../services/payment.service";

@Component({
  selector: 'app-visualization',
  templateUrl: './visualization.component.html',
  styleUrl: './visualization.component.scss'
})
export class VisualizationComponent implements OnInit {
  constructor(
    private service: GroupService,
    private paymentService: PaymentService,
    private router: Router,
    private route: ActivatedRoute,
    private messageService: MessageService,
    protected authService: AuthService,
  ) {
  }

  documentStyle = getComputedStyle(document.documentElement);
  textColor = this.documentStyle.getPropertyValue('--text-color');
  textColorSecondary = this.documentStyle.getPropertyValue('--text-color-secondary');
  surfaceBorder = this.documentStyle.getPropertyValue('--surface-border');

  id: number;
  group: GroupDto;
  expenses: ExpenseDetailDto[];
  allExpenses: ExpenseDetailDto[];
  payments: PaymentDto[];
  allPayments: PaymentDto[];
  // documentStyle: any;

  minimumExpensesSatisfied = true;

  // different data sets for visualization
  personExpensePayedMap = new Map<string, number>();

  personExpensePayedFoodMap = new Map<string, number>();
  personExpensePayedTravelMap = new Map<string, number>();
  personExpensePayedEntertainmentMap = new Map<string, number>();
  personExpensePayedOtherMap = new Map<string, number>();
  personExpensePayedHealthMap = new Map<string, number>();
  personExpensePayedShoppingMap = new Map<string, number>();

  personExpensePayedMapCash = new Map<string, number>();

  personExpensePayedFoodMapCash = new Map<string, number>();
  personExpensePayedTravelMapCash = new Map<string, number>();
  personExpensePayedEntertainmentMapCash = new Map<string, number>();
  personExpensePayedOtherMapCash = new Map<string, number>();
  personExpensePayedHealthMapCash = new Map<string, number>();
  personExpensePayedShoppingMapCash = new Map<string, number>();

  personAmountPayedMapCash = new Map<string, number>();

  personAmountSpendFoodMap = new Map<string, number>();
  personAmountSpendTravelMap = new Map<string, number>();
  personAmountSpendEntertainmentMap = new Map<string, number>();
  personAmountSpendOtherMap = new Map<string, number>();
  personAmountSpendHealthMap = new Map<string, number>();
  personAmountSpendShoppingMap = new Map<string, number>();

  categoryExpenseMap = new Map<string, number>();


  charts: any[] = [];
  rangeDates: Date[] | undefined;
  today: Date = new Date();
  minDate: Date = new Date();

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
        this.getExpenses([new Date(1970), new Date()])
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

  getPayments(dates: Date[]) {
    this.paymentService.getPaymentsByGroupId(this.id).subscribe({
      next: res => {

        // Convert date from backend to Date()
        res.forEach(p => {
          p.date = new Date(p.date);
          if (p.date.getTime() < this.minDate.getTime()) {
            this.minDate = p.date;
          }
        })

        this.allPayments = res;
        this.payments = res.filter(p => p.date.getTime() >= dates[0].getTime() && p.date.getTime() <= dates[1].getTime());


        this.formatDataForDebtsPerUserOverTime()

      }
    })
  }

  getExpenses(dates: Date[]) {

    this.service.getAllExpensesById(this.id).subscribe({
      next: res => {

        this.getPayments([new Date(1970), new Date()])

        //Convert date from backend to Date()
        res.forEach(e => {
          e.date = new Date(e.date);
          if (e.date.getTime() < this.minDate.getTime()) {
            this.minDate = e.date;
          }
        })


        if (!this.rangeDates) {
          this.rangeDates = [this.minDate, this.today];
        }

        if (res.length >= 5) {
          this.minimumExpensesSatisfied = true;
        } else {
          this.minimumExpensesSatisfied = false;
          return
        }

        this.allExpenses = res;
        //filter date
        const endDate = new Date(dates[1].getTime());
        endDate.setDate(endDate.getDate() + 1);
        //this.expenses = res.filter(e => e.date.getTime() >= dates[0].getTime() && e.date.getTime() <= dates[1].getTime());
        this.expenses = res.filter(e =>
          e.date.getTime() >= dates[0].getTime() && e.date.getTime() <= endDate.getTime()
        );

        this.formatDataForGraphs();
        this.formatDataForSpendEuroInCategory()
        this.formatDataExpensesPayedPerPerson()
        // this.formatDataExpensesPayedPerPersonCash()
        this.formatDataAmountSpendPerPerson()
        this.formatDataForExpensesPerUserPerMonth()

        this.charts = this.charts.map(chart => {
          return {
            ...chart,
            data: { ...chart.data }
          };
        });
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

  formatDataForDebtsPerUserOverTime() {
    let graphData: ChartData<"line", { x: string, y: number }[]>
    let graphOptions: ChartOptions<"line">

    let dataPointsPerUser = new Map<string, { x: string, y: number }[]>();
    // Initialize data points for each user
    for (let member of this.group.members) {
      dataPointsPerUser.set(member, []);
    }

    // Loop over all expenses and add them to the data points
    for (let expense of this.allExpenses.filter(e => e.deleted === false)) {
      let previousAmount = dataPointsPerUser.get(expense.payerEmail).length > 0 ?
        dataPointsPerUser.get(expense.payerEmail)[dataPointsPerUser.get(expense.payerEmail).length - 1].y : 0;
      dataPointsPerUser.get(expense.payerEmail).push({
        x: expense.date.toISOString(),
        y: previousAmount + expense.amount - expense.amount * expense.participants[expense.payerEmail]
      });
      for (let participant in expense.participants) {
        if (participant !== expense.payerEmail) {
          let previousAmount = dataPointsPerUser.get(participant).length > 0 ?
            dataPointsPerUser.get(participant)[dataPointsPerUser.get(participant).length - 1].y : 0;
          dataPointsPerUser.get(participant).push({
            x: expense.date.toISOString(),
            y: previousAmount - expense.amount * expense.participants[participant]
          });
        }
      }
    }


    // Correct for payments
    for (let payment of this.allPayments.filter(p => p.deleted === false)) {
      // Correct for payer
      let payerDataPoints = dataPointsPerUser.get(payment.payerEmail)
      payerDataPoints.forEach(dataPoint => {
        if (new Date(dataPoint.x)  > payment.date) {
          dataPoint.y += payment.amount
        }
      })
      // Correct for receiver
      let receiverDataPoints = dataPointsPerUser.get(payment.receiverEmail)
      receiverDataPoints.forEach(dataPoint => {
        if (new Date(dataPoint.x) > payment.date) {
          dataPoint.y -= payment.amount
        }
      })
      // Add data points for the payment
      dataPointsPerUser.get(payment.payerEmail).push({
        x: payment.date.toISOString(),
        y: dataPointsPerUser.get(payment.payerEmail)[dataPointsPerUser.get(payment.payerEmail).length - 1].y + payment.amount
      })
      dataPointsPerUser.get(payment.receiverEmail).push({
        x: payment.date.toISOString(),
        y: dataPointsPerUser.get(payment.receiverEmail)[dataPointsPerUser.get(payment.receiverEmail).length - 1].y - payment.amount
      })
      // Sort the data points by date
      dataPointsPerUser.get(payment.payerEmail).sort((a, b) => new Date(a.x).getTime() - new Date(b.x).getTime())
      dataPointsPerUser.get(payment.receiverEmail).sort((a, b) => new Date(a.x).getTime() - new Date(b.x).getTime())
    }

    // Sort dataPoints
    for (let [user, dataPoints] of dataPointsPerUser) {
      dataPoints.sort((a, b) => new Date(a.x).getTime() - new Date(b.x).getTime())
    }

    // Filter date range with 24 hours added to the end date
    for (let [user, dataPoints] of dataPointsPerUser) {
      dataPointsPerUser.set(user, dataPoints.filter(dataPoint => new Date(dataPoint.x) >= this.rangeDates[0] && new Date(dataPoint.x) <= new Date(this.rangeDates[1].getTime() + 24 * 60 * 60 * 1000)))
    }

    let datasets = [];
    for (let [user, dataPoints] of dataPointsPerUser) {
      datasets.push({
        label: user,
        data: dataPoints,
        borderColor: getRandomColorForEmail(user),
        tension: 0.3
      })
    }

    graphData = {
      labels: [],
      datasets: datasets
    }

    graphOptions = {
      maintainAspectRatio: false,
      aspectRatio: 0.6,
      plugins: {
        legend: {
          labels: {
            color: "black"
          }
        }
      },
      scales: {
        x: {
          type: "time",
          time: {
            unit: "day",
          },
          ticks: {
            color: this.textColor
          },
          title: {
            display: true,
            text: "Time",
            color: this.textColor
          }
        },
        y: {
          ticks: {
            color: this.textColor
          },
          title: {
            display: true,
            text: "Amount owed/debt in €",
            color: this.textColor
          }
        }
      }
    };

    let finalData = {
      data: graphData,
      options: graphOptions,
      type: "line",
      title: "Debts of group members over time",
      description: "This graph shows how the debts of each group member change over time."
    };

    let chart = this.charts.findIndex(c => c.title === finalData.title);
    if (chart !== -1) {
      this.charts[chart] = finalData;
    } else {
      this.charts.push(finalData);
    }

  }

  formatDataExpensesPayedPerPerson() {

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

      graphData.datasets.push({
        label: label,
        data: data,
        fill: false,
        borderColor: '4bc0c0',
        backgroundColor: colorsCat[index]
      });
    });


    let graphOptions = {
      indexAxis: 'y',
      maintainAspectRatio: true,
      responsive: false,
      plugins: {
        tooltip: {
          enabled: true,
          mode: 'index',
          callbacks: {
            title: function (tooltipItems) {
              return tooltipItems[0].label;
            },
            label: function (context) {
              let label = context.dataset.label;
              let value = context.parsed.x;
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
          },
          title: {
            display: true,
            text: 'Amount of expenses payed',
            color: this.textColor
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
          },
          title: {
            display: true,
            text: 'Members of group',
            color: this.textColor
          }
        }
      }
    };


    let dates: Date[] | undefined = [this.minDate, this.today];

    let finalData = {
      data: graphData,
      options: graphOptions,
      type: "bar",
      description: this.getDescriptionForExpensesMadePerPerson(),
      title: "Number of Expenses Payed by Member",
      dates: dates
    };
    let chart = this.charts.findIndex(c => c.title === finalData.title);
    if (chart !== -1) {
      this.charts[chart] = finalData;
    } else {
      this.charts.push(finalData);
    }
  }

  private getDescriptionForExpensesMadePerPerson() {
    return "This bar chart shows the number of expenditures made by each group member in different categories.";
  }

  getExpenseMapForCategory(category: string): Map<string, number> {

    switch (category) {
      case "Food":
        return this.personExpensePayedFoodMap;
      case "Travel":
        return this.personExpensePayedTravelMap;
      case "Entertainment":
        return this.personExpensePayedEntertainmentMap;
      case "Health":
        return this.personExpensePayedHealthMap;
      case "Shopping":
        return this.personExpensePayedShoppingMap;
      case "Other":
        return this.personExpensePayedOtherMap;
      default:
        return new Map<string, number>(); // Save Fallback-Option
    }
  }

  getExpenseMapForCategoryCash(category: string): Map<string, number> {


    switch (category) {
      case "Food":
        return this.personExpensePayedFoodMapCash;
      case "Travel":
        return this.personExpensePayedTravelMapCash;
      case "Entertainment":
        return this.personExpensePayedEntertainmentMapCash;
      case "Health":
        return this.personExpensePayedHealthMapCash;
      case "Shopping":
        return this.personExpensePayedShoppingMapCash;
      case "Other":
        return this.personExpensePayedOtherMapCash;
      default:
        return new Map<string, number>(); // Save Fallback-Option
    }
  }

  getAmountSpendMapForCategoryCash(category: string): Map<string, number> {


    switch (category) {
      case "Food":
        return this.personAmountSpendFoodMap;
      case "Travel":
        return this.personAmountSpendTravelMap;
      case "Entertainment":
        return this.personAmountSpendEntertainmentMap;
      case "Health":
        return this.personAmountSpendHealthMap;
      case "Shopping":
        return this.personAmountSpendShoppingMap;
      case "Other":
        return this.personAmountSpendOtherMap;
      default:
        return new Map<string, number>(); // Save Fallback-Option
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

    datasets.push(dataset);

    graphData = {labels: labels, datasets: datasets};
    graphOptions = {
      maintainAspectRatio: false,
      responsive: true,
      plugins: {
        tooltip: {
          enabled: true,
          callbacks: {
            label: function(tooltipItem) {
              let value = tooltipItem.raw;
              return `Amount: ${value} €`;
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

    let dates: Date[] | undefined = [this.minDate, this.today];

    let finalData: any = {
      data: graphData,
      options: graphOptions,
      type: type,
      description: this.getDescriptionForSpendEuroInCategory(),
      title: "Spending Distribution Across Categories",
      dates: dates
    };
    let chart = this.charts.findIndex(c => c.title === finalData.title);
    if (chart !== -1) {
      this.charts[chart] = finalData;
    } else {
      this.charts.push(finalData);
    }
  }

  getDescriptionForSpendEuroInCategory() {



    return"This pie chart shows the breakdown of your spending by category. The different sections of the chart show the relative share of each spending category in the total budget.";
  }

  private formatDataAmountSpendPerPerson() {

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
      let map = this.getAmountSpendMapForCategoryCash(label);

      map.forEach((value, key) => {
        let memberIndex = sortedMembers.findIndex(member => member === key);
        if (memberIndex !== -1) {
          data[memberIndex] = value;
        }
      });


      graphData.datasets.push({
        label: label,
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
            title: function (tooltipItems) {
              return tooltipItems[0].label;
            },
            label: function (context) {
              let label = context.dataset.label;
              let value = context.parsed.y;
              if (value == 0 || value == '0') {
                return ''
              }
              return `${label}: ${value} €`;
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
          },
          title: {
            display: true,
            text: 'Members of group',
            color: this.textColor
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
          },
          title: {
            display: true,
            text: 'Amount of money spent in €',
            color: this.textColor
          }
        }
      }
    };

    let dates: Date[] | undefined = [this.minDate, this.today];

    let finalData = {
      data: graphData,
      options: graphOptions,
      type: "bar",
      description: this.getDescriptionForAmountSpendPerPerson(),
      title: "Detailed Spending Breakdown by Member",
      dates: dates
    };
    let chart = this.charts.findIndex(c => c.title === finalData.title);
    if (chart !== -1) {
      this.charts[chart] = finalData;
    } else {
      this.charts.push(finalData);
    }
  }

  getDescriptionForAmountSpendPerPerson() {
    return "This stacked bar chart shows the spending of each group member, broken down by category.";
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

    // Create the data for the graph
    let labels = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
    let datasets = [];
    let colors = ['#0f518a', '#4bc0c0', '#36a2eb', '#ffcd56', '#ff6384', '#ff9f40', '#ffcd56', '#ff6384', '#ff9f40', '#ffcd56', '#ff6384', '#ff9f40'];
    colors.filter(t => t);

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

    graphData = {labels, datasets};
    graphOptions = {
      maintainAspectRatio: true,
      responsive: false,
      plugins: {
        tooltip: {
          enabled: true,
          mode: 'index',
          intersect: false,
          callbacks: {
            title: function (tooltipItems) {
              return tooltipItems[0].label;
            },
            label: function (context) {
              let label = context.dataset.label;
              let value = context.parsed.y;
              if (value == 0) {
                return ''
              }
              return `${label}: ${value} €`;
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
          },
          title: {
            display: true,
            text: 'Months',
            color: this.textColor
          }
        },
        y: {
          stacked: true,
          ticks: {
            color: this.textColorSecondary
          },
          grid: {
            color: this.surfaceBorder,
          },
          title: {
            display: true,
            text: 'Amount of money spent in €',
            color: this.textColor
          }
        }
      }
    };

    let finalData = {
      data: graphData,
      options: graphOptions,
      type: "bar",
      title: "Monthly Expenses Breakdown by Member",
      description: `This stacked bar chart shows the monthly spending of each group member.`
    };
    let chart = this.charts.findIndex(c => c.title === finalData.title);
    if (chart !== -1) {
      this.charts[chart] = finalData;
    } else {
      this.charts.push(finalData);
    }
  }

  backToGroup() {
    this.router.navigate(['../'], {relativeTo: this.route})
  }

  private formatDataForGraphs(): void {


    this.initAllMaps();

    for (let expense of this.expenses.filter(expense => !expense.deleted)) {
      // Update category expense map
      this.updateMap(this.categoryExpenseMap, expense.category, expense.amount);

      // Update person expense map for num expenses
      this.updateMap(this.personExpensePayedMap, expense.payerEmail, 1);

      // Update person expense map for cash
      this.updateMap(this.personExpensePayedMapCash, expense.payerEmail, expense.amount);

      // Update person amount spend
      this.updateMapAmount(this.personAmountPayedMapCash, expense.payerEmail, expense);

      // Update person amount spend per category
      this.fillInCategoryAmountSpendMap(expense);

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

    this.personExpensePayedMap = new Map<string, number>();
    this.personExpensePayedMapCash = new Map<string, number>();

    for (let member of this.group.members) {
      this.personExpensePayedMap.set(member, 0);
      this.personExpensePayedMapCash.set(member, 0);
      this.personAmountPayedMapCash.set(member, 0);

      this.personAmountSpendEntertainmentMap.set(member, 0);
      this.personAmountSpendFoodMap.set(member, 0);
      this.personAmountSpendHealthMap.set(member, 0);
      this.personAmountSpendShoppingMap.set(member, 0);
      this.personAmountSpendTravelMap.set(member, 0);
      this.personAmountSpendOtherMap.set(member, 0);

      this.personExpensePayedEntertainmentMap.set(member, 0);
      this.personExpensePayedFoodMap.set(member, 0);
      this.personExpensePayedHealthMap.set(member, 0);
      this.personExpensePayedShoppingMap.set(member, 0);
      this.personExpensePayedTravelMap.set(member, 0);
      this.personExpensePayedOtherMap.set(member, 0);
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

  private fillInCategoryAmountSpendMap(expense: ExpenseDetailDto) {
    const map = this.getAmountSpendMapForCategoryCash(expense.category);
    this.updateMapAmount(map, expense.payerEmail, expense);
  }

  private getMapForCategory(category: string): Map<string, number> {
    switch (category) {
      case "Food":
        return this.personExpensePayedFoodMap;
      case "Travel":
        return this.personExpensePayedTravelMap;
      case "Entertainment":
        return this.personExpensePayedEntertainmentMap;
      case "Health":
        return this.personExpensePayedHealthMap;
      case "Shopping":
        return this.personExpensePayedShoppingMap;
      case "Other":
        return this.personExpensePayedOtherMap;
      default:
        throw new Error(`Unknown category: ${category}`);
    }
  }

  private updateMap(map: Map<string, number>, key: string, increment: number): void {
    const currentValue = map.get(key) || 0;
    map.set(key, currentValue + increment);
  }

  private updateMapAmount(personAmountPayedMapCash: Map<string, number>, payerEmail: string, expense: ExpenseDetailDto) {


    for (let participant in expense.participants) {
      let amount = expense.participants[participant];
      let currentAmount = personAmountPayedMapCash.get(participant) || 0;
      personAmountPayedMapCash.set(participant, Number((currentAmount + amount * expense.amount).toFixed(2)));
    }
  }

  private getCategoryColor(alpha): string[] {
    return ['rgba(15, 81, 138, ' + alpha + ')', 'rgba(75, 192, 192, ' + alpha + ')', 'rgba(54, 162, 235, ' + alpha + ')', 'rgba(255, 205, 86, ' + alpha + ')', 'rgba(255, 99, 132, ' + alpha + ')', 'rgba(255, 159, 64, ' + alpha + ')'];
  }

  onDateSelect($event: Date) {
    if (this.rangeDates && this.rangeDates.length === 2 && this.rangeDates[0] && this.rangeDates[1]) {
      this.getExpenses(this.rangeDates);
    }
    this.formatDataForDebtsPerUserOverTime()
  }

  calcPlaceholderForDateInput() {
    if(this.rangeDates && this.rangeDates[0] && this.rangeDates[1]) {
      return this.rangeDates[0].toLocaleDateString() + ' - ' + this.rangeDates[1].toLocaleDateString();
    }
    return ''
  }
}
