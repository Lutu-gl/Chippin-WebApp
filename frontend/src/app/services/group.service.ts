import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {GroupDto} from "../dtos/group";

@Injectable({
  providedIn: 'root'
})
export class GroupService {

  constructor(private http: HttpClient) { }

  getGroups(): Observable<GroupDto[]> {
    return this.http.get<GroupDto[]>('http://localhost:8080/api/users/groups');
  }
}
