import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Globals } from '../global/globals';
import { Observable } from 'rxjs';
import { ActivityDetailDto } from '../dtos/activity';

@Injectable({
  providedIn: 'root'
})
export class ActivityService {

  private activityBaseUri: string = this.globals.backendUri + "/activity";

  constructor(private httpClient: HttpClient, private globals: Globals) { }

  getExpenseActivitiesFromGroup(groupId: number): Observable<ActivityDetailDto[]> {
    return this.httpClient.get<ActivityDetailDto[]>(this.activityBaseUri + `/group-expenses/${groupId}`);
  }

  getPaymentActivitiesFromGroup(groupId: number): Observable<ActivityDetailDto[]> {
    return this.httpClient.get<ActivityDetailDto[]>(this.activityBaseUri + `/group-payments/${groupId}`);
  }

}
