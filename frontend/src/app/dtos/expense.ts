import { Category } from "./category"

export interface ExpenseCreateDto {
  name: string,
  category: Category
  amount: number,
  payerEmail: string,
  groupId: number,
  participants: {}
}