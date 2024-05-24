import {Component, OnInit, ViewChild} from '@angular/core';
import {ShoppingListDetailDto, ShoppingListItemDto, ShoppingListItemUpdateDto} from "../../../dtos/shoppingList";
import {ShoppingListService} from "../../../services/shopping-list.service";
import {ActivatedRoute, Router, RouterLink} from "@angular/router";
import {ToastrService} from "ngx-toastr";
import {NgClass, NgForOf, NgIf} from "@angular/common";
import {
  ShoppingListItemListItemComponent
} from "../shopping-list-item-list-item/shopping-list-item-list-item.component";
import {AuthService} from "../../../services/auth.service";
import {FormsModule} from "@angular/forms";

@Component({
  selector: 'app-shopping-list-detail',
  standalone: true,
  imports: [
    RouterLink,
    NgForOf,
    NgIf,
    ShoppingListItemListItemComponent,
    FormsModule,
    NgClass,
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
    this.shoppingListService.deleteShoppingList(this.groupId, this.shoppingListId).subscribe({
      next: () => {
        this.notification.success('Shopping list deleted');
        this.router.navigate(['/', 'group', this.groupId]);
      },
      error: err => {
        console.error(err);
        this.notification.error('Error deleting shopping list');
      }
    });
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
    this.notification.info("Not implemented yet")
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


}
