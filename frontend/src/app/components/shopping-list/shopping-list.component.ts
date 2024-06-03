import {Component, Input, OnInit, SimpleChanges} from '@angular/core';
import { ShoppingListListDto } from 'src/app/dtos/shoppingList';
import { ShoppingListService } from 'src/app/services/shopping-list.service';
import {NgForOf} from "@angular/common";
import {RouterLink} from "@angular/router";

@Component({
  selector: 'app-shopping-list',
  standalone: true,
  imports: [
    NgForOf,
    RouterLink
  ],
  templateUrl: './shopping-list.component.html',
  styleUrl: './shopping-list.component.scss'
})
export class ShoppingListComponent {

  @Input() groupId!: number;

  shoppingLists: ShoppingListListDto[] = [];


  constructor(private shoppingListService: ShoppingListService) {
  }

  ngOnChanges(changes: SimpleChanges): void {
    // Get shopping lists for group
    if (!this.groupId) return;
    this.shoppingListService.getShoppingListsForGroup(this.groupId).subscribe({
      next: shoppingLists => {
        this.shoppingLists = shoppingLists;
      },
      error: err => {
        console.error(err);
      }
    })
  }


}
