import {ItemCreateDto, ItemDetailDto} from "./item";
import {UserSelection} from "./user";

export interface RecipeSearch {
  details: string;
}

export interface RecipeDetailDto {
  id?: number
  ingredients: ItemDetailDto[]
  name: String
  description: String
  isPublic: boolean
  portionSize: number
  likes: number
  dislikes: number
}
export interface RecipeListDto {
  id: number
  name: String
}

export interface RecipeCreateWithoutUserDto {
  ingredients: ItemCreateDto[]
  name: String
  description: String
  isPublic: boolean
  portionSize:number
}
