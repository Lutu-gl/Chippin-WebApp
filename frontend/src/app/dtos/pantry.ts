import {ItemDetailDto} from "./item";

export interface PantrySearch {
  details: string;
}

export interface PantryDetailDto {
  items: ItemDetailDto[]
}
