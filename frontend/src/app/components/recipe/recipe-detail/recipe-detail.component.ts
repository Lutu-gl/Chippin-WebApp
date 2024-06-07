import {Component, OnInit} from "@angular/core";
import {ActivatedRoute, Router} from "@angular/router";
import {
  RecipeCreateWithoutUserDto,
  RecipeDetailDto,
  RecipeDetailWithUserInfoDto,
  RecipeGlobalListDto
} from "../../../dtos/recipe";
import {RecipeService} from "../../../services/recipe.service";
import {ItemCreateDto, PantryItemDetailDto, Unit} from "../../../dtos/item";
import {clone} from "lodash";
import {ToastrService} from "ngx-toastr";
import {GroupDto} from "../../../dtos/group";
import {debounceTime, Observable, of} from "rxjs";
import {UserService} from "../../../services/user.service";
import {ShoppingListDetailDto} from "../../../dtos/shoppingList";
import {ConfirmationService, MessageService} from "primeng/api";
import {getStepSize, getSuffix} from "../../../util/unit-helper";

export enum RecipeDetailMode {
  owner,
  viewer
}

@Component({
  templateUrl: './recipe-detail.component.html',
  styleUrl: './recipe-detail.component.scss'
})
export class RecipeDetailComponent implements OnInit {
  mode: RecipeDetailMode = RecipeDetailMode.owner;
  recipe: RecipeDetailWithUserInfoDto = {
    name: '',
    ingredients: [],
    description: '',
    isPublic: false,
    portionSize:1,
    likes:0,
    dislikes:0,
    likedByUser:false,
    dislikedByUser:false
  };
  portion:number = 1;
  /*group: GroupDto = {
    id: 0,
    members: [],
    groupName: ''
  }
  shoppingList: ShoppingListDetailDto = {
    createdAt: undefined, updatedAt: undefined,
    id:0,
    categories: [],
    group: undefined,
    items: [],
    name: "",
    owner: undefined
  } */
  recipeId: number;
  error = false;
  errorMessage = '';
  groups: GroupDto[] = [];
  isPantryDialogVisible = false;
  isShoppingListDialogVisible = false;

  constructor(
    private route: ActivatedRoute,
    private service: RecipeService,
    private userService: UserService,
    private router: Router,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
  ) {
  }

  ngOnInit(): void {
    this.route.data.subscribe(data => {
      this.mode = data.mode;
    });
    const recipeIdparam = this.route.snapshot.paramMap.get('id');
    this.recipeId = recipeIdparam ? +recipeIdparam : Number(recipeIdparam);
    this.service.getRecipeWithInfoById(this.recipeId)
      .subscribe({
        next: data => {
          this.recipe = data;
        },
        error: error => {
          this.printError(error);
        }
      });

    this.userService.getUserGroups().subscribe({
      next: groups => {
        this.groups = groups;
      },
      error: error => {
        this.printError(error);
      }
    });
  }

  getRecipe() {
    this.service.getRecipeWithInfoById(this.recipeId)
      .subscribe({
        next: data => {
          this.recipe = data;
        },
        error: error => {
          this.printError(error);
        }
      });
  }

  printError(error): void {
    if (error && error.error && error.error.errors) {
      for (let i = 0; i < error.error.errors.length; i++) {
        this.messageService.add({severity: 'error', summary: 'Error', detail: `${error.error.errors[i]}`});
      }
    } else if (error && error.error && error.error.message) {
      this.messageService.add({severity: 'error', summary: 'Error', detail: `${error.error.message}`});
    } else if (error && error.error && error.error.detail) {
      this.messageService.add({severity: 'error', summary: 'Error', detail: `${error.error.detail}`});
    } else {
      console.error('Could not load pantry items', error);
      this.messageService.add({severity: 'error', summary: 'Error', detail: `Could not load Recipe!`});
    }
  }

  deleteRecipe() {
    this.service.deleteRecipe(this.recipe.id).subscribe({
      next: res => {
        console.log('deleted recipe: ', res);

      },
      error: err => {
        this.printError(err);
      }
    });
    this.messageService.add({
      severity: 'success',
      summary: 'Successful',
      detail: `Recipe successfully deleted`,
      life: 3000
    });
      this.router.navigate(['/home/recipes']);
  }


  public like() {
    this.service.likeRecipe(this.recipe.id)
      .pipe(debounceTime(1000))
      .subscribe({
        next: data => {

        },
        error: error => {
          this.messageService.add({severity: 'error', summary: 'Error', detail: `You are liking too fast. This like will not be saved`});
        }
      });
    if(this.recipe.dislikedByUser) {
      this.recipe.dislikes--;
    }
    this.recipe.dislikedByUser=false;
    this.recipe.likedByUser=true;
    this.recipe.likes++;
  }

  public updatePortion() {

  }
  public isOwner(): boolean {
  return this.mode === RecipeDetailMode.owner;
}

  public dislike() {
    this.service.dislikeRecipe(this.recipe.id)
      .pipe(debounceTime(1000))
      .subscribe({
        next: data => {

        },
        error: error => {
          this.messageService.add({severity: 'error', summary: 'Error', detail: `You are disliking too fast. This dislike will not be saved`});
        }
      });
    if(this.recipe.likedByUser) {
      this.recipe.likes--;
    }
    this.recipe.likedByUser=false;
    this.recipe.dislikedByUser=true;
    this.recipe.dislikes++;

  }



  /*public removeRecipeIngredientsFromPantry() {
    this.service.removeRecipeIngredientsFromPantry(this.group.id,this.recipe.id, this.portion).subscribe(
      {
        next: data => {
            this.notification.success("Ingredients successfully removed from Pantry");
        },
        error: error => {
          this.printError(error)
        }
      }
    );
  } */

  get formattedDescription(): string {
    return this.recipe.description.replace(/\n/g, '<br>');
  }

  public getScore(recipe: RecipeDetailWithUserInfoDto): number {
    return recipe.likes-recipe.dislikes;
  }

  public unitDisplayer(unit: Unit, amount:number): string {
    switch (unit) {
      case Unit.Gram:
        if(amount > 1000) {
          return amount/1000+ " kg";
        }
        return amount+ " g";
      case Unit.Milliliter:
        if(amount > 1000) {
          return amount/1000 + " l";
        }
        return amount +  " ml";
      case Unit.Piece:
        return amount +" pcs";
    }
  }

  openUsePantryDialog() {
    this.isPantryDialogVisible = true;
  }

  closeUsePantryDialog() {
    this.isPantryDialogVisible = false;
  }

  openUseShoppingListDialog() {

    this.isShoppingListDialogVisible = true;
  }

  closeUseShoppingListDialog() {
    this.isShoppingListDialogVisible = false;
  }






  protected readonly Unit = Unit;
  protected readonly clone = clone;

  protected readonly getSuffix = getSuffix;
  protected readonly getStepSize = getStepSize;
}
