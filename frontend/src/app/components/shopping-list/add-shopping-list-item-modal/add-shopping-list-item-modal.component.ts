import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {KeyValuePipe, NgForOf, NgIf} from "@angular/common";
import {DisplayedUnit, ItemCreateDto, Unit} from "../../../dtos/item";
import {FormsModule, NgForm} from "@angular/forms";
import {ToastrService} from "ngx-toastr";
import {AuthService} from "../../../services/auth.service";
import {ShoppingListService} from "../../../services/shopping-list.service";

@Component({
  selector: 'app-add-shopping-list-item-modal',
  standalone: true,
  imports: [
    NgIf,
    FormsModule,
    KeyValuePipe,
    NgForOf
  ],
  templateUrl: './add-shopping-list-item-modal.component.html',
  styleUrl: './add-shopping-list-item-modal.component.scss'
})
export class AddShoppingListItemModalComponent implements OnInit {
  @Input() shoppingListId: number;
  // Optionally input initial values
  @Input() initialItem: ItemCreateDto;
  @ViewChild('modalClose') modalClose;
  @Output() onAdd = new EventEmitter<void>();


  itemToEdit: ItemCreateDto = {
    amount: 0,
    unit: Unit.Piece,
    description: "",
  };
  protected readonly DisplayedUnit = DisplayedUnit;
  protected readonly Unit = Unit;

  constructor(private notifications: ToastrService,
              private authService: AuthService,
              private shoppingListService: ShoppingListService) {
  }

  ngOnInit() {
    if (this.initialItem) {
      this.itemToEdit = this.initialItem;
    }
  }

  reset() {
    this.itemToEdit = {
      amount: 0,
      unit: Unit.Piece,
      description: "",
    }
    this.modalClose.nativeElement.click();
  }

  onSubmit(form: NgForm) {
    console.log("submitting form")
    if (!form.valid) {
      this.notifications.error('Please fill out all fields');
    }
    this.shoppingListService.addShoppingListItemToShoppingList(this.authService.getUserId(), this.shoppingListId, this.itemToEdit).subscribe({
        next: () => {
          this.notifications.success('Item added to shopping list');
          this.onAdd.emit();
        },
        error: err => {
          this.notifications.error('Error adding item to shopping list');
          console.error(err);
        }
      }
    )

  }
}
