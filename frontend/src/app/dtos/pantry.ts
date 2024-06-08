import {PantryItemDetailDto} from "./item";

export interface PantrySearch {
  details: string;
}

export interface PantryDetailDto {
  items: PantryItemDetailDto[];
}

export interface GetRecipesDto {
  itemIds: number[];
}
