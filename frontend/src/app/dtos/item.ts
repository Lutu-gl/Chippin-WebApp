import {unitToDisplayedUnit} from "../util/unit-helper";

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
  id: number;
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

export function pantryItemCreateDisplayDtoToPantryItemCreateDto(item: PantryItemCreateDisplayDto): PantryItemCreateDto {

  let returnItem: PantryItemCreateDto = {
    description: item.description,
    unit: Unit.Piece,
    amount: item.amount,
    lowerLimit: item.lowerLimit
  }

  switch(item.unit) {
    case DisplayedUnit.Piece:
      returnItem.unit = Unit.Piece;
      break;
    case DisplayedUnit.Pieces:
      returnItem.unit = Unit.Piece;
      break;
    case DisplayedUnit.Gram:
      returnItem.unit = Unit.Gram;
      break;
    case DisplayedUnit.Kilogram:
      returnItem.unit = Unit.Gram;
      returnItem.amount *= 1000;
      returnItem.lowerLimit *= 1000;
      break;
    case DisplayedUnit.Milliliter:
      returnItem.unit = Unit.Milliliter;
      break;
    case DisplayedUnit.Liter:
      returnItem.unit = Unit.Milliliter;
      returnItem.amount *= 1000;
      returnItem.lowerLimit *= 1000;
      break;
    default:
      console.error("Unknown unit");
  }

  return returnItem;
}

export function pantryItemDetailDtoToPantryItemCreateDisplayDto(item: PantryItemDetailDto): PantryItemCreateDisplayDto {

  return {
    id: item.id,
    description: item.description,
    unit: unitToDisplayedUnit(item.unit),
    amount: item.amount,
    lowerLimit: item.lowerLimit
  };
}

export function pantryItemCreateDisplayDtoToPantryItemDetailDto(item: PantryItemCreateDisplayDto, id: number): PantryItemDetailDto {
  let returnItem: PantryItemDetailDto = {
    id: id,
    description: item.description,
    unit: Unit.Piece,
    amount: item.amount,
    lowerLimit: item.lowerLimit
  }

  switch(item.unit) {
    case DisplayedUnit.Piece:
      returnItem.unit = Unit.Piece;
      break;
    case DisplayedUnit.Pieces:
      returnItem.unit = Unit.Piece;
      break;
    case DisplayedUnit.Gram:
      returnItem.unit = Unit.Gram;
      break;
    case DisplayedUnit.Kilogram:
      returnItem.unit = Unit.Gram;
      returnItem.amount *= 1000;
      returnItem.lowerLimit *= 1000;
      break;
    case DisplayedUnit.Milliliter:
      returnItem.unit = Unit.Milliliter;
      break;
    case DisplayedUnit.Liter:
      returnItem.unit = Unit.Milliliter;
      returnItem.amount *= 1000;
      returnItem.lowerLimit *= 1000;
      break;
    default:
      console.error("Unknown unit");
  }

  return returnItem;
}
