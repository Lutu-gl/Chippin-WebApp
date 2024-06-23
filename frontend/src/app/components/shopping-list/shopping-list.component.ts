import {Component, Input, OnInit, SimpleChanges} from '@angular/core';
import {ShoppingListListDto} from 'src/app/dtos/shoppingList';
import {ShoppingListService} from 'src/app/services/shopping-list.service';
import {DecimalPipe, NgForOf, NgIf} from "@angular/common";
import {RouterLink} from "@angular/router";
import {AuthService} from "../../services/auth.service";
import {MessageService} from "primeng/api";
import {ButtonModule} from "primeng/button";
import {DialogModule} from "primeng/dialog";
import {ShoppingListCreateModalComponent} from "./shopping-list-create-modal/shopping-list-create-modal.component";

@Component({
  selector: 'app-shopping-list',
  standalone: true,
  imports: [
    NgForOf,
    RouterLink,
    DecimalPipe,
    NgIf,
    ButtonModule,
    DialogModule,
    ShoppingListCreateModalComponent,
  ],
  templateUrl: './shopping-list.component.html',
  styleUrl: './shopping-list.component.scss'
})
export class ShoppingListComponent implements OnInit {

  shoppingLists: ShoppingListListDto[] = [];

  shoppingListsGrouped: { groupName: string, shoppingLists: ShoppingListListDto[] }[] = [];
  showCreateModal: boolean = false;


  constructor(private shoppingListService: ShoppingListService,
              private authService: AuthService,
              private messageService: MessageService) {
  }

  ngOnInit(): void {
    this.loadShoppingLists();
  }

  loadShoppingLists() {
    // Get all shopping lists for the user
    this.shoppingListService.getShoppingListsForUser(this.authService.getUserId()).subscribe({
      next: shoppingLists => {
        this.shoppingLists = shoppingLists;
        this.groupShoppingLists();
        console.log(this.shoppingListsGrouped)
      },
      error: err => {
        console.error(err);
        this.messageService.add({severity: 'error', summary: 'Error', detail: 'Could not load shopping lists'});
      }
    });
  }

  groupShoppingLists() {
    this.shoppingListsGrouped = [];
    this.shoppingLists.forEach(shoppingList => {
      const group = this.shoppingListsGrouped.find(group => group.groupName === shoppingList.groupName);
      if (group) {
        group.shoppingLists.push(shoppingList);
      } else if (!shoppingList.groupName) {
        if (this.shoppingListsGrouped.find(group => group.groupName === 'Your lists')) {
          this.shoppingListsGrouped.find(group => group.groupName === 'Your lists').shoppingLists.push(shoppingList);
          return;
        }
        this.shoppingListsGrouped.push({groupName: 'Your lists', shoppingLists: [shoppingList]});
      } else {
        this.shoppingListsGrouped.push({groupName: shoppingList.groupName, shoppingLists: [shoppingList]});
      }
    });

    // Sort the shopping lists by group name and start with 'Your lists'
    this.shoppingListsGrouped.sort((a, b) => {
      if (a.groupName === 'Your lists') {
        return -1;
      } else if (b.groupName === 'Your lists') {
        return 1;
      } else {
        return a.groupName.localeCompare(b.groupName);
      }
    });
    // Sort the shopping lists within each group by date
    this.shoppingListsGrouped.forEach(group => {
      group.shoppingLists.sort((a, b) => {
        return new Date(b.updatedAt).getTime() - new Date(a.updatedAt).getTime();
      });
    });
  }





}
