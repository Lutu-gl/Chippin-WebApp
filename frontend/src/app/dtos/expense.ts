import { Category } from "./category"
import { GroupDto } from "./group"

export interface ExpenseCreateDto {
  name: string,
  category: Category
  amount: number,
  payerEmail: string,
  groupId: number,
  participants: object,
  bill: File
}

export interface ExpenseDetailDto {
  id: number,
  name: string,
  category: Category,
  amount: number,
  payerEmail: string,
  group: GroupDto,
  participants: object,
  deleted: boolean,
  archived: boolean,
  date: Date,
  containsBill: boolean
}

