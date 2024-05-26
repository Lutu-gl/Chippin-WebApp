import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {map, Observable} from 'rxjs';
import {Globals} from '../global/globals';
import {GroupDto, GroupListDto} from "../dtos/group";
import {UserSelection} from "../dtos/user";
import { BudgetDto } from '../dtos/budget';

@Injectable({
  providedIn: 'root'
})
export class GroupService {
  private groupBaseUri: string = this.globals.backendUri + '/group';
  constructor(private http: HttpClient, private globals: Globals) { }

  getGroups(): Observable<GroupListDto[]> {
    return this.http.get<GroupListDto[]>(this.globals.backendUri + '/users/groups');
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

  create(group: GroupDto): Observable<GroupDto> {
    // Convert into a format that the backend expects
    const memberEmails: Set<string> = new Set();

    group.members.forEach(member => {
      memberEmails.add(member.email);
    });

    const formattedGroup = {
      groupName: group.groupName,
      members: Array.from(memberEmails)
    };
    return this.http.post<GroupDto>(this.groupBaseUri, formattedGroup);
  }

  getById(id: number): Observable<GroupDto> {
    return this.http.get<any>(this.groupBaseUri + `/${id}`).pipe(
      map(response => {
        // Convert members from string (email) to UserSelection
        const members: UserSelection[] = response.members.map(email => ({ email: email }));

        // Return the transformed data
        return {
          id: response.id,
          groupName: response.groupName,
          members: members
        };
      })
    );
  }

  update(group: GroupDto) {
    // Convert into a format that the backend expects
    const memberEmails: Set<string> = new Set();

    group.members.forEach(member => {
      memberEmails.add(member.email);
    });

    const formattedGroup = {
      groupName: group.groupName,
      members: Array.from(memberEmails)
    };
    return this.http.put<GroupDto>(this.groupBaseUri + `/${group.id}`, formattedGroup);

  }
}
