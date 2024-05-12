import {ItemDetailDto} from "./item";


export interface ShoppingListCreateDto {
  name: string;
  budget: number;
}

export interface ShoppingListEditDto {
  name: string;
  budget: number;
}

export interface ShoppingListDetailDto {
  id: number;
  name: string;
  budget: number;
  groupId: number;
  items: ItemDetailDto[];
}

export interface ShoppingListListDto {
  id: number;
  name: string;
  budget: number;
}
