import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Globals} from '../global/globals';
import {GetRecipesDto, PantryDetailDto, PantrySearch} from "../dtos/pantry";
import {ItemCreateDto, ItemDetailDto, PantryItemCreateDto, PantryItemDetailDto, PantryItemMergeDto} from "../dtos/item";
import {RecipeByItemsDto, RecipeDetailDto, RecipeListDto} from "../dtos/recipe";
import {AuthService} from "./auth.service";

@Injectable({
  providedIn: 'root'
})
export class PantryService {

  private pantryBaseUri: string = this.globals.backendUri + '/group';

  constructor(private httpClient: HttpClient, private globals: Globals, private authService: AuthService) {
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
   * Persists an item belonging to a pantry in the backend.
   *
   * @param id the pantry id
   * @param item to persist
   */
  createItem(id: number, item: PantryItemCreateDto): Observable<PantryItemDetailDto> {
    return this.httpClient.post<PantryItemDetailDto>(`${this.pantryBaseUri}/${id}/pantry`, item);
  }

  /**
   * Deletes an item from a pantry
   *
   * @param pantryId the pantry id
   * @param id the item id
   */
  deleteItem(pantryId: number, id: number): Observable<PantryItemDetailDto> {
    return this.httpClient.delete<PantryItemDetailDto>(`${this.pantryBaseUri}/${pantryId}/pantry/${id}`);
  }

  /**
   * Updates an item in a pantry.
   *
   * @param itemToUpdate the item to update
   * @param pantryId the pantry id
   */
  updateItem(itemToUpdate: PantryItemDetailDto, pantryId: number) {
    console.log(itemToUpdate)
    return this.httpClient.put<PantryItemDetailDto>(`${this.pantryBaseUri}/${pantryId}/pantry`, itemToUpdate);
  }

  /**
   * Updates itemMergeDto.result in the pantry and removes the item with id itemMergeDto.itemToDeleteId
   *
   * @param itemMergeDto contains the new item and the id of the item to delete
   * @param pantryId the pantry id
   */
  mergeItems(itemMergeDto: PantryItemMergeDto, pantryId: number): Observable<PantryItemDetailDto> {
    return this.httpClient.put<PantryItemDetailDto>(`${this.pantryBaseUri}/${pantryId}/pantry/merged`, itemMergeDto)
  }

  /**
   *
   *
   * @param id
   * @param getRecipeDto
   */
  getRecipes(id: number, getRecipesDto: GetRecipesDto) {
    return this.httpClient.post<RecipeByItemsDto[]>(`${this.pantryBaseUri}/${id}/pantry/recipes/user/${this.authService.getUserId()}`, getRecipesDto);
  }
  //List<{RecipeName, RecipeId, ItemsInPantry, Ingredients}>


  getAllMissingItems(id: number) {
    return this.httpClient.get<ItemDetailDto[]>(`${this.pantryBaseUri}/${id}/pantry/missing`);
  }
}
