import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Globals } from '../global/globals';
import { Observable } from 'rxjs';
import { ActivityDetailDto, ActivitySerachDto } from '../dtos/activity';
import { formatDate } from '@angular/common';

@Injectable({
  providedIn: 'root'
})
export class ActivityService {

  private activityBaseUri: string = this.globals.backendUri + "/activity";

  constructor(private httpClient: HttpClient, private globals: Globals) { }

  private formatIsoDate(date: Date): string {
    return formatDate(date, 'YYYY-MM-ddThh:mm:ss', 'en-DK');
  }

  getExpenseActivitiesByUser(searchCriteria: ActivitySerachDto): Observable<ActivityDetailDto[]> {
    let params = new HttpParams();
    if (searchCriteria.search) {
      params = params.append('search', searchCriteria.search);
    }
    if (searchCriteria.from) {
      params = params.append('from', this.formatIsoDate(searchCriteria.from));
    }
    if (searchCriteria.to) {
      params = params.append('to', this.formatIsoDate(searchCriteria.to));
    }

    return this.httpClient.get<ActivityDetailDto[]>(this.activityBaseUri + `/user-expenses`, { params: params });
  }

  getExpenseActivitiesFromGroup(groupId: number, searchCriteria: ActivitySerachDto): Observable<ActivityDetailDto[]> {
    let params = new HttpParams();
    if (searchCriteria.search) {
      params = params.append('search', searchCriteria.search);
    }
    if (searchCriteria.from) {
      params = params.append('from', this.formatIsoDate(searchCriteria.from));
    }
    if (searchCriteria.to) {
      params = params.append('to', this.formatIsoDate(searchCriteria.to));
    }

    return this.httpClient.get<ActivityDetailDto[]>(this.activityBaseUri + `/group-expenses/${groupId}`, { params: params });
  }

  getPaymentActivitiesFromGroup(groupId: number, searchCriteria: ActivitySerachDto): Observable<ActivityDetailDto[]> {
    let params = new HttpParams();
    if (searchCriteria?.search) {
      params = params.append('search', searchCriteria.search);
    }
    if (searchCriteria?.from) {
      params = params.append('from', this.formatIsoDate(searchCriteria.from));
    }
    if (searchCriteria?.to) {
      params = params.append('to', this.formatIsoDate(searchCriteria.to));
    }

    return this.httpClient.get<ActivityDetailDto[]>(this.activityBaseUri + `/group-payments/${groupId}`, { params: params });
  }

}
