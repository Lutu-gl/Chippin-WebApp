import {Component, Input, OnInit, SimpleChanges} from '@angular/core';
import { ShoppingListListDto } from 'src/app/dtos/shoppingList';
import { ShoppingListService } from 'src/app/services/shopping-list.service';
import {NgForOf} from "@angular/common";
import {RouterLink} from "@angular/router";
import {AuthService} from "../../services/auth.service";
import {MessageService} from "primeng/api";

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
export class ShoppingListComponent implements OnInit{

  @Input() groupId!: number;

  shoppingLists: ShoppingListListDto[] = [];



  constructor(private shoppingListService: ShoppingListService,
              private authService: AuthService,
              private messageService: MessageService) {
  }

  ngOnInit(): void {
    if (!this.groupId) {
      // Get all shopping lists for the user
      this.shoppingListService.getShoppingListsForUser(this.authService.getUserId()).subscribe({
        next: shoppingLists => {
          this.shoppingLists = shoppingLists;
        },
        error: err => {
          console.error(err);
          this.messageService.add({severity: 'error', summary: 'Error', detail: 'Could not load shopping lists'});
        }
      });
      return;
    }
    // Get all shopping lists for the group
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
