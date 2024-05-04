export interface ItemDetailDto {
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
