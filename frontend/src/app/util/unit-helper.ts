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
      console.warn("unknown unit")
      return null;
  }
}
