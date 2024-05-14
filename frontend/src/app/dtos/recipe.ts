import {ItemCreateDto, ItemDetailDto} from "./item";

export interface RecipeSearch {
  details: string;
}

export interface RecipeDetailDto {
  id?: number
  name: String
  ingredients: ItemDetailDto[]
  description: String
  isPublic: boolean

}
export interface RecipeListDto {
  id: number
  RecipeName: String
}

export interface RecipeCreateDto {
  ingredients: ItemCreateDto[]
  name: String
  description: String
  isPublic: boolean
}
