import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {PantryService} from "../../services/pantry.service";
import {PantryItemCreateDto, PantryItemDetailDto, PantryItemMergeDto, Unit} from "../../dtos/item";
import {KeyValuePipe, NgForOf, NgIf, NgSwitch, NgSwitchCase} from "@angular/common";
import {FormsModule, NgForm} from "@angular/forms";
import {debounceTime, Subject} from "rxjs";
import {PantrySearch} from "../../dtos/pantry";
import {ConfirmDeleteDialogComponent} from "../confirm-delete-dialog/confirm-delete-dialog.component";
import {EditPantryItemDialogComponent} from "./edit-pantry-item-dialog/edit-pantry-item-dialog.component";
import {clone} from "lodash";
import {displayQuantity} from "../../util/unit-helper";

@Component({
  selector: 'app-pantry',
  standalone: true,
  imports: [
    NgForOf,
    KeyValuePipe,
    NgIf,
    FormsModule,
    ConfirmDeleteDialogComponent,
    NgSwitchCase,
    NgSwitch,
    EditPantryItemDialogComponent
  ],
  templateUrl: './pantry.component.html',
  styleUrl: './pantry.component.scss'
})
export class PantryComponent implements OnInit {

  error = false;
  errorMessage = '';

  items: PantryItemDetailDto[];
  newItem: PantryItemCreateDto = {
    amount: 0,
    unit: Unit.Piece,
    description: "",
    lowerLimit: null
  };
  newLowerLimit: number = 0;
  selectedItem: PantryItemDetailDto = undefined;
  itemToEdit: PantryItemDetailDto = undefined;
  mergeItem: PantryItemMergeDto = undefined;
  searchString: string = "";
  searchChangedObservable = new Subject<void>();
  id: number;

  constructor(
    private route: ActivatedRoute,
    private service: PantryService
  ) {
  }

  ngOnInit(): void {
    this.route.params.subscribe({
      next: params => {
        this.id = +params['id'];
        this.getPantry(this.id);
      },
      error: err => {
        this.defaultServiceErrorHandling(err);
      }
    });
    this.searchChangedObservable
      .pipe(debounceTime(300))
      .subscribe({next: () => this.filterPantry()});
  }

  onSubmit(form: NgForm) {
    if (form.valid) {
      this.addItem();
    }
  }

  markImportant() {
    if (this.newItem.lowerLimit !== null) {
      this.newLowerLimit = this.newItem.lowerLimit;
      this.newItem.lowerLimit = null;
    } else {
      this.newItem.lowerLimit = this.newLowerLimit;
    }
  }

  getPantry(id: number) {
    this.service.getPantryById(id).subscribe({
      next: res => {
        console.log(res.items);
        this.items = res.items;
      },
      error: err => {
        this.defaultServiceErrorHandling(err);
      }
    });
  }

  deleteItem(id: number) {
    this.service.deleteItem(this.id, id).subscribe({
      next: res => {
        console.log('deleted item: ', res)
        this.getPantry(this.id);
      },
      error: err => {
        this.defaultServiceErrorHandling(err);
      }
    });
  }

  filterPantry() {
    let search: PantrySearch = {
      details: this.searchString
    };

    this.service.filterPantry(this.id, search).subscribe({
      next: res => {
        this.items = res.items;
      },
      error: err => {
        this.defaultServiceErrorHandling(err);
      }
    });
  }

  addItem() {
    this.newLowerLimit = 0;
    this.service.createItem(this.id, this.newItem).subscribe({
      next: res => {
        console.log("Item created: ", res);
        this.newItem.amount = 0;
        this.newItem.unit = Unit.Piece;
        this.newItem.description = '';
        this.getPantry(this.id);
      },
      error: err => {
        this.defaultServiceErrorHandling(err);
      }
    });
  }

  editItem() {
    this.service.updateItem(this.itemToEdit, this.id).subscribe({
      next: dto => {
        this.selectedItem = dto;
      },
      error: error => {
        this.defaultServiceErrorHandling(error);
      }
    });
  }

  mergeItems() {
    this.service.mergeItems(this.mergeItem, this.id).subscribe({
      next: dto => {
        this.selectedItem = dto;
      },
      error: error => {
        this.defaultServiceErrorHandling(error);
      }
    });
  }

  changeAmount(item: PantryItemDetailDto, amountChanged: number) {
    item.amount = item.amount + amountChanged > 0 ? item.amount + amountChanged : 0;
    this.service.updateItem(item, this.id).subscribe({
      next: dto => {
        console.log(dto);
        this.selectedItem = dto;
      },
      error: error => {
        this.defaultServiceErrorHandling(error);
      }
    });
  }

  searchChanged() {
    this.searchChangedObservable.next();
  }

  private defaultServiceErrorHandling(error: any) {
    console.log(error);
    this.error = true;
    if (typeof error.error === 'object') {
      this.errorMessage = error.error.error;
    } else {
      this.errorMessage = error.error;
    }
  }

  selectItem(item: PantryItemDetailDto) {
    this.selectedItem = item;
  }

  selectEditItem(item: PantryItemDetailDto) {
    this.itemToEdit = item;
    this.mergeItem = {itemToDeleteId: null, result: clone(item)};
  }

  /**
   * Error flag will be deactivated, which clears the error message
   */
  vanishError() {
    this.error = false;
  }

  getUnitStep(item: PantryItemDetailDto, largeStep: boolean, positive: boolean): number {
    let value: number = item.amount < 1000 ? 1 : 10;
    let prefixNum = positive ? 1 : -1;
    switch (item.unit) {
      case Unit.Piece:
        value *= prefixNum;
        break
      case Unit.Gram:
        value *= prefixNum * 10;
        break;
      case Unit.Milliliter:
        value *= prefixNum * 10;
        break;
      default:
        console.error("Undefined unit");
    }

    return largeStep ? value * 10 : value;
  }

  getUnitStepString(value: number, itemAmount: number): string {
    if (value > 0) {
      if (itemAmount < 1000) {
        return "+" + value;
      } else {
        return "+" + value / 1000;
      }
    } else return itemAmount < 1000 ? value.toString() : (value / 1000).toString();
  }

  getItemAmount(itemAmount: number): number {
    return itemAmount >= 1000 ? itemAmount / 1000 : itemAmount;
  }

  getUnit(itemAmount: number, unit: Unit): string {
    switch (unit) {
      case Unit.Piece:
        return itemAmount == 1 ? "Piece" : "Pieces";
      case Unit.Gram:
        return itemAmount >= 1000 ? "Kilogram" : "Gram";
      case Unit.Milliliter:
        return itemAmount >= 1000 ? "Milliliter" : "Liter";
      default:
        console.error("Undefined unit");
    }
  }

  protected readonly Unit = Unit;
  protected readonly clone = clone;
  protected readonly displayQuantity = displayQuantity;
}
