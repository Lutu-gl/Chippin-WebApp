import {ItemDetailDto} from "./item";
import {Category} from "./category";
import {GroupDto} from "./group";
import {UserSelection} from "./user";


export interface ShoppingListCreateEditDto {
  owner?: UserSelection;
  name: string;
  categories: Category[];
  group: GroupDto;
}

export interface ShoppingListDetailDto {
  id: number;
  name: string;
  owner: UserSelection;
  categories: Category[];
  group: GroupDto;
  items: ShoppingListItemDto[];
}

export interface ShoppingListItemDto {
  id: number;
  item: ItemDetailDto;
  checkedById: number;
}

export interface ShoppingListItemUpdateDto {
  id: number;
  item: ItemDetailDto;
  checked: boolean;
}

export interface ShoppingListListDto {
  id: number;
  name: string;
  groupId: number;
  itemCount: number;
  checkedItemCount: number;
}
