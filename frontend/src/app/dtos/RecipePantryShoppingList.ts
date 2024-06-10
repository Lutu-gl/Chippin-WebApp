import {ItemDetailDto, PantryItemDetailDto} from "./item";
import {ShoppingListItemDto} from "./shoppingList";

export interface AddItemToShoppingListDto {

  recipeItems: ItemDetailDto[];

  shoppingListItems: ShoppingListItemDto[];

  pantryItems: PantryItemDetailDto[];
}

export interface RemoveRecipeIngredientsFromPantryDto {

  recipeItems: ItemDetailDto[];


  pantryItems: PantryItemDetailDto[];
}
