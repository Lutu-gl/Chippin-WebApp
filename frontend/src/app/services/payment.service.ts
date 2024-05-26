import { Injectable } from '@angular/core';
import {PaymentDto} from "../dtos/payment";
import {ExpenseCreateDto} from "../dtos/expense";
import {Observable} from "rxjs";
import { HttpClient } from '@angular/common/http';
import { Globals } from '../global/globals';
import { UserSelection } from '../dtos/user';
import {DebtGroupDetailDto} from "../dtos/debt";

@Injectable({
  providedIn: 'root'
})
export class PaymentService {
  private paymentBaseUri: string = this.globals.backendUri + '/payment';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  createPayment(payment: PaymentDto): Observable<PaymentDto> {
    return this.httpClient.post<PaymentDto>(this.paymentBaseUri, payment);
  }

  updatePayment(paymentId: number, payment: PaymentDto): Observable<PaymentDto> {
    console.log(payment);
    console.log(paymentId);

    return this.httpClient.put<PaymentDto>(this.paymentBaseUri + `/${paymentId}`, payment)
  }

  deletePayment(paymentId: number): Observable<PaymentDto> {
    return this.httpClient.delete<PaymentDto>(this.paymentBaseUri + `/${paymentId}`)
  }

  recoverPayment(paymentId: number): Observable<PaymentDto>{
    return this.httpClient.put<PaymentDto>(this.paymentBaseUri + `/recover/${paymentId}`, {});
  }

  getPaymentById(paymentId: number): Observable<PaymentDto> {
    return this.httpClient.get<PaymentDto>(this.paymentBaseUri + `/${paymentId}`)
  }
}
