import {UserSelection} from "./user";

export interface GroupDetailDto {
  id: number
  groupName: string
}

export interface GroupListDto {
  id: number
  groupName: string
  membersCount: number
  membersDebts: { [key: string]: number }
}

export interface GroupDto {
  id?: number
  groupName: string
  members: string[]
}
