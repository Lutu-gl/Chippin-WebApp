import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Globals} from '../global/globals';
import {PantryDetailDto} from "../dtos/pantry";

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
}
