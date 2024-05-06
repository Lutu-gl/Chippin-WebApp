import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {PantryService} from "../../services/pantry.service";
import {ItemCreateDto, ItemDetailDto, Unit} from "../../dtos/item";
import {KeyValuePipe, NgForOf, NgIf} from "@angular/common";
import {FormsModule, NgForm} from "@angular/forms";
import {debounceTime, Subject} from "rxjs";
import {PantrySearch} from "../../dtos/pantry";
import {ConfirmDeleteDialogComponent} from "../confirm-delete-dialog/confirm-delete-dialog.component";

@Component({
  selector: 'app-pantry',
  standalone: true,
  imports: [
    NgForOf,
    KeyValuePipe,
    NgIf,
    FormsModule,
    ConfirmDeleteDialogComponent
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
  itemForDeletion: ItemDetailDto = undefined;
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

  setItemForDeletion(item: ItemDetailDto) {
    this.itemForDeletion = item;
  }

  /**
   * Error flag will be deactivated, which clears the error message
   */
  vanishError() {
    this.error = false;
  }

  protected readonly Unit = Unit;
  protected readonly onsubmit = onsubmit;
}
