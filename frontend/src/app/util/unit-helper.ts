import {
  DisplayedUnit,
  ItemCreateDto,
  ItemDetailDto,
  PantryItemCreateDisplayDto,
  PantryItemCreateDto,
  PantryItemDetailDto,
  Unit
} from "../dtos/item";

export function convertQuantity(displayUnit: DisplayedUnit, amount: number): [Unit, number] {
  let factor: number = 1;
  let unit: Unit;
  switch (displayUnit) {
    case DisplayedUnit.Gram:
      unit = Unit.Gram;
      break;
    case DisplayedUnit.Kilogram:
      unit = Unit.Gram;
      factor = 1000;
      break;
    case DisplayedUnit.Milliliter:
      unit = Unit.Milliliter;
      break;
    case DisplayedUnit.Liter:
      unit = Unit.Milliliter;
      factor = 1000;
      break;
    case DisplayedUnit.Piece:
      unit = Unit.Piece;
      break;
    case DisplayedUnit.Pieces:
      unit = Unit.Piece;
      break;
  }
  return [unit, amount * factor];
}

export function unitToDisplayedUnit(unit: Unit): DisplayedUnit {
  switch (unit){
    case Unit.Piece:
      return DisplayedUnit.Piece;
    case Unit.Milliliter:
      return DisplayedUnit.Milliliter;
    case Unit.Gram:
      return DisplayedUnit.Gram;
  }
}

export function displayedUnitToUnit(unit: DisplayedUnit): Unit {
  switch (unit){
    case DisplayedUnit.Piece:
      return Unit.Piece;
    case DisplayedUnit.Pieces:
      return Unit.Piece;
    case DisplayedUnit.Milliliter:
      return Unit.Milliliter;
    case DisplayedUnit.Liter:
      return Unit.Milliliter;
    case DisplayedUnit.Gram:
      return Unit.Gram;
    case DisplayedUnit.Kilogram:
      return Unit.Gram;
  }
}

export function getStepSize(item: PantryItemDetailDto | ItemCreateDto): number {
  switch (item.unit) {
    case Unit.Piece:
      return item.amount > 100 ? 10 : 1;
    case Unit.Gram:
      switch (true) {
        case (item.amount < 100):
          return 10;
        case (item.amount < 1000):
          return 100;
        case (item.amount < 10000):
          return 250;
        default:
          return 1000;
      }
    case Unit.Milliliter:
      switch (true) {
        case (item.amount < 100):
          return 10;
        case (item.amount < 1000):
          return 100;
        case (item.amount < 10000):
          return 250;
        default:
          return 1000;
      }
    default:
      console.error("Unknown Unit");
      return 1;
  }
}

export function getAmountForCreateEdit(item: PantryItemCreateDisplayDto) {
  if(item.unit === DisplayedUnit.Liter || item.unit === DisplayedUnit.Kilogram) {
    if(item.amount > 1000) {
      item.amount = 1000;
    }
    if(item.lowerLimit > 1000) {
      item.lowerLimit = 1000;
    }
  }
}
export function getSuffixForCreateEdit(item: PantryItemCreateDisplayDto): String {
  switch (item.unit) {
    case DisplayedUnit.Piece:
      return item.amount == 1 ? "pc" : "pcs";
    case DisplayedUnit.Pieces:
      return item.amount == 1 ? "pc" : "pcs";
    case DisplayedUnit.Gram:
      return "g";
    case DisplayedUnit.Kilogram:
      return "kg";
    case DisplayedUnit.Milliliter:
      return "ml";
    case DisplayedUnit.Liter:
      return "l";
    default:
      console.error("Unknown Unit");
      return "";
  }
}
export function getSuffix(item: PantryItemDetailDto | PantryItemCreateDto | ItemDetailDto): String {
  switch (item.unit) {
    case Unit.Piece:
      return item.amount == 1 ? "pc" : "pcs";
    case Unit.Gram:
      return item.amount < 1000 ? "g" : "kg";
    case Unit.Milliliter:
      return item.amount < 1000 ? "ml" : "l";
    default:
      console.error("Unknown Unit");
      return "";
  }
}

export function getLimitSuffix(item: PantryItemDetailDto): String {
  switch (item.unit) {
    case Unit.Piece:
      return item.lowerLimit == 1 ? "pc" : "pcs";
    case Unit.Gram:
      return item.lowerLimit < 1000 ? "g" : "kg";
    case Unit.Milliliter:
      return item.lowerLimit < 1000 ? "ml" : "l";
    default:
      console.error("Unknown Unit");
      return "";
  }
}

function getAmount(item: PantryItemDetailDto | ItemDetailDto): number {
  switch (item.unit) {
    case Unit.Piece:
      return item.amount;
    case Unit.Gram:
      return item.amount < 1000 ? item.amount : item.amount/1000;
    case Unit.Milliliter:
      return item.amount < 1000 ? item.amount : item.amount/1000;
    default:
      console.error("Unknown Unit");
      return item.amount;
  }
}

function getLowerLimit(item: PantryItemDetailDto): number {
  switch (item.unit) {
    case Unit.Piece:
      return item.lowerLimit;
    case Unit.Gram:
      return item.lowerLimit < 1000 ? item.lowerLimit : item.lowerLimit/1000;
    case Unit.Milliliter:
      return item.lowerLimit < 1000 ? item.lowerLimit : item.lowerLimit/1000;
    default:
      console.error("Unknown Unit");
      return item.lowerLimit;
  }
}

export function formatAmount(item: PantryItemDetailDto | ItemDetailDto): String {
  return getAmount(item).toLocaleString('de-DE') + getSuffix(item);
}

export function formatLowerLimit(item: PantryItemDetailDto): String {
  if(!item.lowerLimit) {
    return "-";
  }
  return getLowerLimit(item).toLocaleString('de-DE') + getLimitSuffix(item);
}
