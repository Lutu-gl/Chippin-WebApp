import {Component, OnInit} from "@angular/core";
import {ActivatedRoute, Router} from "@angular/router";
import {RecipeDetailWithUserInfoDto} from "../../../dtos/recipe";
import {RecipeService} from "../../../services/recipe.service";
import {DisplayedUnit, ItemCreateDto, ItemDetailDto, PantryItemDetailDto, Unit} from "../../../dtos/item";
import {clone} from "lodash";
import {ToastrService} from "ngx-toastr";
import {GroupDto} from "../../../dtos/group";
import {UserService} from "../../../services/user.service";
import {ShoppingListListDto} from "../../../dtos/shoppingList";
import {ConfirmationService, MessageService} from "primeng/api";
import {
  formatAmount,
  formatLowerLimit,
  getAmountForCreateEdit,
  getStepSize,
  getSuffix,
  getSuffixForCreateEdit
} from "../../../util/unit-helper";
import {ShoppingListService} from "../../../services/shopping-list.service";
import {AuthService} from "../../../services/auth.service";
import {AddItemToShoppingListDto} from "../../../dtos/AddRecipeItemToShoppingListDto";
import * as _ from "lodash";

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
  recipe!: RecipeDetailWithUserInfoDto;
  portion: number = 1;
  group: GroupDto = {
    id: 0,
    members: [],
    groupName: ''
  }
  shoppingLists: ShoppingListListDto[];
  shoppingListsGrouped: { groupName: string, shoppingLists: ShoppingListListDto[] }[] = [];
  shoppingList!: ShoppingListListDto;
  addItemToShoppingListDto!: AddItemToShoppingListDto;
  addItemToShoppingListDtoReset!: AddItemToShoppingListDto;
  selectedIngredients: ItemDetailDto[];
  recipeId: number;
  error = false;
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
    private notification: ToastrService,
    private shoppingListService: ShoppingListService,
    private authService: AuthService
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
    this.shoppingListService.getShoppingListsForUser(this.authService.getUserId()).subscribe({
      next: dto => {
        this.shoppingLists = dto;
        this.groupShoppingLists();
        console.log("Grouped ", this.shoppingListsGrouped)
      }, error: error => {
        this.printError(error);
      }
    })
  }

  onSelectShoppingList(): AddItemToShoppingListDto {
    console.log(this.shoppingList)
    if (!this.shoppingList || this.shoppingList.groupId === null) {
      console.log("null");
      return;
    }
    this.service.selectIngredientsForShoppingListWithPantry(this.recipeId, this.shoppingList.id, this.shoppingList.groupId).subscribe({
      next: dto => {
        this.addItemToShoppingListDto = dto;
        this.addItemToShoppingListDtoReset = JSON.parse(JSON.stringify(this.addItemToShoppingListDto));
        this.shoppingList = {...this.shoppingList};
        console.log(dto);
      }, error: error => {
        this.printError(error);
      }
    })
    return null;
  }

  findMatchingShoppingListItem(item: ItemDetailDto): any {
    let dto = this.addItemToShoppingListDto.shoppingListItems.find(i => i.item.description === item.description);
    if (!dto) {
      return {
        amount: 0,
        id: null,
        description: "",
        unit: item.unit
      };
    }
    return dto.item;
  }

  findMatchingPantryItems(item: ItemDetailDto): PantryItemDetailDto {
    let dto: PantryItemDetailDto = this.addItemToShoppingListDto.pantryItems.find(i => i.description === item.description);
    if (!dto) {
      return {
        amount: 0,
        id: null,
        description: "",
        lowerLimit: null,
        unit: item.unit
      };
    }
    return dto;
  }

  getColor(item: ItemDetailDto) {
    let pantryMatching = this.findMatchingPantryItems(item);
    let shoppingListMatching = this.findMatchingShoppingListItem(item);

    if (pantryMatching.amount + shoppingListMatching.amount < item.amount) {
      return 'bg-red-600';
    } else if (pantryMatching.amount >= item.amount) {
      return 'bg-green-600'
    } else if (pantryMatching.amount + shoppingListMatching.amount >= item.amount) {
      return 'bg-orange-600'
    }
  }

  reset() {
    this.addItemToShoppingListDto = JSON.parse(JSON.stringify(this.addItemToShoppingListDtoReset));
    console.log(this.addItemToShoppingListDto)
  }

  addSelectedIngredientsToShoppingList() {
    //TODO wait for endpoint
    for (let ingredient of this.selectedIngredients) {
      let dto: ItemCreateDto = {
        description: ingredient.description,
        amount: ingredient.amount,
        unit: ingredient.unit
      };
      this.shoppingListService.addShoppingListItemToShoppingList(this.authService.getUserId(), this.shoppingList.id, dto).subscribe({
        next: res => {
          console.log(res);
        }, error: err => {
          console.log(err)
          this.printError(err);
        }
      });
    }
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
      console.error('Could not load recipe items', error);
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
    this.notification.success("Recipe successfully deleted");
    this.router.navigate(['/recipe']);
  }


  public like() {
    this.service.likeRecipe(this.recipe.id)
      .subscribe({
        next: data => {

        },
        error: error => {
          this.printError(error);
        }
      });
    if (this.recipe.dislikedByUser) {
      this.recipe.dislikes--;
    }
    this.recipe.dislikedByUser = false;
    this.recipe.likedByUser = true;
    this.recipe.likes++;
  }

  public updatePortion() {

  }

  public isOwner(): boolean {
    return this.mode === RecipeDetailMode.owner;
  }

  public dislike() {
    this.service.dislikeRecipe(this.recipe.id)
      .subscribe({
        next: data => {

        },
        error: error => {
          this.printError(error);
        }
      });
    if (this.recipe.likedByUser) {
      this.recipe.likes--;
    }
    this.recipe.likedByUser = false;
    this.recipe.dislikedByUser = true;
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
    return recipe.likes - recipe.dislikes;
  }

  public unitDisplayer(unit: Unit, amount: number): string {
    switch (unit) {
      case Unit.Gram:
        if (amount > 1000) {
          return amount / 1000 + " kg";
        }
        return amount + " g";
      case Unit.Milliliter:
        if (amount > 1000) {
          return amount / 1000 + " l";
        }
        return amount + " ml";
      case Unit.Piece:
        return amount + " pcs";
    }
  }

  openUsePantryDialog() {
    this.isPantryDialogVisible = true;
  }

  closeUsePantryDialog() {
    this.reset();
    this.isPantryDialogVisible = false;
  }

  openUseShoppingListDialog() {
    this.isShoppingListDialogVisible = true;
  }

  closeUseShoppingListDialog() {
    this.reset();
    this.shoppingList = null;
    this.addItemToShoppingListDto = null;
    this.addItemToShoppingListDtoReset = null;
    this.selectedIngredients = [];
    this.isShoppingListDialogVisible = false;
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


  protected readonly Unit = Unit;
  protected readonly clone = clone;

  protected readonly DisplayedUnit = DisplayedUnit;
  protected readonly getSuffixForCreateEdit = getSuffixForCreateEdit;
  protected readonly getAmountForCreateEdit = getAmountForCreateEdit;
  protected readonly formatAmount = formatAmount;
  protected readonly Object = Object;
  protected readonly formatLowerLimit = formatLowerLimit;
  protected readonly getSuffix = getSuffix;
  protected readonly getStepSize = getStepSize;
}
