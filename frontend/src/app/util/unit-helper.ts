import {DisplayedUnit, Unit} from "../dtos/item";

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
