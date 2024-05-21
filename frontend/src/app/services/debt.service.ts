import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Globals } from '../global/globals';
import { ExpenseCreateDto, ExpenseDetailDto } from '../dtos/expense';
import { Observable, map } from 'rxjs';
import { UserSelection } from '../dtos/user';
import {DebtGroupDetailDto} from "../dtos/debt";
@Injectable({
  providedIn: 'root'
})
export class DebtService {
  private expenseBaseUri: string = this.globals.backendUri + '/debt';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }
  getDebtById(groupId: number): Observable<DebtGroupDetailDto> {
    return this.httpClient.get<any>(this.expenseBaseUri + `/${groupId}`)
  }
}
