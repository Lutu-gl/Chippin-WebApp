import {DisplayedUnit, PantryItemCreateDisplayDto, PantryItemDetailDto, Unit} from "../dtos/item";

export function displayQuantity(unit: Unit, amount: number): [DisplayedUnit, number] {
  switch (unit) {
    case Unit.Gram:
      return amount >= 1000
        ? [DisplayedUnit.Kilogram, amount / 1000]
        : [DisplayedUnit.Gram, amount];
    case Unit.Milliliter:
      return amount >= 1000
        ? [DisplayedUnit.Liter, amount / 1000]
        : [DisplayedUnit.Milliliter, amount];
    case Unit.Piece:
      return amount > 1
        ? [DisplayedUnit.Pieces, amount]
        : [DisplayedUnit.Piece, amount];
    default:
      return [null, null];
  }
}

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

export function getStepSize(item: PantryItemDetailDto): number {
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
      return item.amount == 1 ? " Piece" : " Pieces";
    case DisplayedUnit.Pieces:
      return item.amount == 1 ? " Piece" : " Pieces";
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
export function getSuffix(item: PantryItemDetailDto): String {
  switch (item.unit) {
    case Unit.Piece:
      return item.amount == 1 ? " Piece" : " Pieces";
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
      return item.lowerLimit == 1 ? " Piece" : " Pieces";
    case Unit.Gram:
      return item.lowerLimit < 1000 ? "g" : "kg";
    case Unit.Milliliter:
      return item.lowerLimit < 1000 ? "ml" : "l";
    default:
      console.error("Unknown Unit");
      return "";
  }
}

function getAmount(item: PantryItemDetailDto): number {
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

export function formatAmount(item: PantryItemDetailDto): String {
  return getAmount(item).toLocaleString('de-DE') + getSuffix(item);
}

export function formatLowerLimit(item: PantryItemDetailDto): String {
  if(!item.lowerLimit) {
    return "-";
  }
  return getLowerLimit(item).toLocaleString('de-DE') + getLimitSuffix(item);
}
