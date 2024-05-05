import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Globals} from '../global/globals';
import {PantryDetailDto, PantrySearch} from "../dtos/pantry";
import {Item, ItemDetailDto} from "../dtos/item";

@Injectable({
  providedIn: 'root'
})
export class PantryService {

  private pantryBaseUri: string = this.globals.backendUri + '/group';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Loads a pantry by its id.
   *
   * @param id id of the pantry to load
   */
  getPantryById(id: number): Observable<PantryDetailDto> {
    return this.httpClient.get<PantryDetailDto>(`${this.pantryBaseUri}/${id}/pantry`);
  }

  /**
   * Filters for items in a pantry.
   *
   * @param id the pantry id
   * @param pantrySearch the search string
   */
  filterPantry(id: number, pantrySearch: PantrySearch): Observable<PantryDetailDto> {
    let params = new HttpParams();
    params = params.append('details', pantrySearch.details)
    return this.httpClient.get<PantryDetailDto>(`${this.pantryBaseUri}/${id}/pantry/search`, {params});
  }

  /**
   * Persists an item belonging to a pantry to the backend.
   *
   * @param id the pantry id
   * @param item to persist
   */
  createItem(id: number, item: ItemDetailDto): Observable<Item> {
    console.log('Create item' + item + ' for pantry with id ' + id);
    return this.httpClient.post<Item>(`${this.pantryBaseUri}/${id}/pantry`, item);
  }
}
