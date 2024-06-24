import {Component, OnInit} from "@angular/core";
import {ActivatedRoute, Router} from "@angular/router";
import {RecipeDetailWithUserInfoDto} from "../../../dtos/recipe";
import {RecipeService} from "../../../services/recipe.service";
import {DisplayedUnit, ItemCreateDto, ItemDetailDto, PantryItemDetailDto, Unit} from "../../../dtos/item";
import {clone} from "lodash";
import {GroupDto} from "../../../dtos/group";
import {debounceTime} from "rxjs";
import {UserService} from "../../../services/user.service";
import {ShoppingListListDto} from "../../../dtos/shoppingList";
import {ConfirmationService, MessageService} from "primeng/api";
import {
  formatAmount,
  getSuffixForCreateEdit
} from "../../../util/unit-helper";
import {ShoppingListService} from "../../../services/shopping-list.service";
import {AuthService} from "../../../services/auth.service";
import {AddItemToShoppingListDto, RemoveRecipeIngredientsFromPantryDto} from "../../../dtos/RecipePantryShoppingList";
import {saveAs} from "file-saver";
import {PantryService} from "../../../services/pantry.service";

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
  portion: number;
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


  removeIngredientsDto: RemoveRecipeIngredientsFromPantryDto;
  removeIngredientsDtoReset: RemoveRecipeIngredientsFromPantryDto;
  selectedPantryIngredients: ItemDetailDto[];


  recipeId: number;
  error = false;
  groups: GroupDto[] = [];
  isPantryDialogVisible = false;
  isShoppingListDialogVisible = false;


  constructor(
    private route: ActivatedRoute,
    private service: RecipeService,
    private pantryService: PantryService,
    private userService: UserService,
    private router: Router,
    private messageService: MessageService,
    private shoppingListService: ShoppingListService,
    private authService: AuthService,
    private confirmationService: ConfirmationService
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
          this.portion = this.recipe.portionSize;
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
      }, error: error => {
        this.printError(error);
      }
    })
  }

  onSelectShoppingList() {

    if (!this.shoppingList.groupId) {
      this.service.selectIngredientsForShoppingList(this.recipeId, this.shoppingList.id).subscribe({
        next: dto => {
          this.addItemToShoppingListDto = dto;
          this.addItemToShoppingListDtoReset = JSON.parse(JSON.stringify(this.addItemToShoppingListDto));
          this.selectedIngredients=this.addItemToShoppingListDtoReset.recipeItems;
          this.shoppingList = {...this.shoppingList};
        }, error: error => {
          this.printError(error);
        }
      })
    } else {
      this.service.selectIngredientsForShoppingListWithPantry(this.recipeId, this.shoppingList.id, this.shoppingList.groupId).subscribe({
        next: dto => {
          this.addItemToShoppingListDto = dto;
          this.addItemToShoppingListDtoReset = JSON.parse(JSON.stringify(this.addItemToShoppingListDto));
          this.shoppingList = {...this.shoppingList};
        }, error: error => {
          this.printError(error);
        }
      })
    }
  }

  onSelectPantry() {
    if (!this.group || this.group.id === null) {
      return;
    }
    this.service.removeRecipeIngredientsFromPantry(this.recipeId, this.group.id, this.portion).subscribe({
      next: dto => {
        this.removeIngredientsDto = dto;
        this.removeIngredientsDtoReset = JSON.parse(JSON.stringify(this.removeIngredientsDto));
        this.selectedPantryIngredients = this.removeIngredientsDtoReset.recipeItems;
        this.group = {...this.group};
      }, error: error => {
        this.printError(error);
      }
    })
  }

  findMatchingShoppingListItem(item: ItemDetailDto): any {
    let dto = this.addItemToShoppingListDto.shoppingListItems.find(i => i.item.description === item.description && i.item.unit === item.unit);
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
    let dto: PantryItemDetailDto = this.addItemToShoppingListDto.pantryItems.find(i => i.description === item.description && i.unit === item.unit);
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

  findMatchingPantryItemsForRemove(item: ItemDetailDto): PantryItemDetailDto {
    let dto: PantryItemDetailDto = this.removeIngredientsDto.pantryItems.find(i => i.description === item.description && i.unit === item.unit);
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
    if (!this.addItemToShoppingListDto.pantryItems) {

      return '';
    } else {
      let pantryMatching = this.findMatchingPantryItems(item);
      let shoppingListMatching = this.findMatchingShoppingListItem(item);

      if (pantryMatching.amount + shoppingListMatching.amount < item.amount) {
        return 'bg-red-50';
      } else if (pantryMatching.amount >= item.amount) {
        return 'bg-green-50'
      } else if (pantryMatching.amount + shoppingListMatching.amount >= item.amount) {
        return 'bg-orange-50'
      }
    }
  }

  getPantryColor(item: ItemDetailDto) {
    let pantryMatching = this.findMatchingPantryItemsForRemove(item);

    if (pantryMatching.amount - item.amount < 0) {
      return 'bg-red-50';
    } else {
      return '';
    }
  }

  reset() {
    if (this.addItemToShoppingListDtoReset) {
      this.addItemToShoppingListDto = JSON.parse(JSON.stringify(this.addItemToShoppingListDtoReset));

    }
    if (this.removeIngredientsDtoReset) {
      this.removeIngredientsDto = JSON.parse(JSON.stringify(this.removeIngredientsDtoReset));
      this.portion = this.recipe.portionSize;
    }
  }

  addSelectedIngredientsToShoppingList() {
    let list: ItemCreateDto[] = [];
    for (let ingredient of this.selectedIngredients) {
      let selected: ItemDetailDto = this.addItemToShoppingListDto.recipeItems
        .find(i => i.id === ingredient.id);

      let dto: ItemCreateDto = {
        description: selected.description,
        amount: selected.amount,
        unit: selected.unit
      };
      list.push(dto);
    }
    let listCopy: ShoppingListListDto = {...this.shoppingList};
    this.shoppingListService.addShoppingListItemsToShoppingList(this.authService.getUserId(), this.shoppingList.id, list).subscribe({
      next: res => {
        this.messageService.add({
          severity: 'success',
          summary: 'Successful',
          detail: `${list.length} items added to ${listCopy.name}`,
          life: 3000
        });
        this.router.navigate(['shopping-list/', listCopy.id]);
      }, error: err => {
        this.printError(err);
      }
    });

  }

  onRemovePantrySubmit() {
    let result: PantryItemDetailDto[] = [];

    for (let i = 0; i < this.selectedPantryIngredients.length; i++) {
      let dto: PantryItemDetailDto = this.removeIngredientsDto.pantryItems.find(p => p.description === this.selectedPantryIngredients[i].description
        && p.unit === this.selectedPantryIngredients[i].unit);

      let ingredients = this.removeIngredientsDto.recipeItems.find(item => item.id === this.selectedPantryIngredients[i].id);

      if (dto) {
        let copy: PantryItemDetailDto = {
          id: dto.id,
          description: dto.description,
          unit: dto.unit,
          lowerLimit: dto.lowerLimit,
          amount: (dto.amount - ingredients.amount < 0 ? 0 : dto.amount - ingredients.amount)
        }
        result.push(copy);
      }
    }
    let groupId: number = this.group.id;
    this.pantryService.updateItems(result, this.group.id).subscribe(
      {
        next: data => {
          this.messageService.add({
            severity: 'success',
            summary: 'Success',
            detail: `Recipe Ingredients have been successfully removed from your Pantry`
          });
          this.router.navigate(['group/', groupId]);
        }, error: error => {
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
    this.confirmationService.confirm({
      message: 'Are you sure you want to delete ' + this.recipe.name + '?',
      header: 'Confirm',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.service.deleteRecipe(this.recipe.id).subscribe({
          next: res => {


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
    });


  }


  public like() {
    this.service.likeRecipe(this.recipe.id)
      .pipe(debounceTime(1000))
      .subscribe({
        next: data => {

        },
        error: error => {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: `You are liking too fast. This like will not be saved`
          });
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
    for (let i = 0; i < this.removeIngredientsDto.recipeItems.length; i++) {
      this.removeIngredientsDto.recipeItems[i].amount = this.recipe.ingredients[i].amount * (this.portion / this.recipe.portionSize);
    }
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
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: `You are disliking too fast. This dislike will not be saved`
          });
        }
      });
    if (this.recipe.likedByUser) {
      this.recipe.likes--;
    }
    this.recipe.likedByUser = false;
    this.recipe.dislikedByUser = true;
    this.recipe.dislikes++;

  }

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
          return (amount / 1000).toLocaleString('de-DE') + " kg";
        }
        return amount + " g";
      case Unit.Milliliter:
        if (amount > 1000) {
          return (amount / 1000).toLocaleString('de-DE') + " l";
        }
        return amount.toLocaleString('de-DE') + " ml";
      case Unit.Piece:
        return amount.toLocaleString('de-DE') + " pcs";
    }
  }

  openUsePantryDialog() {
    this.isPantryDialogVisible = true;
  }

  closeUsePantryDialog() {
    this.reset();
    this.group = null;
    this.selectedPantryIngredients = [];
    this.removeIngredientsDtoReset = null;
    this.removeIngredientsDto = null;
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

  getPdf() {
    let data = this.service.exportRecipe(this.recipeId).subscribe(
      {
        next: data => {
          const blob = new Blob([data], {type: 'application/pdf'});


          saveAs(blob, this.recipe.name + '.pdf');
        },
        error: error => {
          this.printError(error);
        }


      }
    );

  }

  protected readonly Unit = Unit;
  protected readonly clone = clone;

  protected readonly DisplayedUnit = DisplayedUnit;
  protected readonly getSuffixForCreateEdit = getSuffixForCreateEdit;
  protected readonly formatAmount = formatAmount;
  protected readonly Object = Object;
}
