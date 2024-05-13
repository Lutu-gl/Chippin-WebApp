import {ItemDetailDto} from "./item";

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
  name: String
  ingredients: ItemDetailDto[]
  description: String
  isPublic: boolean
}
