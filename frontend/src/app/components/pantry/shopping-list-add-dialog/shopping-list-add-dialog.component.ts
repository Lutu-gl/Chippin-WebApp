import {Component, Input, OnInit} from '@angular/core';
import {ItemCreateDto, PantryItemDetailDto} from "../../../dtos/item";
import {ShoppingListCreateEditDto, ShoppingListListDto} from "../../../dtos/shoppingList";
import {ShoppingListService} from "../../../services/shopping-list.service";
import {NgForOf, NgIf} from "@angular/common";
import {ActivatedRoute} from "@angular/router";
import {displayQuantity} from "../../../util/unit-helper";
import {FormsModule} from "@angular/forms";
import {Category} from "../../../dtos/category";
import {AuthService} from "../../../services/auth.service";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-shopping-list-add-dialog',
  standalone: true,
  imports: [
    NgForOf,
    NgIf,
    FormsModule
  ],
  templateUrl: './shopping-list-add-dialog.component.html',
  styleUrl: './shopping-list-add-dialog.component.scss'
})
export class ShoppingListAddDialogComponent implements OnInit {
  @Input() item: PantryItemDetailDto;
  shoppingLists: ShoppingListListDto[];
  selectedList: ShoppingListListDto = null;

  constructor(
    private route: ActivatedRoute,
    private notifications: ToastrService,
    private authService: AuthService,
    private shoppingListService: ShoppingListService
  ) {
  }

  ngOnInit(): void {
    this.route.params.subscribe({
      next: params => {
        let id: number = +params['id'];
        this.shoppingListService.getShoppingListsForGroup(id).subscribe({
          next: res => {
            this.shoppingLists = res;
            if(this.shoppingLists.length > 0) {
              this.selectedList = this.shoppingLists[0];
            }
          },
          error: err => {
            console.log(err);
          }
        })
      },
      error: err => {
        console.error(err);
      }
    });
  }

  reset() {
    if(this.shoppingLists.length > 0) {
      this.selectedList = this.shoppingLists[0];
    } else {
      this.selectedList = null;
    }
  }

  addToShoppingList() {
    let itemDto: ItemCreateDto = {
      description: this.item.description,
      amount: this.item.lowerLimit - this.item.amount,
      unit: this.item.unit
    }

    this.shoppingListService.addShoppingListItemToShoppingList(this.authService.getUserId(), this.selectedList.id, itemDto).subscribe({
      next: res => {
        this.notifications.success(
          `Added ${displayQuantity(res.item.unit, res.item.amount)[1]} ${displayQuantity(res.item.unit, res.item.amount)[0]} ${res.item.description} to ${this.selectedList.name}`);
        this.reset();
      }, error: err => {
        this.notifications.error('Error adding item to shopping list');
        this.reset();
      }
    })
  }

  protected readonly displayQuantity = displayQuantity;
  protected readonly undefined = undefined;
}
