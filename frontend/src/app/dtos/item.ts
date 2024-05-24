export interface ItemDetailDto {
  id: number;
  description: String;
  amount: number;
  unit: Unit;
}

export interface PantryItemDetailDto {
  id: number;
  description: String;
  amount: number;
  unit: Unit;
  lowerLimit: number;
}

export interface PantryItemMergeDto {
  result: PantryItemDetailDto;
  itemToDeleteId: number;
}

export interface ItemCreateDto {
  description: String;
  amount: number;
  unit: Unit;
}

export interface PantryItemCreateDto {
  description: String;
  amount: number;
  unit: Unit;
  lowerLimit: number;
}

export interface PantryItemCreateDisplayDto {
  description: String;
  amount: number;
  unit: DisplayedUnit;
  lowerLimit: number;
}

export enum Unit {
  Piece = "Piece",
  Gram = "Gram",
  Milliliter = "Milliliter"
}

export enum DisplayedUnit {
  Piece = "Piece",
  Pieces = "Pieces",
  Gram = "Gram",
  Kilogram = "Kilogram",
  Milliliter = "Milliliter",
  Liter = "Liter"
}
