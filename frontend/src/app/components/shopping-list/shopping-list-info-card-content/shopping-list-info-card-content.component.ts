import {Component, OnInit} from '@angular/core';
import {ShoppingListListDto} from "../../../dtos/shoppingList";
import {NgForOf, NgIf, SlicePipe} from "@angular/common";
import {ShoppingListService} from "../../../services/shopping-list.service";
import {AuthService} from "../../../services/auth.service";
import {Observable} from "rxjs";
import {RouterLink} from "@angular/router";
import {ButtonModule} from "primeng/button";
import {AutoCompleteCompleteEvent, AutoCompleteModule} from "primeng/autocomplete";
import {FormsModule} from "@angular/forms";

@Component({
  selector: 'app-shopping-list-info-card-content',
  standalone: true,
  imports: [
    NgForOf,
    NgIf,
    SlicePipe,
    RouterLink,
    ButtonModule,
    AutoCompleteModule,
    FormsModule
  ],
  templateUrl: './shopping-list-info-card-content.component.html',
  styleUrl: './shopping-list-info-card-content.component.scss'
})
export class ShoppingListInfoCardContentComponent implements OnInit {

  shoppingLists: ShoppingListListDto[] = [];
  currentUserId: number;
  suggestions: any[];

  constructor(private shoppingListService: ShoppingListService,
              private authService: AuthService) { }

  ngOnInit() {
    // Get the current user's id
    this.currentUserId = this.authService.getUserId();
    // Load shopping lists
    this.loadShoppingLists().subscribe({
      next: shoppingLists => this.shoppingLists = shoppingLists,
      error: err => console.error('Error loading shopping lists', err)
    })


  }

  loadShoppingLists(): Observable<ShoppingListListDto[]> {
    return this.shoppingListService.getShoppingListsForUser(this.currentUserId);
  }

  search($event: AutoCompleteCompleteEvent) {
    this.suggestions = [
      "Apple",
      "Banana",
      "Cherry",
      "Date",
      "Elderberry",
      "Fig",
      "Grape",
      "Kiwi",
      "Lemon",
      "Mango",
      "Nectarine",
      "Orange",

    ]
  }
}
