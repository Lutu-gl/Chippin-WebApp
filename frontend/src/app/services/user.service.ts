import { Injectable } from '@angular/core';
import {Observable, of} from "rxjs";
import {UserSelection} from "../dtos/user";
import {HttpClient, HttpParams} from "@angular/common/http";
import {tap} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(
    private http: HttpClient,
  ) { }

  getUserEmail(): string {
    return localStorage.getItem('userEmail');
  }


  // TODO implement : Search for friends!
  searchFriends(param: { name: string; limit: number }): Observable<UserSelection[]> {
    function searchFriendsMock() {
      // Generate a list of UserSelections based on the input parameters
      const mockUsers: UserSelection[] = [];
      const baseEmail = 'user';

      for (let i = 1; i <= 5; i++) {
        mockUsers.push({
          id: i,
          email: `${baseEmail}${i}@email.com`
        });
      }

      // Filter based on the 'name' provided and limit the results according to 'limit'
      const filteredUsers = mockUsers.filter(user => user.email.toLowerCase().includes(param.name.toLowerCase())).slice(0, param.limit);

      // Return an Observable of the filtered list
      return of(filteredUsers);
    }

    return searchFriendsMock();

    //return this.http.get<UserSelection[]>("ImplementationMissing", { params })
      //.pipe(tap(members => members.map(h => {
      //})));
  }
}
