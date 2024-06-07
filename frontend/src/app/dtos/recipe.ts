import {ItemCreateDto, ItemDetailDto} from "./item";

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
export interface RecipeDetailWithUserInfoDto {
  id?: number
  ingredients: ItemDetailDto[]
  name: String
  description: String
  isPublic: boolean
  portionSize: number
  likes: number
  dislikes: number
  likedByUser:boolean
  dislikedByUser:boolean
}
export interface RecipeListDto {
  id: number
  name: String
  likes:number
  dislikes:number
}

export interface RecipeGlobalListDto {
  id: number
  name: String
  likes:number
  dislikes:number
  likedByUser:boolean
  dislikedByUser:boolean
}

export interface RecipeCreateWithoutUserDto {
  ingredients: ItemCreateDto[]
  name: String
  description: String
  isPublic: boolean
  portionSize:number
}
