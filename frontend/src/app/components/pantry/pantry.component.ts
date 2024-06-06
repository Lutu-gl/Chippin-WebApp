import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {PantryService} from "../../services/pantry.service";
import {
  DisplayedUnit,
  PantryItemCreateDisplayDto,
  pantryItemCreateDisplayDtoToPantryItemCreateDto,
  pantryItemCreateDisplayDtoToPantryItemDetailDto,
  PantryItemDetailDto,
  pantryItemDetailDtoToPantryItemCreateDisplayDto,
  PantryItemMergeDto,
} from "../../dtos/item";
import {KeyValuePipe, NgForOf, NgIf, NgSwitch, NgSwitchCase} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {debounceTime, Subject} from "rxjs";
import {GetRecipesDto, PantrySearch} from "../../dtos/pantry";
import {ConfirmDeleteDialogComponent} from "../confirm-delete-dialog/confirm-delete-dialog.component";
import {EditPantryItemDialogComponent} from "./edit-pantry-item-dialog/edit-pantry-item-dialog.component";
import {DisplayRecipesDialogComponent} from "./display-recipes-dialog/display-recipes-dialog.component";
import {RecipeListDto} from "../../dtos/recipe";
import {ShoppingListAddDialogComponent} from "./shopping-list-add-dialog/shopping-list-add-dialog.component";
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
  formatAmount,
  formatLowerLimit,
  getAmountForCreateEdit,
  getStepSize,
  getSuffix,
  getSuffixForCreateEdit
} from "../../util/unit-helper";
import {inRange} from "lodash";
import {TabMenuModule} from "primeng/tabmenu";

@Component({
  selector: 'app-pantry',
  standalone: true,
  imports: [
    NgForOf,
    KeyValuePipe,
    NgIf,
    FormsModule,
    ConfirmDeleteDialogComponent,
    NgSwitchCase,
    NgSwitch,
    EditPantryItemDialogComponent,
    DisplayRecipesDialogComponent,
    ShoppingListAddDialogComponent,
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
    TabMenuModule
  ],
  providers: [MessageService, ConfirmationService],
  templateUrl: './pantry.component.html',
  styleUrl: './pantry.component.scss'
})
export class PantryComponent implements OnInit {
  tabMenuItems: MenuItem[] | undefined;
  tabMenuActiveItem: MenuItem | undefined;

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
  recipes: RecipeListDto[];

  constructor(
    private route: ActivatedRoute,
    private service: PantryService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
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
  }

  hideDialog() {
    this.itemDialog = false;
    this.submitted = false;
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
    this.createEditItemReset = {...this.createEditItem};
    console.log(this.createEditItemReset);
    this.edit = true;
    this.itemToEditId = item.id;
    this.submitted = false;
    this.itemDialog = true;
  }

  saveNewItem() {
    this.submitted = true;

    if (this.createEditItem.description?.trim()
      && inRange(this.createEditItem.amount, 0, 1000001)
      && (!this.createEditItem.lowerLimit || inRange(this.createEditItem.lowerLimit, 0, 1000001))) {

      this.service.createItem(this.id, pantryItemCreateDisplayDtoToPantryItemCreateDto(this.createEditItem)).subscribe({
        next: dto => {
          console.log("Created new item: ", dto);
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

    if (this.createEditItem.description?.trim()
      && inRange(this.createEditItem.amount, 0, 1000001)
      && (!this.createEditItem.lowerLimit || inRange(this.createEditItem.lowerLimit, 0, 1000001))) {

      this.service.updateItem(pantryItemCreateDisplayDtoToPantryItemDetailDto(this.createEditItem, this.itemToEditId), this.id).subscribe({
        next: dto => {
          console.log("Updated item: ", dto);
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

    if (this.itemMergeEdit.description?.trim()
      && inRange(this.itemMergeEdit.amount, 0, 1000001)
      && (!this.itemMergeEdit.lowerLimit || inRange(this.itemMergeEdit.lowerLimit, 0, 1000001))) {

      let mergeDto: PantryItemMergeDto = {
        itemToDeleteId: this.itemMergeEdit.id,
        result: pantryItemCreateDisplayDtoToPantryItemDetailDto(this.itemMergeEdit, this.createEditItem.id)
      }

      mergeDto.result.id = this.createEditItem.id;

      this.service.mergeItems(mergeDto, this.id).subscribe({
        next: dto => {
          console.log("Updated item: ", dto);
          this.messageService.add({
            severity: 'success',
            summary: 'Successful',
            detail: `$Items merged`,
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
    this.itemMergeEdit = { ...this.itemMergeEdit };
    this.itemMergeEdit.amount += baseItem.unit === this.itemMergeEdit.unit ? baseItem.amount : 0;
    this.itemMergeEditReset = {...this.itemMergeEdit};
    console.log(this.itemMergeEditReset);
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
        //TODO: mass delete in backend + count does not work
        let count = 0;
        for (let item of this.selectedItems) {
          this.service.deleteItem(this.id, item.id).subscribe({
            next: res => {
              count++;
              this.getPantry(this.id);
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
        }
        this.messageService.add({
          severity: 'error',
          summary: 'Items Deleted',
          detail: `Deleted ${count} items`,
          life: 3000
        });
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
        console.log(result);
      },
      error: error => {
        console.error(error);
        this.messageService.add({severity: 'error', summary: 'Error', detail: `Could not decrement item amount!`});
      }
    })
  }

  increment(item: PantryItemDetailDto) {
    item.amount += getStepSize(item);
    if (item.amount > 1000000) {
      item.amount = 1000000;
    }
    this.service.updateItem(item, this.id).subscribe({
      next: result => {
        console.log(result);
      },
      error: error => {
        console.error(error);
        this.messageService.add({severity: 'error', summary: 'Error', detail: `Could not increment item amount!`});
      }
    })
  }

  getRecipes() {
    let getRecipesDto: GetRecipesDto = {
      itemIds: this.selectedItems.map(i => i.id)
    }
    console.log(getRecipesDto);
    this.service.getRecipes(this.id, getRecipesDto).subscribe({
      next: res => {
        console.log(res);
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
              severity: 'error',
              summary: 'Item Deleted',
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

  mergeItemsSelectOptions(item: PantryItemCreateDisplayDto): PantryItemDetailDto[] {
    return this.items.filter(i => i.id != item.id);
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
}
