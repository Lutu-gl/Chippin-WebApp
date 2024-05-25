import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {PantryService} from "../../services/pantry.service";
import {
  DisplayedUnit,
  PantryItemCreateDisplayDto,
  PantryItemCreateDto,
  PantryItemDetailDto,
  PantryItemMergeDto,
  Unit
} from "../../dtos/item";
import {KeyValuePipe, NgForOf, NgIf, NgSwitch, NgSwitchCase} from "@angular/common";
import {FormsModule, NgForm} from "@angular/forms";
import {debounceTime, Subject} from "rxjs";
import {PantrySearch} from "../../dtos/pantry";
import {ConfirmDeleteDialogComponent} from "../confirm-delete-dialog/confirm-delete-dialog.component";
import {EditPantryItemDialogComponent} from "./edit-pantry-item-dialog/edit-pantry-item-dialog.component";
import {clone} from "lodash";
import {displayQuantity, unitToDisplayedUnit} from "../../util/unit-helper";
import {ToastrService} from "ngx-toastr";

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
  newItem: PantryItemCreateDisplayDto = {
    amount: 0,
    unit: DisplayedUnit.Piece,
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
    private service: PantryService,
    private notification: ToastrService
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
        this.getPantry(this.id);
        this.notification.success('Item deleted');
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

  convertItemDto(item: PantryItemCreateDisplayDto): PantryItemCreateDto {
    let updatedUnit: Unit;
    let factor: number = 1;
    switch (item.unit) {
      case DisplayedUnit.Gram:
        updatedUnit = Unit.Gram;
        break;
      case DisplayedUnit.Kilogram:
        updatedUnit = Unit.Gram;
        factor = 1000;
        break;
      case DisplayedUnit.Milliliter:
        updatedUnit = Unit.Milliliter;
        break;
      case DisplayedUnit.Liter:
        updatedUnit = Unit.Milliliter;
        factor = 1000;
        break;
      case DisplayedUnit.Piece:
        updatedUnit = Unit.Piece;
        break;
      case DisplayedUnit.Pieces:
        updatedUnit = Unit.Piece;
        break;
    }

    return {
      description: item.description,
      lowerLimit: item.lowerLimit,
      amount: item.amount * factor,
      unit: updatedUnit,
    }
  }

  addItem() {
    this.newLowerLimit = 0;
    this.service.createItem(this.id, this.convertItemDto(this.newItem)).subscribe({
      next: res => {
        this.notification.success('Added ' + this.newItem.amount + ' ' + this.newItem.unit + ' ' + this.newItem.description);
        this.newItem.amount = 0;
        this.newItem.unit = DisplayedUnit.Piece;
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
        this.notification.success('Edited ' + dto.description);
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
        this.notification.success('Items merged');
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
    this.notification.error(this.errorMessage);
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
    let value: number = 1; //item.amount < 1000 ? 1 : 10;
    let prefixNum = positive ? 1 : -1;
    switch (item.unit) {
      case Unit.Piece:
        value = item.amount < 100 ? value : value * 5;
        break
      case Unit.Gram:
        value = item.amount < 1000 ? value * 10 : value * 100;
        break;
      case Unit.Milliliter:
        value = item.amount < 1000 ? value * 10 : value * 100;
        break;
      default:
        console.warn("Undefined unit");
    }
    value *= prefixNum;
    return largeStep ? value * 10 : value;
  }

  getUnitStepString(value: number, item: PantryItemDetailDto): string {
    if (item.unit === Unit.Piece) {
      if (value > 0) {
        return "+" + value;
      } else {
        return value.toString();
      }
    }
    if (value > 0) {
      if (item.amount < 1000) {
        return "+" + value;
      } else {
        return "+" + value / 1000;
      }
    } else return item.amount < 1000 ? value.toString() : (value / 1000).toString();
  }

  protected readonly Unit = Unit;
  protected readonly clone = clone;
  protected readonly displayQuantity = displayQuantity;
  protected readonly DisplayedUnit = DisplayedUnit;
  protected readonly unitToDisplayedUnit = unitToDisplayedUnit;
  protected readonly undefined = undefined;
}
