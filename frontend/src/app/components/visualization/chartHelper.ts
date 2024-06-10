import {ExpenseDetailDto} from "../../dtos/expense";


export function groupExpensesByUserEmail(expenses: ExpenseDetailDto[]): { [userEmail: string]: ExpenseDetailDto[] } {
  let result: { [userEmail: string]: ExpenseDetailDto[] } = {};
  expenses.forEach(expense => {
    if (!result[expense.payerEmail]) {
      result[expense.payerEmail] = [];
    }
    result[expense.payerEmail].push(expense);
  })
  return result;
}

export function sumExpensesPerUserPerMonth(expenses: { [userEmail: string]: ExpenseDetailDto[] }): {
  [userEmail: string]: { [month: number]: number }
} {
  let result: { [userEmail: string]: { [month: number]: number } } = {};
  for (let userEmail in expenses) {
    result[userEmail] = {};
    expenses[userEmail].forEach(expense => {
      let date = new Date(expense.date);
      let month = date.getMonth();
      if (!result[userEmail][month]) {
        result[userEmail][month] = 0;
      }
      result[userEmail][month] += expense.amount;
    })
  }
  return result;

}

export function getRandomColorForEmail(email: string): string {
  let hash = 0;
  for (let i = 0; i < email.length; i++) {
    hash = email.charCodeAt(i) + ((hash << 5) - hash);
  }
  let color = '#';
  for (let i = 0; i < 3; i++) {
    let value = (hash >> (i * 8)) & 0xFF;
    color += ('00' + value.toString(16)).substr(-2);
  }
  return color;
}

export function getRandomColorForEmailx(email: string): string {
  let colors = [
    "#FF9999", "#99FF99", "#9999FF", "#FFFF99", "#FF99FF", "#99FFFF",
    "#CC6666", "#66CC66", "#6666CC", "#CCCC66", "#CC66CC", "#66CCCC",
    "#E0E0E0", "#B3B3B3", "#CCCCFF", "#CC99B3", "#FFFFE0", "#E0FFFF",
    "#996699", "#FFCCCC", "#66B2CC", "#E0E0FF", "#8080B3", "#FFB3FF",
    "#FFFFB3", "#B3FFFF", "#CC99CC", "#CC6666", "#66CCCC", "#6666FF",
    "#66CCFF", "#CCFFCC", "#CCB3FF", "#FFB3CC", "#FFCCB3", "#6682FF",
    "#66CCCC", "#B3CC66", "#FFCC66", "#FF9966", "#9999B3", "#B3B3B3",
    "#6699CC", "#99CC99", "#669966", "#999966", "#996666", "#996699",
    "#666699", "#666666"
  ]
  // Get a random index between 0 and the length of the colors array for the email (hash) only use the part before the @
  let index = email.split('@')[0]
    .split('').reduce((acc, char) => acc + char.charCodeAt(0), 0) * 7 % colors.length;
  return colors[index];

}

export function getHighestMonthAndSum(expenses: { [userEmail: string]: { [month: number]: number } }): [string, number] {
  let expensesPerMonth: { [month: number]: number } = {};
  for (let userEmail in expenses) {
    for (let month in expenses[userEmail]) {
      if (!expensesPerMonth[month]) {
        expensesPerMonth[month] = 0;
      }
      expensesPerMonth[month] += expenses[userEmail][month];
    }
  }
  let highestMonth = 0;
  let sum = 0;
  for (let month in expensesPerMonth) {
    if (expensesPerMonth[month] > sum) {
      highestMonth = parseInt(month);
      sum = expensesPerMonth[month];
    }
  }

  return [getMonthName(highestMonth), sum]
}

export function getMonthName(month: number): string {
  switch (month) {
    case 0:
      return 'January';
    case 1:
      return 'February';
    case 2:
      return 'March';
    case 3:
      return 'April';
    case 4:
      return 'May';
    case 5:
      return 'June';
    case 6:
      return 'July';
    case 7:
      return 'August';
    case 8:
      return 'September';
    case 9:
      return 'October';
    case 10:
      return 'November';
    case 11:
      return 'December';
  }
}
