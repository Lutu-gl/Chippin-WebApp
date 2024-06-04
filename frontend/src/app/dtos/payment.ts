export interface PaymentDto {
  id?: number;
  amount: number;
  payerEmail: string;
  receiverEmail: string;
  deleted: boolean;
  groupId: number;
  archived: boolean;
}
