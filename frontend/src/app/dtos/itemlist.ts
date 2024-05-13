import {ItemDetailDto} from "./item";
import {UserSelection} from "./user";

export interface ItemListSearch {
  details: string;
}

export interface ItemListDetailDto {
  id?: number
  name: String
  items: ItemDetailDto[]
}

export interface ItemListListDto {
  id: number
  itemListName: string
}
export interface ItemListCreateDto {
  name: String
  items: ItemDetailDto[]
}
