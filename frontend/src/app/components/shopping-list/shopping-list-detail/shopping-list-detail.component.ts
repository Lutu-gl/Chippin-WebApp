import {Component, OnInit, ViewChild} from '@angular/core';
import {ShoppingListDetailDto} from "../../../dtos/shoppingList";
import {ShoppingListService} from "../../../services/shopping-list.service";
import {ActivatedRoute, Router, RouterLink} from "@angular/router";
import {ToastrService} from "ngx-toastr";
import {NgForOf, NgIf} from "@angular/common";

@Component({
  selector: 'app-shopping-list-detail',
  standalone: true,
  imports: [
    RouterLink,
    NgForOf,
    NgIf,
  ],
  templateUrl: './shopping-list-detail.component.html',
  styleUrl: './shopping-list-detail.component.scss'
})
export class ShoppingListDetailComponent implements OnInit{

  constructor(private shoppingListService: ShoppingListService,
              private route: ActivatedRoute,
              private notification: ToastrService,
              private router: Router) {
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
}
