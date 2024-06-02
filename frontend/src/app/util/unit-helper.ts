import {DisplayedUnit, PantryItemDetailDto, Unit} from "../dtos/item";

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
