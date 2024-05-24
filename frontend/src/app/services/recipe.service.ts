import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Globals} from '../global/globals';
import {
  RecipeCreateWithoutUserDto,
  RecipeDetailDto,
  RecipeGlobalListDto,
  RecipeListDto,
  RecipeSearch
} from "../dtos/recipe";
import {ItemCreateDto, ItemDetailDto} from "../dtos/item";
import {RecipeDetailComponent} from "../components/recipe/recipe-detail/recipe-detail.component";

@Injectable({
  providedIn: 'root'
})
export class RecipeService {

  private recipeBaseUri: string = this.globals.backendUri + '/group';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Create a new recipe
   * @return the returned recipe
   * @param recipe
   */

  createRecipe(recipe: RecipeCreateWithoutUserDto): Observable<RecipeDetailDto> {
    return this.httpClient.post<RecipeDetailDto>(`${this.recipeBaseUri}/recipe/create`, recipe);
  }
  /**
   * Loads a recipe by its id.
   *
   * @param id id of the recipe to load
   */
  getRecipeById(id: number): Observable<RecipeDetailDto> {
    return this.httpClient.get<RecipeDetailDto>(`${this.recipeBaseUri}/${id}/recipe`);
  }

  /**
   * Filters for items in a recipe.
   *
   * @param id the recipe id
   * @param recipeSearch the search string
   */
  filterRecipe(id: number, recipeSearch: RecipeSearch): Observable<RecipeDetailDto> {
    let params = new HttpParams();
    params = params.append('details', recipeSearch.details)
    return this.httpClient.get<RecipeDetailDto>(`${this.recipeBaseUri}/${id}/recipe/search`, {params});
  }

  /**
   * Persists an item belonging to a recipe to the backend.
   *
   * @param id the recipe id
   * @param item to persist
   */
  createItem(id: number, item: ItemCreateDto): Observable<ItemDetailDto> {
    console.log('Create item' + item + ' for recipe with id ' + id);
    return this.httpClient.post<ItemDetailDto>(`${this.recipeBaseUri}/${id}/recipe`, item);
  }

  /**
   * Deletes an ingredient from a recipe
   *
   * @param recipeId the recipe id
   * @param id the item id
   */
  deleteIngredient(recipeId: number, id: number): Observable<ItemDetailDto> {
    return this.httpClient.delete<ItemDetailDto>(`${this.recipeBaseUri}/${recipeId}/recipe/${id}`);
  }

  /**
   * Updates an item in a recipe.
   *
   * @param itemToUpdate the item to update
   * @param recipeId the recipe id
   */
  updateItem(itemToUpdate: ItemDetailDto, recipeId: number) {
    console.log("item: ", itemToUpdate)
    return this.httpClient.put<ItemDetailDto>(`${this.recipeBaseUri}/${recipeId}/recipe`, itemToUpdate);
  }

  /**
   * Get all recipes associated with the user that sends this request.
   * @return all recipes associated with the user
   */
  getRecipesFromUser(): Observable<RecipeListDto[]>{
    return this.httpClient.get<RecipeListDto[]>(`${this.recipeBaseUri}/recipe/list`);
  }

  /**
   * Update recipe with recipeId with new recipe.
   * @param toUpdate the RecipeDetailDto with new information
   * @return the updated recipeDetailDto
   */
  updateRecipe(toUpdate: RecipeDetailDto): Observable<RecipeDetailDto> {
    return this.httpClient.put<RecipeDetailDto>(`${this.recipeBaseUri}/recipe/update`, toUpdate);
  }

  /**
   * Get the list of all public recipes ordered by their like count (desc.).
   * @return the list of all public recipes
   */
  getPublicRecipeOrderedByLikes(): Observable<RecipeGlobalListDto[]> {
    return this.httpClient.get<RecipeGlobalListDto[]>(`${this.recipeBaseUri}/recipe/global`);
  }

  deleteRecipe(id: number) : Observable<void>{
     return this.httpClient.delete<void>(`${this.recipeBaseUri}/recipe/${id}/delete`);
  }

  likeRecipe(id:number): Observable<RecipeDetailDto> {
    return this.httpClient.put<RecipeDetailDto>(`${this.recipeBaseUri}/recipe/${id}/like`, {});
  }

  dislikeRecipe(id:number): Observable<RecipeDetailDto> {
    return this.httpClient.put<RecipeDetailDto>(`${this.recipeBaseUri}/recipe/${id}/dislike`, {});
  }
}
