import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Globals } from '../global/globals';
import { ExpenseCreateDto, ExpenseDetailDto } from '../dtos/expense';
import { Observable, map } from 'rxjs';
import { UserSelection } from '../dtos/user';

@Injectable({
  providedIn: 'root'
})
export class ExpenseService {

  private expenseBaseUri: string = this.globals.backendUri + '/expense';
  private expenseBaseUriGroup: string = this.globals.backendUri + '/group';


  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  createExpense(expense: ExpenseCreateDto): Observable<ExpenseCreateDto> {

    const formData = new FormData();
    formData.append('name', expense.name);
    formData.append('amount', expense.amount ? expense.amount.toString() : '');
    formData.append('category', expense.category ? expense.category : '');
    formData.append('payerEmail', expense.payerEmail ? expense.payerEmail : '');
    formData.append('groupId', expense.groupId ? expense.groupId.toString() : '');
    formData.append('participants', JSON.stringify(expense.participants));
    if (expense.bill) {
      formData.append('bill', expense.bill, expense.bill.name);
    }

    return this.httpClient.post<ExpenseCreateDto>(this.expenseBaseUri, formData);
  }

  updateExpense(expenseId: number, expense: ExpenseCreateDto): Observable<ExpenseCreateDto> {
    const formData = new FormData();
    formData.append('name', expense.name);
    formData.append('amount', expense.amount ? expense.amount.toString() : '');
    formData.append('category', expense.category ? expense.category : '');
    formData.append('payerEmail', expense.payerEmail ? expense.payerEmail : '');
    formData.append('groupId', expense.groupId ? expense.groupId.toString() : '');
    formData.append('participants', JSON.stringify(expense.participants));
    if (expense.bill) {
      formData.append('bill', expense.bill, expense.bill.name);
    }
    return this.httpClient.put<ExpenseCreateDto>(this.expenseBaseUri + `/${expenseId}`, formData);
  }

  deleteExpense(expenseId: number): Observable<void> {
    return this.httpClient.delete<void>(this.expenseBaseUri + `/${expenseId}`);
  }

  recoverExpense(expenseId: number): Observable<ExpenseCreateDto> {
    return this.httpClient.put<ExpenseCreateDto>(this.expenseBaseUri + `/recover/${expenseId}`, {});
  }

  getExpenseById(expenseId: number): Observable<ExpenseDetailDto> {
    return this.httpClient.get<any>(this.expenseBaseUri + `/${expenseId}`)
      .pipe(map(response => {
        const members: UserSelection[] = response.group.members.map(email => ({ email: email }));
        response.group.members = members;
        return response;
      }));
  }

  getExpenseBillById(expenseId: number): Observable<Blob> {
    return this.httpClient.get(this.expenseBaseUri + `/bill/${expenseId}`, { responseType: 'blob' });
  }

  getExpensesByGroupId(groupId: number): Observable<ExpenseDetailDto[]> {
    return this.httpClient.get<any>(this.expenseBaseUriGroup + `/${groupId}/expenses`)
      .pipe(map(response => {
        return response.map(expense => {
          const members: UserSelection[] = expense.group.members.map(email => ({ email: email }));
          expense.group.members = members;
          return expense;
        });
      }));
  }

}
