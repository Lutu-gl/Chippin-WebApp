import {ItemDetailDto} from "./item";
import {Category} from "./category";
import {GroupDto} from "./group";


export interface ShoppingListCreateDto {
  name: string;
  categories: Category[];
  group: number | null;
}

export interface ShoppingListEditDto {
  name: string;
  categories: Category[];
  group: number | null;
}

export interface ShoppingListDetailDto {
  id: number;
  name: string;
  categories: Category[];
  groupId: number;
  items: ItemDetailDto[];
}

export interface ShoppingListListDto {
  id: number;
  name: string;
  budget: number;
}
