export interface PaymentDto {
  id?: number;
  amount: number;
  payerEmail: string;
  receiverEmail: string;
  groupId: number;
}
