import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Globals} from '../global/globals';
import {PantryDetailDto, PantrySearch} from "../dtos/pantry";

@Injectable({
  providedIn: 'root'
})
export class PantryService {

  private pantryBaseUri: string = this.globals.backendUri + '/group';
  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  getPantryById(id: number): Observable<PantryDetailDto> {
    return this.httpClient.get<PantryDetailDto>(`${this.pantryBaseUri}/${id}/pantry`);
  }

  filterPantry(id: number, pantrySearch: PantrySearch): Observable<PantryDetailDto> {
    let params = new HttpParams();
    params = params.append('details', pantrySearch.details)
    return this.httpClient.get<PantryDetailDto>(`${this.pantryBaseUri}/${id}/pantry/search`, { params });
  }
}
