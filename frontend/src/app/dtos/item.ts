export interface ItemDetailDto {
  id: number;
  description: String;
  amount: number;
  unit: Unit;
}

export interface ItemCreateDto {
  description: String;
  amount: number;
  unit: Unit;
}
export enum Unit {
  Piece = "Piece",
  Gram = "Gram",
  Milliliter = "Milliliter"
}
