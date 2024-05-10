import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {GroupDto, GroupListDto} from "../dtos/group";

@Injectable({
  providedIn: 'root'
})
export class GroupService {

  constructor(private http: HttpClient) { }

  getGroups(): Observable<GroupListDto[]> {
    return this.http.get<GroupListDto[]>('http://localhost:8080/api/users/groups');
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
    console.log('Creating group with formatted data:', formattedGroup);
    return this.http.post<GroupDto>('http://localhost:8080/api/group', formattedGroup);
  }
}
