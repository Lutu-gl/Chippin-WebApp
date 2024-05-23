import {Component, OnInit} from '@angular/core';
import {ShoppingListCreateDto} from "../../../dtos/shoppingList";
import {FormsModule, NgForm, NgModel} from "@angular/forms";
import {KeyValuePipe, NgForOf, NgIf} from "@angular/common";
import {ShoppingListService} from "../../../services/shopping-list.service";
import {ToastrService} from "ngx-toastr";
import {ActivatedRoute, Router} from "@angular/router";
import {AppModule} from "../../../app.module";
import {Observable, of} from "rxjs";
import {Category} from "../../../dtos/category";


export enum ShoppingListCreateEditMode {
  create,
  edit,
}


@Component({
  selector: 'app-shopping-list-create',
  templateUrl: './shopping-list-create.component.html',
  styleUrl: './shopping-list-create.component.scss'
})
export class ShoppingListCreateComponent implements OnInit {
  mode: ShoppingListCreateEditMode = ShoppingListCreateEditMode.create;
  protected readonly ShoppingListCreateEditMode = ShoppingListCreateEditMode;

  groupId: number;
  shoppingListId: number;
  shoppingListDto: ShoppingListCreateDto = {
    name: "",
    categories: [],
    group: null,
  }


  // let categorysuggestions be all categories from the Category-enum that include the input string
  categorySuggestions = (text: string): Observable<Category[]> => {
    return of(Object.values(Category).filter(c => c.includes(text)));
  }

  dummyCategorySelectionModel: unknown;

  ngOnInit(): void {
    this.route.data.subscribe({
      next: data => {
        this.mode = data['mode'];
      },
      error: err => {
        console.error(err);
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
    this.shoppingListService.getShoppingListById(this.shoppingListId).subscribe({
      next: shoppingList => {
        this.shoppingListDto = {
          name: shoppingList.name,
          categories: shoppingList.categories,
          group: shoppingList.groupId
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

  addCategory(category: Category): void {
    if (!category || this.shoppingListDto.categories.includes(category)) {
      this.dummyCategorySelectionModel = null;
      return;
    }
    setTimeout(() => {
      this.dummyCategorySelectionModel = null;
      this.shoppingListDto.categories.push(category);
    })
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
      this.shoppingListService.updateShoppingList(this.groupId, this.shoppingListId, this.shoppingListDto).subscribe({
        next: response => {
          this.notification.success(`Shopping list ${this.shoppingListDto.name} updated successfully`);
          console.log("Edit mode")
          this.router.navigate([`/group/${this.groupId}/shoppingList/${this.shoppingListId}`]);
        },
        error: err => {
          this.notification.error(`Failed to update shopping list ${this.shoppingListDto.name}`);
          console.error(err);
        }
      });
      return;
    }

    this.shoppingListService.createShoppingList(this.groupId, this.shoppingListDto).subscribe({
        next: response => {
          this.notification.success(`Shopping list ${this.shoppingListDto.name} created successfully`);
          console.log("Create mode")
          this.router.navigate([`/group/${this.groupId}/shoppingList/${response.id}`]);
        },
        error: err => {
          this.notification.error(`Failed to create shopping list ${this.shoppingListDto.name}`);
          console.error(err)
        }
      }
    )


  }

  formatCategory = (category: Category) => Category[category];

  removeCategory(category: Category) {
    this.shoppingListDto.categories = this.shoppingListDto.categories.filter(c => c !== category);
  }
}
