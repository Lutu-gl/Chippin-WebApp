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

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  createExpense(expense: ExpenseCreateDto): Observable<ExpenseCreateDto> {
    return this.httpClient.post<ExpenseCreateDto>(this.expenseBaseUri, expense);
  }

  updateExpense(expenseId: number, expense: ExpenseCreateDto): Observable<ExpenseCreateDto> {
    return this.httpClient.put<ExpenseCreateDto>(this.expenseBaseUri + `/${expenseId}`, expense);
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

}
