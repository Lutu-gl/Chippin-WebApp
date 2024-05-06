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
  Teaspoon = "Teaspoon",
  Tablespoon = "Tablespoon",
  Gram = "Gram",
  Kilogram = "Kilogram",
  Liter = "Liter",
  Milliliter = "Milliliter"
}
