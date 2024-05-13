import {Component, OnInit} from "@angular/core";
import {UserSelection} from "../../../dtos/user";
import {UserService} from "../../../services/user.service";
import {ActivatedRoute, Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";
import {NgForm, NgModel} from "@angular/forms";
import {map, Observable} from "rxjs";
import {RecipeCreateDto, RecipeDetailDto} from "../../../dtos/recipe";
import {ItemDetailDto} from "../../../dtos/item";
import {RecipeService} from "../../../services/recipe.service";

export enum RecipeCreateEditMode {
  create,
  edit,
}

@Component({
  selector: 'app-recipe-create-edit',
  templateUrl: './recipe-create-edit.component.html',
  styleUrl: './recipe-create-edit.component.scss'
})

export class RecipeCreateEditComponent implements OnInit {
  mode: RecipeCreateEditMode = RecipeCreateEditMode.create;

  recipe: RecipeDetailDto = {
    id: 0,
    name: '',
    ingredients: [],
    description: '',
    isPublic: false
  };
  ingredients: (ItemDetailDto | null)[] = new Array(0);
  dummyMemberSelectionModel: unknown; // Just needed for the autocomplete

  constructor(
    private service: RecipeService,
    private userService: UserService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
  ) {
  }

  public get heading(): string {
    switch (this.mode) {
      case RecipeCreateEditMode.create:
        return 'Create New Recipe';
      case RecipeCreateEditMode.edit:
        return 'Edit Recipe';
      default:
        return '?';
    }
  }

  public get submitButtonText(): string {
    switch (this.mode) {
      case RecipeCreateEditMode.create:
        return 'Create';
      case RecipeCreateEditMode.edit:
        return 'Edit';
      default:
        return '?';
    }
  }

  get modeIsCreate(): boolean {
    return this.mode === RecipeCreateEditMode.create;
  }



  private get modeActionFinished(): string {
    switch (this.mode) {
      case RecipeCreateEditMode.create:
        return 'created';
      case RecipeCreateEditMode.edit:
        return 'edited';
      default:
        return '?';
    }
  }

  ngOnInit(): void {
    this.route.data.subscribe(data => {
      this.mode = data.mode;
    });


    let emailString = this.userService.getUserEmail();
    if(emailString === null) {
      this.notification.error(`You need to be logged in to create a recipe. Please logout and login again.`);
      return;
    }

    if (!this.modeIsCreate) {
      this.getRecipe();
    }
  }
  getRecipe(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.service.getRecipeById(id)
      .subscribe(pRecipe => {
        this.recipe = pRecipe;
        this.ingredients = pRecipe.ingredients;
        this.recipe.description=pRecipe.description;
        this.recipe.isPublic=pRecipe.isPublic;
        this.recipe.name=pRecipe.name;
      });
  }

  public dynamicCssClassesForInput(input: NgModel): any {
    return {
      'is-invalid': !input.valid && !input.pristine,
    };
  }

  public onSubmit(form: NgForm): void {
    if (form.valid) {
      this.recipe.ingredients = this.ingredients;

      let observable: Observable<RecipeCreateDto>;
      switch (this.mode) {
        case RecipeCreateEditMode.create:

          observable = this.service.createRecipe(this.recipeDetailToRecipeCreate(this.recipe));
          break;
        case RecipeCreateEditMode.edit:
          //observable = this.service.update(this.recipe);
          break;
        default:
          console.error('Unknown RecipeCreateEditMode', this.mode);
          return;
      }
      observable.subscribe({
        next: data => {
          this.notification.success(`Recipe ${this.recipe.name} successfully ${this.modeActionFinished}.`);
          this.router.navigate(['/recipe']);
        },
        error: error => {
          console.log(error);
          if (error && error.error && error.error.errors) {
            for (let i = 0; i < error.error.errors.length; i++) {
              this.notification.error(`${error.error.errors[i]}`);
            }
          } else if (error && error.error && error.error.message) { // if no detailed error explanation exists. Give a more general one if available.
            this.notification.error(`${error.error.message}`);
          } else if (error && error.error.detail) {
            this.notification.error(`${error.error.detail}`);
          } else {
            switch (this.mode) {
              case RecipeCreateEditMode.create:
                console.error('Error creating recipe', error);
                this.notification.error(`Creation of recipe did not work!`);
                break;
              case RecipeCreateEditMode.edit:
                console.error('Error editing recipe', error);
                this.notification.error(`Edit of recipe did not work!`);
                break;
              default:
                console.error('Unknown RecipeCreateEditMode. Operation did not work!', this.mode);
            }
          }
        }
      });
    }
  }

  public formatIngredients(item: ItemDetailDto | null): string {
    return !item
      ? ""
      : `${item.description}`
  }

  public addIngredient(item: ItemDetailDto | null) {
    if (!item)
      return;
    setTimeout(() => {
      const items = this.ingredients;
      if (items.some(m => m?.id === item.id)) {
        this.notification.error(`${item.description} is already in ingredient list`, "Duplicate Ingredient");
        this.dummyMemberSelectionModel = null;
        return;
      }
      items.push(item);
      this.dummyMemberSelectionModel = null;
    });
  }
  private recipeDetailToRecipeCreate(detailDto: RecipeDetailDto): RecipeCreateDto {
    const {id, ...rest } = detailDto;
    return rest;
  }

  memberSuggestions = (input: string): Observable<UserSelection[]> =>
    this.userService.searchFriends({name: input, limit: 5})
      .pipe(map(members => members.map(h => ({
        id: h.id,
        email: h.email,
      }))));

  public removeIngredient(index: number) {
    this.ingredients.splice(index, 1);
  }

  protected readonly recipeCreateEditMode = RecipeCreateEditMode;
}


