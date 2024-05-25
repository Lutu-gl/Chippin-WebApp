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
   * Get all recipes associated with the user that sends this request.
   * @return all recipes associated with the user
   */
  getRecipesFromUser(): Observable<RecipeListDto[]>{
    return this.httpClient.get<RecipeListDto[]>(`${this.recipeBaseUri}/recipe/list`);
  }

  /**
   * Get all recipes liked by the user that sends this request.
   * @return all recipes liked by the user
   */
  getLikedRecipesFromUser(): Observable<RecipeListDto[]>{
    return this.httpClient.get<RecipeListDto[]>(`${this.recipeBaseUri}/recipe/likedlist`);
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

  /**
   * Delete a recipe with the given id.
   * @param id the id of the recipe to delete
   */
  deleteRecipe(id: number) : Observable<void> {

     return this.httpClient.delete<void>(`${this.recipeBaseUri}/recipe/${id}/delete`);

  }

  /**
   * Increase the like count of the recipe by 1 and store who liked it.
   * If the user already disliked, remove the dislike
   * @param id of the recipe to like
   */
  likeRecipe(id:number): Observable<RecipeDetailDto> {
    return this.httpClient.put<RecipeDetailDto>(`${this.recipeBaseUri}/recipe/${id}/like`, {});
  }
  /**
   * Increase the dislike count of the recipe by 1 and store who disliked it.
   * If the user already liked, remove the like
   * @param id of the recipe to dislike
   */
  dislikeRecipe(id:number): Observable<RecipeDetailDto> {
    return this.httpClient.put<RecipeDetailDto>(`${this.recipeBaseUri}/recipe/${id}/dislike`, {});
  }

  /**
   * Search for all public recipes with a search string
   * @param searchParam the string to search for
   */
  searchGlobalRecipes(searchParam: RecipeSearch): Observable<RecipeGlobalListDto[]> {
    let params = new HttpParams();
    params = params.append('details', searchParam.details)
    return this.httpClient.get<RecipeGlobalListDto[]>(`${this.recipeBaseUri}/recipe/search/global`, {params});
  }

  /**
   * Search for all owned recipes with a search string
   * @param searchParam the string to search for
   */
  searchOwnRecipes(searchParam: RecipeSearch): Observable<RecipeListDto[]> {
    let params = new HttpParams();
    params = params.append('details', searchParam.details)
    return this.httpClient.get<RecipeListDto[]>(`${this.recipeBaseUri}/recipe/search/own`, {params});
  }

  removeRecipeIngredientsFromPantry(pantryId:number, recipeId:number, portion:number): Observable<String[]> {

    return this.httpClient.put<String[]>(`${this.recipeBaseUri}/recipe/${recipeId}/pantry/${pantryId}/${portion}`, {});
  }
}
