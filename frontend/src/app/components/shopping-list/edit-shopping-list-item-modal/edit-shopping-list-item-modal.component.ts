import {Component, EventEmitter, Input, OnChanges, Output, ViewChild} from '@angular/core';
import {ShoppingListItemDto, ShoppingListItemUpdateDto} from "../../../dtos/shoppingList";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {KeyValuePipe, NgForOf, NgIf} from "@angular/common";
import {ToastrService} from "ngx-toastr";
import {AuthService} from "../../../services/auth.service";
import {ShoppingListService} from "../../../services/shopping-list.service";
import {ItemDetailDto, Unit} from "../../../dtos/item";

@Component({
  selector: 'app-edit-shopping-list-item-modal',
  standalone: true,
  imports: [
    FormsModule,
    KeyValuePipe,
    NgForOf,
    NgIf,
    ReactiveFormsModule
  ],
  templateUrl: './edit-shopping-list-item-modal.component.html',
  styleUrl: './edit-shopping-list-item-modal.component.scss'
})
export class EditShoppingListItemModalComponent implements OnChanges {
  @Input() shoppingListItem: ShoppingListItemDto;
  @Input() shoppingListId: number;
  @Output() onEdit = new EventEmitter<void>();
  @ViewChild('modalClose') modalClose;

  itemToEdit: ItemDetailDto = undefined;
  protected readonly Unit = Unit;

  constructor(private notifications: ToastrService,
              private authService: AuthService,
              private shoppingListService: ShoppingListService) {
  }

  reset() {
    this.modalClose.nativeElement.click();
  }

  ngOnChanges(): void {
    if (!this.shoppingListItem) return;
    this.itemToEdit = {
      id: this.shoppingListItem.item.id,
      amount: this.shoppingListItem.item.amount,
      unit: this.shoppingListItem.item.unit,
      description: this.shoppingListItem.item.description,
    }
  }

  onSubmit() {

    let updateItem: ShoppingListItemUpdateDto = {
      id: this.shoppingListItem.id,
      item: this.itemToEdit,
      checked: !!this.shoppingListItem.checkedById
    }
    this.shoppingListService.updateShoppingListItem(this.authService.getUserId(), this.shoppingListId, updateItem).subscribe({
        next: () => {
          this.notifications.success('Item updated');
          this.reset();
          this.onEdit.emit();
        },
        error: err => {
          this.notifications.error('Error updating item');
          console.error(err);
        }
      }
    )

  }
}
