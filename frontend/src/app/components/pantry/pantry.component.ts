import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {PantryService} from "../../services/pantry.service";
import {
  DisplayedUnit,
  ItemCreateDto,
  ItemDetailDto,
  PantryItemCreateDisplayDto,
  pantryItemCreateDisplayDtoToPantryItemCreateDto,
  pantryItemCreateDisplayDtoToPantryItemDetailDto,
  PantryItemDetailDto,
  pantryItemDetailDtoToPantryItemCreateDisplayDto,
  PantryItemMergeDto,
} from "../../dtos/item";
import {KeyValuePipe, NgClass, NgForOf, NgIf, NgStyle, NgSwitch, NgSwitchCase} from "@angular/common";
import {FormGroup, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {debounceTime, Subject} from "rxjs";
import {GetRecipesDto, PantrySearch} from "../../dtos/pantry";
import {RecipeByItemsDto} from "../../dtos/recipe";
import {ButtonModule} from "primeng/button";
import {TagModule} from "primeng/tag";
import {RatingModule} from "primeng/rating";
import {TableModule} from "primeng/table";
import {RippleModule} from "primeng/ripple";
import {ToolbarModule} from "primeng/toolbar";
import {ToastModule} from "primeng/toast";
import {ChipsModule} from "primeng/chips";
import {DialogModule} from "primeng/dialog";
import {DropdownModule} from "primeng/dropdown";
import {RadioButtonModule} from "primeng/radiobutton";
import {InputNumberModule} from "primeng/inputnumber";
import {ConfirmDialogModule} from "primeng/confirmdialog";
import {ConfirmationService, MenuItem, MessageService} from "primeng/api";
import {
  displayedUnitToUnit,
  formatAmount,
  formatLowerLimit,
  getAmountForCreateEdit,
  getStepSize,
  getSuffix,
  getSuffixForCreateEdit
} from "../../util/unit-helper";
import {inRange, valuesIn} from "lodash";
import {TabMenuModule} from "primeng/tabmenu";
import {AutoCompleteModule} from "primeng/autocomplete";
import {ShoppingListListDto} from "../../dtos/shoppingList";
import {ShoppingListService} from "../../services/shopping-list.service";
import {AuthService} from "../../services/auth.service";
import {BadgeModule} from "primeng/badge";

@Component({
  selector: 'app-pantry',
  standalone: true,
  imports: [
    NgForOf,
    KeyValuePipe,
    NgIf,
    FormsModule,
    NgSwitchCase,
    NgSwitch,
    ButtonModule,
    TagModule,
    RatingModule,
    TableModule,
    RippleModule,
    ToolbarModule,
    ToastModule,
    ChipsModule,
    DialogModule,
    DropdownModule,
    RadioButtonModule,
    InputNumberModule,
    ConfirmDialogModule,
    TabMenuModule,
    NgClass,
    AutoCompleteModule,
    ReactiveFormsModule,
    BadgeModule,
    NgStyle
  ],
  providers: [MessageService, ConfirmationService],
  templateUrl: './pantry.component.html',
  styleUrl: './pantry.component.scss'
})
export class PantryComponent implements OnInit {
  tabMenuItems: MenuItem[] | undefined;
  tabMenuActiveItem: MenuItem | undefined;

  recipeDialog: boolean = false;
  itemDialog: boolean = false;
  items!: PantryItemDetailDto[];
  createEditItem!: PantryItemCreateDisplayDto;
  createEditItemReset!: PantryItemCreateDisplayDto;
  itemMergeEdit!: PantryItemCreateDisplayDto;
  itemMergeEditReset!: PantryItemCreateDisplayDto;
  itemToEditId: number;
  submitted: boolean = false;
  edit: boolean = false;
  selectedItems!: PantryItemDetailDto[] | null;
  searchString: string = "";
  searchChangedObservable = new Subject<void>();
  id: number;
  recipes: RecipeByItemsDto[];
  itemToAdd: ItemCreateDto;
  addItemToShoppingListModalOpen: boolean;
  allShoppingLists: ShoppingListListDto[];
  selectedShoppingList: ShoppingListListDto;
  unitsForItems: any[];

  constructor(
    private route: ActivatedRoute,
    private service: PantryService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    private shoppingListService: ShoppingListService,
    private authService: AuthService,
  ) {
  }

  onActiveItemChange(event: MenuItem) {
    this.itemMergeEdit = {
      id: null,
      description: "",
      amount: 0,
      unit: DisplayedUnit.Piece,
      lowerLimit: null,
    };
    this.tabMenuActiveItem = event;
    this.itemMergeEditReset = {...this.itemMergeEdit};
  }

  getIngredientAmount(recipe: RecipeByItemsDto, ingredient: ItemDetailDto): string {
    let find: ItemDetailDto | undefined = recipe.itemsInPantry.find(i => i.description === ingredient.description && i.unit === ingredient.unit);
    if(find === undefined) {
      return 0 + "/" + formatAmount(ingredient);
    }
    if(find.unit === ingredient.unit) {
      return find.amount + "/" + formatAmount(ingredient);
    }
    return find.amount + "/" + ingredient.amount + ingredient.unit;
  }

  getIngredientMatch(recipe: RecipeByItemsDto, ingredient: ItemDetailDto): 'red' | 'orange' | 'green' {
    let find: ItemDetailDto | undefined = recipe.itemsInPantry.find(i => i.description === ingredient.description && i.unit === ingredient.unit);
    return find === undefined || find.amount === 0 ? "red" : find.amount >= ingredient.amount ? "green" : "orange";
  }

  isEditSelected(): boolean {
    return this.tabMenuActiveItem === this.tabMenuItems[0];
  }

  isMergeSelected(): boolean {
    return this.tabMenuActiveItem === this.tabMenuItems[1];
  }

  ngOnInit(): void {
    //tab menu in Edit/Merge dialog
    this.tabMenuItems = [
      {label: 'Edit'},
      {label: 'Merge'}
    ];
    this.tabMenuActiveItem = this.tabMenuItems[0];

    this.route.params.subscribe({
      next: params => {
        this.id = +params['id'];
        this.getPantry(this.id);
      },
      error: error => {
        if (error && error.error && error.error.errors) {
          for (let i = 0; i < error.error.errors.length; i++) {
            this.messageService.add({severity: 'error', summary: 'Error', detail: `${error.error.errors[i]}`});
          }
        } else if (error && error.error && error.error.message) {
          this.messageService.add({severity: 'error', summary: 'Error', detail: `${error.error.message}`});
        } else if (error && error.error && error.error.detail) {
          this.messageService.add({severity: 'error', summary: 'Error', detail: `${error.error.detail}`});
        } else {
          console.error('Could not get pantry id', error);
          this.messageService.add({severity: 'error', summary: 'Error', detail: `Could not load pantry!`});
        }
      }
    });
    this.searchChangedObservable
      .pipe(debounceTime(300))
      .subscribe({next: () => this.filterPantry()});

    // Retrieve shopping lists
    this.shoppingListService.getShoppingListsForGroup(this.id).subscribe({
      next: shoppingLists => {
        this.allShoppingLists = shoppingLists;
      },
      error: err => {
        this.messageService.add({severity: "error", summary: "Error", detail: "Could not load shopping lists"})
      }
    })

    this.unitsForItems = valuesIn(DisplayedUnit).map(unit => {
      return {label: unit, value: unit}
    });
  }

  hideDialog() {
    this.itemDialog = false;
    this.submitted = false;
    this.recipeDialog = false;
  }

  openRecipeDialog() {
    this.recipeDialog = true;
  }

  openNew() {
    this.tabMenuActiveItem = this.tabMenuItems[0]
    this.createEditItem = {
      id: null,
      description: "",
      amount: 0,
      unit: DisplayedUnit.Piece,
      lowerLimit: null,
    };
    this.edit = false;
    this.itemToEditId = null;
    this.submitted = false;
    this.itemDialog = true;
  }

  openEdit(item: PantryItemDetailDto) {
    this.tabMenuActiveItem = this.tabMenuItems[0]
    this.createEditItem = pantryItemDetailDtoToPantryItemCreateDisplayDto(item);
    this.itemMergeEdit = {
      id: null,
      description: "",
      amount: 0,
      unit: DisplayedUnit.Piece,
      lowerLimit: null,
    };
    this.createEditItemReset = {...this.createEditItem}
    this.edit = true;
    this.itemToEditId = item.id;
    this.submitted = false;
    this.itemDialog = true;
  }

  saveNewItem() {
    this.submitted = true;

    if (inRange(this.createEditItem.description?.length, 2, 61)
      && inRange(this.createEditItem.amount, 0, 1000001)
      && (!this.createEditItem.lowerLimit || inRange(this.createEditItem.lowerLimit, 0, 1000001))) {

      this.service.createItem(this.id, pantryItemCreateDisplayDtoToPantryItemCreateDto(this.createEditItem)).subscribe({
        next: dto => {
          this.messageService.add({
            severity: 'success',
            summary: 'Successful',
            detail: `${dto.description} created`,
            life: 3000
          });
          this.getPantry(this.id);
        }, error: error => {
          if (error && error.error && error.error.errors) {
            for (let i = 0; i < error.error.errors.length; i++) {
              this.messageService.add({severity: 'error', summary: 'Error', detail: `${error.error.errors[i]}`});
            }
          } else if (error && error.error && error.error.message) {
            this.messageService.add({severity: 'error', summary: 'Error', detail: `${error.error.message}`});
          } else if (error && error.error && error.error.detail) {
            this.messageService.add({severity: 'error', summary: 'Error', detail: `${error.error.detail}`});
          } else {
            console.error('Could not create item: ', error);
            this.messageService.add({severity: 'error', summary: 'Error', detail: `Could not create item!`});
          }
        }
      })
      this.itemDialog = false;
    }
  }

  editItem() {
    this.submitted = true;

    if (inRange(this.createEditItem.description?.length, 2, 61)
      && inRange(this.createEditItem.amount, 0, 1000001)
      && (!this.createEditItem.lowerLimit || inRange(this.createEditItem.lowerLimit, 0, 1000001))) {

      this.service.updateItem(pantryItemCreateDisplayDtoToPantryItemDetailDto(this.createEditItem, this.itemToEditId), this.id).subscribe({
        next: dto => {
          this.messageService.add({
            severity: 'success',
            summary: 'Successful',
            detail: `${dto.description} updated`,
            life: 3000
          });
          this.getPantry(this.id);
        },
        error: error => {
          if (error && error.error && error.error.errors) {
            for (let i = 0; i < error.error.errors.length; i++) {
              this.messageService.add({severity: 'error', summary: 'Error', detail: `${error.error.errors[i]}`});
            }
          } else if (error && error.error && error.error.message) {
            this.messageService.add({severity: 'error', summary: 'Error', detail: `${error.error.message}`});
          } else if (error && error.error && error.error.detail) {
            this.messageService.add({severity: 'error', summary: 'Error', detail: `${error.error.detail}`});
          } else {
            console.error('Could not update item: ', error);
            this.messageService.add({severity: 'error', summary: 'Error', detail: `Could not update item!`});
          }
        }
      })

      this.itemDialog = false;
    }
  }

  mergeItems() {
    this.submitted = true;

    if (inRange(this.createEditItem.description?.length, 2, 61)
      && inRange(this.itemMergeEdit.amount, 0, 1000001)
      && (!this.itemMergeEdit.lowerLimit || inRange(this.itemMergeEdit.lowerLimit, 0, 1000001))) {

      let mergeDto: PantryItemMergeDto = {
        itemToDeleteId: this.itemMergeEdit.id,
        result: pantryItemCreateDisplayDtoToPantryItemDetailDto(this.itemMergeEdit, this.createEditItem.id)
      }

      mergeDto.result.id = this.createEditItem.id;

      this.service.mergeItems(mergeDto, this.id).subscribe({
        next: dto => {
          this.messageService.add({
            severity: 'success',
            summary: 'Successful',
            detail: `Items merged`,
            life: 3000
          });
          this.getPantry(this.id);
        },
        error: error => {
          if (error && error.error && error.error.errors) {
            for (let i = 0; i < error.error.errors.length; i++) {
              this.messageService.add({severity: 'error', summary: 'Error', detail: `${error.error.errors[i]}`});
            }
          } else if (error && error.error && error.error.message) {
            this.messageService.add({severity: 'error', summary: 'Error', detail: `${error.error.message}`});
          } else if (error && error.error && error.error.detail) {
            this.messageService.add({severity: 'error', summary: 'Error', detail: `${error.error.detail}`});
          } else {
            console.error('Could not merge item: ', error);
            this.messageService.add({severity: 'error', summary: 'Error', detail: `Could not merge item!`});
          }
        }
      })

      this.itemDialog = false;
    }
  }

  setItemToMerge(baseItem: PantryItemCreateDisplayDto) {
    //Work on copy of item
    //Prevents changing items in item list
    this.itemMergeEdit = {...this.itemMergeEdit};
    this.itemMergeEdit.amount += baseItem.unit === this.itemMergeEdit.unit ? baseItem.amount : 0;
    this.itemMergeEditReset = {...this.itemMergeEdit};
  }

  resetEditItem() {
    this.createEditItem = {...this.createEditItemReset};
  }

  resetMergeItem() {
    this.itemMergeEdit = {...this.itemMergeEditReset};
  }

  getPantry(id: number) {
    this.service.getPantryById(id).subscribe({
      next: res => {
        this.items = res.items;
        this.items.forEach(item => {
          let belowMinimum = this.belowMinimum(item)
          if (belowMinimum !== null && belowMinimum === true) {
            this.setAmountOfItemInGroup(item);
          }
        })
      },
      error: error => {
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
          this.messageService.add({severity: 'error', summary: 'Error', detail: `Could not load pantry!`});
        }
      }
    });
  }

  deleteSelectedItems() {
    this.confirmationService.confirm({
      message: 'Are you sure you want to delete the selected items?',
      header: 'Confirm',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {

        let length: number = this.selectedItems.length;
        this.service.deleteItems(this.id, this.selectedItems.map(i => i.id)).subscribe({
          next: res => {
            this.getPantry(this.id);
            this.messageService.add({
              severity: 'success',
              summary: 'Success',
              detail: length > 1 ? `Deleted ${length} items` : 'Deleted 1 item',
              life: 3000
            });
          },
          error: err => {
            this.messageService.add({
              severity: 'error',
              summary: 'Error',
              detail: `Could not delete items`,
              life: 3000
            });
          }
        })

        this.selectedItems = null;
      }
    });
  }

  decrement(item: PantryItemDetailDto) {
    item.amount -= getStepSize(item);
    if (item.amount < 0) {
      item.amount = 0;
    }
    this.service.updateItem(item, this.id).subscribe({
      next: result => {
      },
      error: error => {
        console.error(error);
        this.messageService.add({severity: 'error', summary: 'Error', detail: `Could not decrement item amount!`});
      }
    })
    // Update amount in shopping lists
    this.setAmountOfItemInGroup(item);
  }

  increment(item: PantryItemDetailDto) {
    item.amount += getStepSize(item);
    if (item.amount > 1000000) {
      item.amount = 1000000;
    }
    this.service.updateItem(item, this.id).subscribe({
      next: result => {
      },
      error: error => {
        console.error(error);
        this.messageService.add({severity: 'error', summary: 'Error', detail: `Could not increment item amount!`});
      }
    })
    // Update amount in shopping lists
    this.setAmountOfItemInGroup(item);
  }

  getRecipes() {
    let getRecipesDto: GetRecipesDto = {
      itemIds: this.selectedItems.map(i => i.id)
    }
    this.service.getRecipes(this.id, getRecipesDto).subscribe({
      next: res => {
        this.recipes = res;
      }, error: err => {
        console.error(err);
        this.messageService.add({severity: 'error', summary: 'Error', detail: `Could not get recipes!`});
      }
    })
  }

  deleteItem(item: PantryItemDetailDto) {
    this.confirmationService.confirm({
      message: 'Are you sure you want to delete the selected items?',
      header: 'Confirm',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.service.deleteItem(this.id, item.id).subscribe({
          next: res => {
            this.getPantry(this.id);
            this.messageService.add({
              severity: 'success',
              summary: 'Success',
              detail: `Deleted ${item.description}`,
              life: 3000
            });
          },
          error: err => {
            console.error(err);
            this.messageService.add({
              severity: 'error',
              summary: 'Error',
              detail: `Could not delete ${item.description}`,
              life: 3000
            });
          }
        });
        this.selectedItems = null;
      }
    });
  }

  filterPantry() {
    let search: PantrySearch = {
      details: this.searchString
    };

    this.service.filterPantry(this.id, search).subscribe({
      next: res => {
        this.items = res.items;
      },
      error: err => {

      }
    });
  }

  belowMinimum(item: PantryItemDetailDto): boolean | null {
    if (item.lowerLimit === null || item.lowerLimit === 0) {
      return null;
    }
    return item.amount < item.lowerLimit;
  }

  getRowColor(belowMin: boolean | null): string {
    if (belowMin === null) {
      return '';
    }
    return belowMin ? 'bg-red-50' : '';
  }

  getTagSeverity(belowMin: boolean | null, item: ItemDetailDto): "success" | "secondary" | "danger" | "warning" {
    if (belowMin === null) {
      return "secondary";
    }
    if (!belowMin) return "success"


    if (!item.amountInShoppingLists || item.amountInShoppingLists < 1) {
      return "danger";
    }
    return "warning";

  }

  setAmountOfItemInGroup(item: ItemDetailDto) {
    this.shoppingListService.getAmountOfItemInGroup(this.id, item).subscribe({
      next: amount => {
        item.amountInShoppingLists = amount;
      }
      ,
      error: error => {
        console.error(error)
        item.amountInShoppingLists = null;
      }
    })
    item.amountInShoppingLists = null;

  }

  mergeItemsSelectOptions(item
                            :
                            PantryItemCreateDisplayDto
  ):
    PantryItemDetailDto[] {
    return this.items.filter(i => i.id != item.id);
  }

  displayMergeWarning(item: PantryItemCreateDisplayDto) {
    return this.items.find(i => i.unit === displayedUnitToUnit(item.unit) && i.description === item.description && i.id !== item?.id) !== undefined;
  }

  protected readonly getStepSize = getStepSize;
  protected readonly DisplayedUnit = DisplayedUnit;
  protected readonly Object = Object;
  protected readonly getQuantity = formatAmount;
  protected readonly getSuffix = getSuffix;
  protected readonly getSuffixForCreateEdit = getSuffixForCreateEdit;
  protected readonly getAmountForCreateEdit = getAmountForCreateEdit;
  protected readonly formatLowerLimit = formatLowerLimit;
  protected readonly formatAmount = formatAmount
  addItemForm: FormGroup;

  addToShoppingList(item: PantryItemDetailDto) {
    this.selectedShoppingList = null;
    this.itemToAdd = {
      amount: item.lowerLimit - item.amount,
      unit: item.unit,
      description: item.description
    }

    this.addItemToShoppingListModalOpen = true;
  }

  addItem() {
    this.shoppingListService.addShoppingListItemToShoppingList(this.authService.getUserId(), this.selectedShoppingList.id, this.itemToAdd).subscribe({
      next: () => {
        this.addItemToShoppingListModalOpen = false;
        this.messageService.add({severity: "success", summary: "Success", detail: "Added item to shopping list"})
        this.getPantry(this.id);
      },
      error: error => {
        if (error && error.error && error.error.errors) {
          for (let i = 0; i < error.error.errors.length; i++) {
            this.messageService.add({severity: 'error', summary: 'Error', detail: `${error.error.errors[i]}`});
          }
        } else if (error && error.error && error.error.message) {
          this.messageService.add({severity: 'error', summary: 'Error', detail: `${error.error.message}`});
        } else if (error && error.error && error.error.detail) {
          this.messageService.add({severity: 'error', summary: 'Error', detail: `${error.error.detail}`});
        } else {
          console.error('Could not add items to shopping list', error);
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: `Could not add item to shopping list!`
          });
        }
      }
    })
  }
}
