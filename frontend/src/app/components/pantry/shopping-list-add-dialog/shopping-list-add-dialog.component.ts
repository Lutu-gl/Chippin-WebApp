import {Component, Input, OnInit} from '@angular/core';
import {PantryItemDetailDto} from "../../../dtos/item";
import {ShoppingListListDto} from "../../../dtos/shoppingList";
import {ShoppingListService} from "../../../services/shopping-list.service";
import {NgForOf, NgIf} from "@angular/common";
import {ActivatedRoute} from "@angular/router";
import {displayQuantity} from "../../../util/unit-helper";
import {FormsModule} from "@angular/forms";

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
  selectedList: ShoppingListListDto;

  constructor(
    private route: ActivatedRoute,
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

  addToShoppingList() {
    //this.shoppingListService.updateShoppingList()
  }

  protected readonly displayQuantity = displayQuantity;
}
