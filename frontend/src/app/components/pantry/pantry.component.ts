import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {PantryService} from "../../services/pantry.service";
import {ItemCreateDto, ItemDetailDto, Unit} from "../../dtos/item";
import {KeyValuePipe, NgForOf, NgIf, NgSwitch, NgSwitchCase} from "@angular/common";
import {FormsModule, NgForm} from "@angular/forms";
import {debounceTime, Subject} from "rxjs";
import {PantrySearch} from "../../dtos/pantry";
import {ConfirmDeleteDialogComponent} from "../confirm-delete-dialog/confirm-delete-dialog.component";
import {EditPantryItemDialogComponent} from "../edit-pantry-item-dialog/edit-pantry-item-dialog.component";
import {clone} from "lodash";

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

  items: ItemDetailDto[];
  newItem: ItemCreateDto = {
    amount: 0,
    unit: Unit.Piece,
    description: ""
  };
  selectedItem: ItemDetailDto = undefined;
  itemToEdit: ItemDetailDto = undefined;
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
    console.log('is form valid?', form.valid);
    if (form.valid) {
      this.addItem();
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

  changeAmount(item: ItemDetailDto, amountChanged: number) {
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

  selectItem(item: ItemDetailDto) {
    this.selectedItem = item;
  }

  selectEditItem(item: ItemDetailDto) {
    this.itemToEdit = item;
  }

  /**
   * Error flag will be deactivated, which clears the error message
   */
  vanishError() {
    this.error = false;
  }

  getUnitStep(unit: Unit, largeStep: boolean, positive: boolean): number {
    let value: number = 0;
    let prefixNum = positive === true ? 1 : -1;
    switch (unit) {
      case Unit.Piece:
        value = prefixNum * 1;
        break
      case Unit.Gram:
        value = prefixNum * 10;
        break;
      case Unit.Kilogram:
        value = prefixNum * .1;
        break;
      case Unit.Liter:
        value = prefixNum * .1;
        break;
      case Unit.Milliliter:
        value = prefixNum * 10;
        break;
      case Unit.Tablespoon:
        value = prefixNum * 1;
        break;
      case Unit.Teaspoon:
        value = prefixNum * 1;
        break;
      default:
        console.error("Undefined unit");
    }

    return largeStep ? value * 10 : value;
  }

  getUnitStepString(value: number): string {
    if (value > 0) return "+" + value;
    else return value.toString();

  }

  protected readonly Unit = Unit;
  protected readonly clone = clone;
}
