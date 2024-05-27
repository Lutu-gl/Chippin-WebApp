import {Component, OnInit} from '@angular/core';
import {ShoppingListDetailDto, ShoppingListItemDto, ShoppingListItemUpdateDto} from "../../../dtos/shoppingList";
import {ShoppingListService} from "../../../services/shopping-list.service";
import {ActivatedRoute, Router, RouterLink} from "@angular/router";
import {ToastrService} from "ngx-toastr";
import {NgClass, NgForOf, NgIf} from "@angular/common";
import {AuthService} from "../../../services/auth.service";
import {FormsModule} from "@angular/forms";
import {ConfirmDeleteDialogComponent} from "../../confirm-delete-dialog/confirm-delete-dialog.component";
import {
  AddShoppingListItemModalComponent
} from "../add-shopping-list-item-modal/add-shopping-list-item-modal.component";
import {
  EditShoppingListItemModalComponent
} from "../edit-shopping-list-item-modal/edit-shopping-list-item-modal.component";

@Component({
  selector: 'app-shopping-list-detail',
  standalone: true,
  imports: [
    RouterLink,
    NgForOf,
    NgIf,
    FormsModule,
    NgClass,
    ConfirmDeleteDialogComponent,
    AddShoppingListItemModalComponent,
    EditShoppingListItemModalComponent,
  ],
  templateUrl: './shopping-list-detail.component.html',
  styleUrl: './shopping-list-detail.component.scss'
})
export class ShoppingListDetailComponent implements OnInit{

  constructor(private shoppingListService: ShoppingListService,
              private route: ActivatedRoute,
              private notification: ToastrService,
              private router: Router,
              private authService: AuthService) {
  }

  shoppingListDetailDto: ShoppingListDetailDto;
  groupId: number;
  shoppingListId: number;
  selectedItem: ShoppingListItemDto;

  ngOnInit(): void {
    this.route.params.subscribe({
      next: params => {
        this.groupId = +params['id'];
        this.shoppingListId = +params['shoppingListId'];
      },
      error: err => {
        this.notification.error('Error loading shopping list');
        this.router.navigate(['/group', this.groupId]);
        console.error(err);
      }
    });
    this.loadShoppingListDetailDto();
  }

  loadShoppingListDetailDto(): void {
    this.shoppingListService.getShoppingListById(this.shoppingListId).subscribe({
      next: shoppingList => {
        this.shoppingListDetailDto = shoppingList;
      },
      error: err => {
        console.error(err);
      }
    });
  }

  deleteShoppingList() {
    this.shoppingListService.deleteShoppingList(this.shoppingListId).subscribe({
      next: () => {
        this.notification.success('Shopping list deleted');
        this.router.navigate(['/']);
      },
      error: err => {
        console.error(err);
        this.notification.error('Error deleting shopping list');
      }
    });
  }

  selectItem(item: ShoppingListItemDto) {
    this.selectedItem = item;
  }

  deleteItem(itemId: number) {
    this.shoppingListService.deleteShoppingListItem(this.authService.getUserId(), this.shoppingListId, itemId).subscribe({
      next: value => {
        this.loadShoppingListDetailDto();
      },
      error: err => {
        console.error(err);
      }
    })
  }

  addItemToPantry(itemId: number) {
    this.shoppingListService.moveShoppingListItemToPantry(this.authService.getUserId(), this.shoppingListId, itemId).subscribe({
      next: value => {
        this.loadShoppingListDetailDto();
        this.notification.success("Item moved to pantry");
      },
      error: err => {
        console.error(err);
      }
    })
  }

  toggleChecked(itemId: number) {
    let shoppingListItem = this.shoppingListDetailDto.items.find(item => item.id === itemId);
    let shoppingListItemUpdateDto: ShoppingListItemUpdateDto = {
      id: shoppingListItem.id,
      item: shoppingListItem.item,
      checked: !shoppingListItem.checkedById
    }

    this.shoppingListService.updateShoppingListItem(this.authService.getUserId(), this.shoppingListId, shoppingListItemUpdateDto ).subscribe({
      next: value => {
        this.loadShoppingListDetailDto();
      },
      error: err => {
        console.error(err);
      }
    })
  }

  addCheckedItemsToPantry() {
    this.shoppingListService.moveShoppingListItemsToPantry(this.authService.getUserId(), this.shoppingListId).subscribe({
      next: value => {
        this.loadShoppingListDetailDto();
        this.notification.success("Checked items moved to pantry");
      },
      error: err => {
        console.error(err);
      }
    })
  }
}
