import { Injectable } from '@angular/core';
import {Observable, of} from "rxjs";
import {UserSelection} from "../dtos/user";
import {HttpClient, HttpParams} from "@angular/common/http";
import {tap} from "rxjs/operators";
import {Globals} from '../global/globals';
import { GroupDto } from '../dtos/group';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private userBaseUri: string = this.globals.backendUri + '/users';

  constructor(
    private globals: Globals,
    private http: HttpClient,
  ) { }


  getUserGroups(): Observable<GroupDto[]> {
    return this.http.get<GroupDto[]>(this.userBaseUri + '/groups');
  }

}
