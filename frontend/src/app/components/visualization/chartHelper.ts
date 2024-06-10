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


export function getRandomColor(): string {
  return '#' + Math.floor(Math.random() * 16777215).toString(16);
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
