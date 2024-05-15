import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Globals } from '../global/globals';
import { ExpenseCreateDto } from '../dtos/expense';
import { Observable } from 'rxjs';

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

}
