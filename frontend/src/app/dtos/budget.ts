import { Category } from "./category"

export interface BudgetDto {
    id?: number
    name: string
    amount: number
    category: any
    resetFrequency: any
    alreadySpent?: number
    timestamp?: string
    daysUntilReset?: number
  }