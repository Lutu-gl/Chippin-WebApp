import {UserSelection} from "./user";

export interface GroupListDto {
  id: number
  groupName: string
}

export interface GroupDto {
  id?: number
  groupName: string
  members: UserSelection[]
}
