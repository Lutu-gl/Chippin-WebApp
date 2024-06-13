import {Component, Input, OnInit} from '@angular/core';
import {CurrencyPipe, NgForOf, NgIf} from "@angular/common";
import {ShoppingListListDto} from "../../../dtos/shoppingList";
import {ShoppingListService} from "../../../services/shopping-list.service";
import {MessageService} from "primeng/api";
import {RouterLink} from "@angular/router";
import {ButtonModule} from "primeng/button";
import {ShoppingListCreateModalComponent} from "../shopping-list-create-modal/shopping-list-create-modal.component";

@Component({
  selector: 'app-shopping-lists-in-group',
  standalone: true,
  imports: [
    CurrencyPipe,
    NgForOf,
    NgIf,
    RouterLink,
    ButtonModule,
    ShoppingListCreateModalComponent
  ],
  templateUrl: './shopping-lists-in-group.component.html',
  styleUrl: './shopping-lists-in-group.component.scss'
})
export class ShoppingListsInGroupComponent implements OnInit {
  @Input() groupId!: number;
  groupShoppingLists: ShoppingListListDto[] = [];
  showCreateModal: boolean;

  constructor(
    private shoppingListService: ShoppingListService,
    private messageService: MessageService
  ) {
  }

  ngOnInit(): void {
    this.loadGroupShoppingLists();
  }

  loadGroupShoppingLists() {
    this.shoppingListService.getShoppingListsForGroup(this.groupId).subscribe({
      next: shoppingLists => {
        this.groupShoppingLists = shoppingLists;
      },
      error: error => {
        console.error('Error loading shopping lists for group', error);
        this.messageService.add({severity: 'error', summary: 'Error loading shopping lists for group', detail: error.message});
      }
    });
  }

  getSortedShoppingLists() {
    return this.groupShoppingLists.sort((a,b) => {
      return a.groupName.localeCompare(b.groupName)
    })
  }

}
