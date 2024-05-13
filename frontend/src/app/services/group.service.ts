import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {map, Observable} from 'rxjs';
import {GroupDto, GroupListDto} from "../dtos/group";
import {UserSelection} from "../dtos/user";
import { BudgetDto } from '../dtos/budget';

@Injectable({
  providedIn: 'root'
})
export class GroupService {

  constructor(private http: HttpClient) { }

  getGroups(): Observable<GroupListDto[]> {
    return this.http.get<GroupListDto[]>('http://localhost:8080/api/users/groups');
  }

  getGroupBudgets(groupId: number): Observable<BudgetDto[]> {
    return this.http.get<BudgetDto[]>(`http://localhost:8080/api/v1/group/${groupId}/budgets`);
  }

  createBudget(groupId: number, budget: BudgetDto): Observable<BudgetDto> {
    return this.http.post<BudgetDto>(`http://localhost:8080/api/v1/group/${groupId}/budget`, budget);
  }

  updateBudget(groupId: number, budgetId: number, budget: BudgetDto): Observable<BudgetDto> {
    return this.http.put<BudgetDto>(`http://localhost:8080/api/v1/group/${groupId}/budget/${budgetId}`, budget);
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
    return this.http.post<GroupDto>('http://localhost:8080/api/group', formattedGroup);
  }

  getById(id: number): Observable<GroupDto> {
    return this.http.get<any>(`http://localhost:8080/api/group/${id}`).pipe(
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
    return this.http.put<GroupDto>(`http://localhost:8080/api/group/${group.id}`, formattedGroup);

  }
}
