export interface ActivityDetailDto {
  id: number,
  description: string,
  category: any,
  timestamp: Date,
  expenseId: number,
  groupId: number,
  paymentId: number,
  userId: number
}

export interface ActivitySerachDto {
  search: string,
  from: Date,
  to: Date
}
