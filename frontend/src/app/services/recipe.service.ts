import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Globals} from '../global/globals';
import {
  RecipeCreateWithoutUserDto,
  RecipeDetailDto, RecipeDetailWithUserInfoDto,
  RecipeGlobalListDto,
  RecipeListDto,
  RecipeSearch
} from "../dtos/recipe";
import {ItemCreateDto, ItemDetailDto} from "../dtos/item";
import {AddItemToShoppingListDto, RemoveRecipeIngredientsFromPantryDto} from "../dtos/RecipePantryShoppingList";

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
   * Loads a recipe by its id.
   *
   * @param id id of the recipe to load
   */
  getRecipeWithInfoById(id: number): Observable<RecipeDetailWithUserInfoDto> {
    return this.httpClient.get<RecipeDetailWithUserInfoDto>(`${this.recipeBaseUri}/${id}/recipe/info`);
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
  getRecipesFromUser(): Observable<RecipeListDto[]> {
    return this.httpClient.get<RecipeListDto[]>(`${this.recipeBaseUri}/recipe/list`);
  }

  /**
   * Get all recipes liked by the user that sends this request.
   * @return all recipes liked by the user
   */
  getLikedRecipesFromUser(): Observable<RecipeListDto[]> {
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
  deleteRecipe(id: number): Observable<void> {

    return this.httpClient.delete<void>(`${this.recipeBaseUri}/recipe/${id}/delete`);

  }

  /**
   * Increase the like count of the recipe by 1 and store who liked it.
   * If the user already disliked, remove the dislike
   * @param id of the recipe to like
   */
  likeRecipe(id: number): Observable<RecipeDetailDto> {
    return this.httpClient.put<RecipeDetailDto>(`${this.recipeBaseUri}/recipe/${id}/like`, {});
  }

  /**
   * Increase the dislike count of the recipe by 1 and store who disliked it.
   * If the user already liked, remove the like
   * @param id of the recipe to dislike
   */
  dislikeRecipe(id: number): Observable<RecipeDetailDto> {
    return this.httpClient.put<RecipeDetailDto>(`${this.recipeBaseUri}/recipe/${id}/dislike`, {});
  }

  /**
   * Search for all public recipes with a search string
   * @param searchParam the string to search for
   */
  searchGlobalRecipes(searchParam: RecipeSearch): Observable<RecipeGlobalListDto[]> {
    let params = new HttpParams();
    params = params.append('details', searchParam.details);
    return this.httpClient.get<RecipeGlobalListDto[]>(`${this.recipeBaseUri}/recipe/search/global`, {params});
  }

  /**
   * Search for all public recipes with a search string
   * @param searchParam the string to search for
   */
  searchLikedRecipes(searchParam: RecipeSearch): Observable<RecipeListDto[]> {
    let params = new HttpParams();
    params = params.append('details', searchParam.details);
    return this.httpClient.get<RecipeListDto[]>(`${this.recipeBaseUri}/recipe/search/liked`, {params});
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

  /**
   * Update an item in a recipe.
   * @param item the item to update
   * @param recipeId the id of the recipe the item belongs to
   */
  updateItemInRecipe(item: ItemDetailDto, recipeId:number): Observable<ItemDetailDto> {
    return this.httpClient.put<ItemDetailDto>(`${this.recipeBaseUri}/${recipeId}/recipe`, item);
  }

  removeRecipeIngredientsFromPantry(recipeId:number, pantryId:number , portion:number): Observable<RemoveRecipeIngredientsFromPantryDto> {

    return this.httpClient.put<RemoveRecipeIngredientsFromPantryDto>(`${this.recipeBaseUri}/recipe/${recipeId}/pantry/${pantryId}/${portion}`, {});
  }


  /**
   * Return a suggestion for the user what items he can add to his shopping list.
   * This function takes into account what already is in the selected shopping list and the optional pantry
   *
   * @param recipeId       the recipe with the ingredients
   * @param shoppingListId the shopping list to add
   * @param pantryId       the pantry the user wants to be considered
   * @return a list of items with
   */
  selectIngredientsForShoppingListWithPantry(recipeId: number, shoppingListId: number, pantryId: number): Observable<AddItemToShoppingListDto> {
    return this.httpClient.get<AddItemToShoppingListDto>(`${this.recipeBaseUri}/recipe/${recipeId}/shoppinglist/${shoppingListId}/pantry/${pantryId}`);
  }

  selectIngredientsForShoppingList(recipeId: number, shoppingListId: number): Observable<AddItemToShoppingListDto> {
    return this.httpClient.get<AddItemToShoppingListDto>(`${this.recipeBaseUri}/recipe/${recipeId}/shoppinglist/${shoppingListId}`);
  }

  exportRecipe(recipeId:number) {
    return this.httpClient.get(`${this.globals.backendUri}/recipe/${recipeId}/pdf`, {responseType: "blob"});
  }
}
