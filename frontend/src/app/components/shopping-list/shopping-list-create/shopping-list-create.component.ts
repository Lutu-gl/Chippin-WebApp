import {Component, OnInit} from '@angular/core';
import {ShoppingListCreateEditDto} from "../../../dtos/shoppingList";
import {NgForm, NgModel} from "@angular/forms";
import {ShoppingListService} from "../../../services/shopping-list.service";
import {ToastrService} from "ngx-toastr";
import {ActivatedRoute, Router} from "@angular/router";
import {map, Observable, of} from "rxjs";
import {Category} from "../../../dtos/category";
import {GroupService} from "../../../services/group.service";
import {GroupDto} from "../../../dtos/group";
import {AuthService} from "../../../services/auth.service";


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
  currentUserId: number;
  groupId: number;
  shoppingListId: number;
  shoppingListDto: ShoppingListCreateEditDto = {
    name: "",
    categories: [],
    group: null,
  }

  constructor(private shoppingListService: ShoppingListService,
              private groupService: GroupService,
              private notification: ToastrService,
              private route: ActivatedRoute,
              private router: Router,
              private authService: AuthService) {
  }

  dummyCategorySelectionModel: unknown;
  dummyGroupSelectionModel: unknown;

  // let categorysuggestions be all categories from the Category-enum that include the input string
  categorySuggestions = (text: string): Observable<Category[]> => {
    if (!text) return of(Object.values(Category));
    return of(Object.values(Category).filter(c => c.toLowerCase().includes(text.toLowerCase())));
  }

  groupSuggestions = (input: string): Observable<any[]> => {
    console.log(this.groupService.getGroups());
    if (!input) return this.groupService.getGroups();
    return this.groupService.getGroups().pipe(
      map(groups => groups.filter(group => group.groupName.toLowerCase().includes(input.toLowerCase())))
    );
  }

  formatGroup = (model: GroupDto) => {
    if (!model) return "";
    return model.groupName;
  };

  ngOnInit(): void {
    // Set current userId
    this.currentUserId = this.authService.getUserId();
    // Get query param groupId
    this.route.queryParams.subscribe({
      next: params => {
        this.groupId = +params['groupId']
        this.groupService.getById(this.groupId).subscribe({
          next: group => {
            this.setGroup(group)
            this.dummyGroupSelectionModel = group;
            console.log(this.shoppingListDto)
          },
          error: err => {
            console.error(err);
          }
        })
      },
      error: err => {
        console.error(err);
      }
    })

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
          owner: shoppingList.owner,
          name: shoppingList.name,
          categories: shoppingList.categories,
          group: shoppingList.group
        };
        this.dummyGroupSelectionModel = shoppingList.group;
        console.log(this.shoppingListDto)
        console.log(this.currentUserId)
      },
      error: err => {
        console.error(err);
      }
    });
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

  setGroup(group: GroupDto ): void {
    if (!group) {
      this.shoppingListDto.group = null;
      return;
    }
    setTimeout(() => {
      this.shoppingListDto.group = group;
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
      this.shoppingListService.updateShoppingList(this.shoppingListId, this.shoppingListDto).subscribe({
        next: response => {
          this.notification.success(`Shopping list ${this.shoppingListDto.name} updated successfully`);
          console.log("Edit mode")
          this.router.navigate([`/shopping-list/${this.shoppingListId}`]);
        },
        error: err => {
          this.notification.error(`Failed to update shopping list ${this.shoppingListDto.name}`);
          console.error(err);
        }
      });
      return;
    }

    this.shoppingListService.createShoppingList(this.currentUserId, this.shoppingListDto).subscribe({
        next: response => {
          this.notification.success(`Shopping list ${this.shoppingListDto.name} created successfully`);
          console.log("Create mode")
          this.router.navigate([`/shopping-list/${response.id}`]);
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
