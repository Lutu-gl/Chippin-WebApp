import { Category } from "./category"
import { GroupDto } from "./group"

export interface ExpenseCreateDto {
  name: string,
  category: Category
  amount: number,
  payerEmail: string,
  groupId: number,
  participants: object
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
  date: Date
}

