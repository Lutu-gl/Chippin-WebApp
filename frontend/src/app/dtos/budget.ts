import { Category } from "./category"

export interface BudgetDto {
    id?: number
    name: string
    amount: number
    category: any
    alreadySpent?: number
  }