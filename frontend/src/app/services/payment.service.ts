import { Injectable } from '@angular/core';
import {PaymentDto} from "../dtos/payment";
import {ExpenseCreateDto} from "../dtos/expense";
import {Observable} from "rxjs";
import { HttpClient } from '@angular/common/http';
import { Globals } from '../global/globals';
import { UserSelection } from '../dtos/user';

@Injectable({
  providedIn: 'root'
})
export class PaymentService {
  private paymentBaseUri: string = this.globals.backendUri + '/payment';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  create(payment: PaymentDto): Observable<PaymentDto> {
    return this.httpClient.post<PaymentDto>(this.paymentBaseUri, payment);
  }

  update(payment: PaymentDto) {
    return undefined;
  }
}
