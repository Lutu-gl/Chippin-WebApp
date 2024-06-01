import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {map, Observable} from 'rxjs';
import {Globals} from '../global/globals';
import {GroupDto, GroupDetailDto, GroupListDto} from "../dtos/group";
import {UserSelection} from "../dtos/user";
import { BudgetDto } from '../dtos/budget';

@Injectable({
  providedIn: 'root'
})
export class GroupService {
  private groupBaseUri: string = this.globals.backendUri + '/group';
  constructor(private http: HttpClient, private globals: Globals) { }

  getGroups(): Observable<GroupDetailDto[]> {
    return this.http.get<GroupDetailDto[]>(this.globals.backendUri + '/users/groups');
  }
  getGroupsWithDebtInfos(): Observable<GroupListDto[]> {
    return this.http.get<GroupListDto[]>(this.globals.backendUri + '/users/groups-with-debt-infos');
  }

  getGroupBudgets(groupId: number): Observable<BudgetDto[]> {
    return this.http.get<BudgetDto[]>(this.groupBaseUri + `/${groupId}/budgets`);
  }

  createBudget(groupId: number, budget: BudgetDto): Observable<BudgetDto> {
    return this.http.post<BudgetDto>(this.groupBaseUri + `/${groupId}/budget`, budget);
  }

  deleteBudget(groupId: number, budgetId: number): Observable<void> {
    return this.http.delete<void>(`${this.groupBaseUri}/${groupId}/budget/${budgetId}`);
  }

  updateBudget(groupId: number, budgetId: number, budget: BudgetDto): Observable<BudgetDto> {
    return this.http.put<BudgetDto>(this.groupBaseUri + `/${groupId}/budget/${budgetId}`, budget);
  }

  getByBudgetId(groupId: number, budgetId: number): Observable<BudgetDto> {
    return this.http.get<BudgetDto>(`${this.groupBaseUri}/${groupId}/budget/${budgetId}`);
  }

  create(group: GroupDto): Observable<GroupDto> {
    return this.http.post<GroupDto>(this.groupBaseUri, group);
  }

  getById(id: number): Observable<GroupDto> {
    return this.http.get<any>(this.groupBaseUri + `/${id}`)
  }

  update(group: GroupDto) {
    return this.http.put<GroupDto>(this.groupBaseUri + `/${group.id}`, group);
  }
}
