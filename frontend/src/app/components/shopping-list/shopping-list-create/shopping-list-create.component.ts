import {Component, OnInit} from '@angular/core';
import {ShoppingListCreateDto} from "../../../dtos/shoppingList";
import {Unit} from "../../../dtos/item";
import {FormsModule, NgForm, NgModel} from "@angular/forms";
import {KeyValuePipe, NgForOf, NgIf} from "@angular/common";
import {ShoppingListService} from "../../../services/shopping-list.service";
import {ToastrService} from "ngx-toastr";
import {ActivatedRoute, Router} from "@angular/router";


export enum ShoppingListCreateEditMode {
  create,
  edit,
}


@Component({
  selector: 'app-shopping-list-create',
  standalone: true,
  imports: [
    FormsModule,
    KeyValuePipe,
    NgForOf,
    NgIf
  ],
  templateUrl: './shopping-list-create.component.html',
  styleUrl: './shopping-list-create.component.scss'
})
export class ShoppingListCreateComponent implements OnInit {
  mode: ShoppingListCreateEditMode = ShoppingListCreateEditMode.create;

  shoppingListCreateDto: ShoppingListCreateDto = {
    name: "",
    budget: null
  };
  groupId: number;
  shoppingListId: number;

  ngOnInit(): void {
    this.route.data.subscribe({
      next: data => {
        this.mode = data['mode'];
      },
      error: err => {
        console.error(err);
      }
    })
    this.route.params.subscribe({
      next: params => {
        this.groupId = +params['id'];
      }
    })
    if (this.mode === ShoppingListCreateEditMode.edit) {
      this.route.params.subscribe({
        next: params => {
          this.shoppingListId = +params['shoppingListId'];
        },
        error: err => {
          console.error(err);
        }
      });
      this.loadShoppingListDetailDto();
    }
  }

  loadShoppingListDetailDto(): void {
    this.shoppingListService.getShoppingListById(this.groupId, this.shoppingListId).subscribe({
      next: shoppingList => {
        this.shoppingListCreateDto = {
          name: shoppingList.name,
          budget: shoppingList.budget
        };
      },
      error: err => {
        console.error(err);
      }
    });
  }

  constructor(private shoppingListService: ShoppingListService,
              private notification: ToastrService,
              private route: ActivatedRoute,
              private router: Router) {
  }

  public dynamicCssClassesForInput(input: NgModel): any {
    return {
      'is-invalid': !input.valid && !input.pristine,
    };
  }

  onSubmit(form: NgForm) {
    if (!form.valid) {
      this.notification.error('Please fill out all required fields');
      return;
    }

    if (this.mode === ShoppingListCreateEditMode.edit) {
      this.shoppingListService.updateShoppingList(this.groupId, this.shoppingListId, this.shoppingListCreateDto).subscribe({
        next: response => {
          this.notification.success(`Shopping list ${this.shoppingListCreateDto.name} updated successfully`);
          this.router.navigate([`/group/${this.groupId}/shoppingList/${this.shoppingListId}`]);
        },
        error: err => {
          this.notification.error(`Failed to update shopping list ${this.shoppingListCreateDto.name}`);
          console.error(err);
        }
      });
      return;
    }

    this.shoppingListService.createShoppingList(this.groupId, this.shoppingListCreateDto).subscribe({
      next: response => {
        this.notification.success(`Shopping list ${this.shoppingListCreateDto.name} created successfully`);
        this.router.navigate([`/group/${this.groupId}/shoppingList/${response.id}`]);
      },
      error: err => {
        this.notification.error(`Failed to create shopping list ${this.shoppingListCreateDto.name}`);
        console.error(err)
      }
      }
    )


  }

  protected readonly ShoppingListCreateEditMode = ShoppingListCreateEditMode;
}
