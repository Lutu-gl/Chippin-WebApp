import {Injectable} from '@angular/core';
import {Globals} from '../global/globals';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {
  ShoppingListCreateEditDto,
  ShoppingListDetailDto,
  ShoppingListListDto
} from "../dtos/shoppingList";

@Injectable({
  providedIn: 'root'
})
export class ShoppingListService {

  private shoppingListBaseUri: string = this.globals.backendUri;


  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Creates a new shopping list for a group.
   *
   * @param userId the id of the user creating the shopping list
   * @param shoppingList the shopping list to create
   */
  createShoppingList(userId: number, shoppingList: ShoppingListCreateEditDto): Observable<ShoppingListDetailDto> {
    return this.httpClient.post<ShoppingListDetailDto>(`${this.shoppingListBaseUri}/users/${userId}/shopping-lists`, shoppingList);
  }

  /**
   * Gets a shopping list by its id.
   *
   * @param shoppingListId the id of the shopping list
   */
  getShoppingListById(shoppingListId: number): Observable<ShoppingListDetailDto> {
    return this.httpClient.get<ShoppingListDetailDto>(`${this.shoppingListBaseUri}/shopping-lists/${shoppingListId}`);
  }

  /**
   * Deletes a shopping list.
   *
   * @param groupId the id of the group the shopping list belongs to
   * @param shoppingListId the id of the shopping list
   */
  deleteShoppingList(groupId: number, shoppingListId: number): Observable<ShoppingListDetailDto> {
    return this.httpClient.delete<ShoppingListDetailDto>(`${this.shoppingListBaseUri}/${groupId}/shoppinglist/${shoppingListId}`);
  }

  /**
   * Gets all shopping lists for a group.
   *
   * @param groupId the id of the group
   */
  getShoppingListsForGroup(groupId: number): Observable<ShoppingListListDto[]> {
    return this.httpClient.get<ShoppingListListDto[]>(`${this.shoppingListBaseUri}/${groupId}/shoppinglist`);
  }

  /**
   * Updates a shopping list.
   *
   * @param groupId the id of the group the shopping list belongs to
   * @param shoppingListId the id of the shopping list
   * @param shoppingList the shopping list to update
   * @returns the updated shopping list
   */
  updateShoppingList(groupId: number, shoppingListId: number, shoppingList: ShoppingListEditDto): Observable<ShoppingListDetailDto> {
    return this.httpClient.put<ShoppingListDetailDto>(`${this.shoppingListBaseUri}/${groupId}/shoppinglist/${shoppingListId}`, shoppingList);
  }

  getShoppingListsForUser(currentUserId: number) {
    return this.httpClient.get<ShoppingListListDto[]>(`${this.shoppingListBaseUri}/users/${currentUserId}/shopping-lists`);
  }
}
