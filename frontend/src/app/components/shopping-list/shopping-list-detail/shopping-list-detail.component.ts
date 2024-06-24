import {Component, OnInit} from '@angular/core';
import {ShoppingListDetailDto, ShoppingListItemDto, ShoppingListItemUpdateDto} from "../../../dtos/shoppingList";
import {ShoppingListService} from "../../../services/shopping-list.service";
import {ActivatedRoute, Router, RouterLink} from "@angular/router";
import {NgClass, NgForOf, NgIf} from "@angular/common";
import {AuthService} from "../../../services/auth.service";
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {
  AddShoppingListItemModalComponent
} from "../add-shopping-list-item-modal/add-shopping-list-item-modal.component";
import {
  EditShoppingListItemModalComponent
} from "../edit-shopping-list-item-modal/edit-shopping-list-item-modal.component";
import {ButtonModule} from "primeng/button";
import {DividerModule} from "primeng/divider";
import {TagModule} from "primeng/tag";
import {ScrollPanelModule} from "primeng/scrollpanel";
import {CardModule} from "primeng/card";
import {ButtonGroupModule} from "primeng/buttongroup";
import {ConfirmDialogModule} from "primeng/confirmdialog";
import {ConfirmationService, MenuItem, MessageService} from "primeng/api";
import {CheckboxModule} from "primeng/checkbox";
import {DockModule} from "primeng/dock";
import {BadgeModule} from "primeng/badge";
import {DragDropModule} from "primeng/dragdrop";
import {MenuModule} from "primeng/menu";
import {ProgressSpinnerModule} from "primeng/progressspinner";
import {DialogModule} from "primeng/dialog";
import {ChipsModule} from "primeng/chips";
import {DropdownModule} from "primeng/dropdown";
import {DisplayedUnit, ItemCreateDto, ItemDetailDto} from "../../../dtos/item";
import {valuesIn} from "lodash";
import {InputGroupModule} from "primeng/inputgroup";
import {PaginatorModule} from "primeng/paginator";
import {InputGroupAddonModule} from "primeng/inputgroupaddon";
import {convertQuantity, formatAmount} from "../../../util/unit-helper";
import {ShoppingListEditModalComponent} from "../shopping-list-edit-modal/shopping-list-edit-modal.component";
import {AutoCompleteCompleteEvent, AutoCompleteModule, AutoCompleteSelectEvent} from "primeng/autocomplete";
import {PantryService} from "../../../services/pantry.service";
import {TabViewModule} from "primeng/tabview";

@Component({
  selector: 'app-shopping-list-detail',
  standalone: true,
  imports: [
    RouterLink,
    NgForOf,
    NgIf,
    FormsModule,
    NgClass,
    AddShoppingListItemModalComponent,
    EditShoppingListItemModalComponent,
    ButtonModule,
    DividerModule,
    TagModule,
    ScrollPanelModule,
    CardModule,
    ButtonGroupModule,
    ConfirmDialogModule,
    CheckboxModule,
    DockModule,
    BadgeModule,
    DragDropModule,
    MenuModule,
    ProgressSpinnerModule,
    DialogModule,
    ChipsModule,
    DropdownModule,
    InputGroupModule,
    PaginatorModule,
    InputGroupAddonModule,
    ReactiveFormsModule,
    ShoppingListEditModalComponent,
    AutoCompleteModule,
    TabViewModule,
  ],
  templateUrl: './shopping-list-detail.component.html',
  styleUrl: './shopping-list-detail.component.scss'
})
export class ShoppingListDetailComponent implements OnInit {

  constructor(private shoppingListService: ShoppingListService,
              private route: ActivatedRoute,
              private router: Router,
              private authService: AuthService,
              private confirmationService: ConfirmationService,
              private pantryService: PantryService,
              private messageService: MessageService) {
    this.addItemForm = new FormGroup({
      name: new FormControl('', [Validators.required, Validators.minLength(2), Validators.maxLength(255)]),
      amount: new FormControl('', [Validators.required, Validators.min(1), Validators.max(100000)]),
      unit: new FormControl('', Validators.required)
    })
    this.editItemForm = new FormGroup({
      name: new FormControl('', [Validators.required, Validators.minLength(2), Validators.maxLength(255)]),
      amount: new FormControl('', [Validators.required, Validators.min(1), Validators.max(100000)]),
      unit: new FormControl('', Validators.required)
    })
  }

  shoppingListDetailDto: ShoppingListDetailDto;
  groupId: number;
  userEmail: string;
  userId: number;
  shoppingListId: number;
  selectedItem: ShoppingListItemDto;
  draggedItem: ShoppingListItemDto;
  shoppingListItemMenuItems: MenuItem[] | undefined;
  shoppingCartItemMenuItems: MenuItem[] | undefined;
  displayAddItemDialog: boolean = false;
  displayEditItemDialog: boolean = false;
  units: any[]
  addItemForm: FormGroup = new FormGroup({});
  editItemForm: FormGroup = new FormGroup({});
  itemToEdit: ShoppingListItemDto | undefined;
  showEditModal: boolean = false;
  allMissingItems: ItemDetailDto[] = [];

  ngOnInit(): void {
    this.route.params.subscribe({
      next: params => {
        this.groupId = +params['id'];
        this.shoppingListId = +params['shoppingListId'];
      },
      error: err => {
        this.messageService.add({severity: 'error', summary: 'Error', detail: 'Error loading shopping list'});
        this.router.navigate(['/group', this.groupId]);
        console.error(err);
      }
    });

    this.userEmail = this.authService.getEmail();
    this.userId = this.authService.getUserId();

    // this.units has to be an array the values of the enum DisplayedUnit
    //q: How do i do that?
    this.units = valuesIn(DisplayedUnit).map(unit => {
      return {label: unit, value: unit}
    });

    this.shoppingListItemMenuItems = [
      {
        label: "Edit",
        icon: "pi pi-pencil",
        command: () => {
          // Open edit modal
          this.openEditItemDialog(this.selectedItem);
        }
      },
      {
        label: "Delete",
        icon: "pi pi-trash",
        command: () => {
          // Delete item
          this.confirmationService.confirm({
            header: "Delete Item",
            message: "Are you sure you want to delete this item?",
            acceptLabel: "Delete",
            acceptIcon: "pi pi-trash",
            acceptButtonStyleClass: "p-button-danger p-button-text",
            rejectLabel: "Cancel",
            rejectIcon: "pi pi-times",
            rejectButtonStyleClass: "p-button-secondary p-button-text",
            accept: () => {
              this.deleteItem(this.selectedItem.id);
              this.messageService.add({severity: 'success', summary: 'Success', detail: 'Item deleted'});
            },
            reject: () => {
              this.messageService.add({severity: 'info', summary: 'Canceled', detail: 'Delete canceled'})
            }
          })
        }
      }
    ]


    this.loadShoppingListDetailDto();
    this.loadAllMissingItems();
  }

  loadAllMissingItems() {
    if (!this.shoppingListDetailDto?.group) return;
    this.pantryService.getAllMissingItems(this.shoppingListDetailDto.group.id).subscribe({
      next: data => {
        this.allMissingItems = data;
      },
      error: err => {
        console.error(err);
        this.messageService.add({severity: 'error', summary: 'Error', detail: 'Could not load missing items'});
      }
    })
  }

  setShoppingCartMenuItems() {
    this.shoppingCartItemMenuItems = [
      {
        label: "Move to pantry",
        icon: "pi pi-check",
        command: () => {
          // Move item to pantry
          this.confirmationService.confirm({
            header: `Move "${this.selectedItem.item.description}" to pantry?`,
            message: `Are you sure you want to move "${this.selectedItem.item.description}" to the pantry?`,
            acceptLabel: "Move",
            acceptButtonStyleClass: "p-button-primary p-button-text",
            rejectLabel: "Cancel",
            rejectButtonStyleClass: "p-button-secondary p-button-text",
            accept: () => {
              this.addItemToPantry(this.selectedItem.id);
              this.messageService.add({severity: 'success', summary: 'Success', detail: 'Item moved to pantry'});
            },
            reject: () => {
              this.messageService.add({severity: 'info', summary: 'Canceled', detail: 'Move canceled'})
            }
          })
        }
      },
      {
        label: "Edit",
        icon: "pi pi-pencil",
        command: () => {
          // Open edit modal
          this.openEditItemDialog(this.selectedItem);
        }
      },
      {
        label: "Delete",
        icon: "pi pi-trash",
        command: () => {
          // Delete item
          this.confirmationService.confirm({
            header: "Delete Item",
            message: "Are you sure you want to delete this item?",
            acceptLabel: "Delete",
            acceptIcon: "pi pi-trash",
            acceptButtonStyleClass: "p-button-danger p-button-text",
            rejectLabel: "Cancel",
            rejectIcon: "pi pi-times",
            rejectButtonStyleClass: "p-button-secondary p-button-text",
            accept: () => {
              this.deleteItem(this.selectedItem.id);
              this.messageService.add({severity: 'success', summary: 'Success', detail: 'Item deleted'});
            },
            reject: () => {
              this.messageService.add({severity: 'info', summary: 'Canceled', detail: 'Delete canceled'})
            }
          })
        }
      },
      {
        label: "Move back to shopping list",
        icon: "pi pi-undo",
        command: () => {
          // Move item back to shopping list
          this.toggleChecked(this.selectedItem.id);
        }
      }
    ]

    if (!this.shoppingListDetailDto?.group) {
      // Remove "move to pantry" button
      this.shoppingCartItemMenuItems = this.shoppingCartItemMenuItems.filter(item => item.label !== "Move to pantry");
    }

  }

  loadShoppingListDetailDto(): void {
    this.shoppingListService.getShoppingListById(this.shoppingListId).subscribe({
      next: shoppingList => {
        this.shoppingListDetailDto = shoppingList;
        this.setShoppingCartMenuItems();
        this.loadAllMissingItems();
      },
      error: err => {
        console.error(err);
      }
    });
  }

  getShoppingListItems(): ShoppingListItemDto[] {
    return this.shoppingListDetailDto.items.filter(item => !item.checkedById)
      .sort((a, b) => a.updatedAt > b.updatedAt ? -1 : 1);
  }

  getShoppingCartItems(): ShoppingListItemDto[] {
    return this.shoppingListDetailDto.items.filter(item => item.checkedById)
      .sort((a, b) => a.updatedAt > b.updatedAt ? -1 : 1);
  }

  deleteShoppingList() {
    this.shoppingListService.deleteShoppingList(this.shoppingListId).subscribe({
      next: () => {
        this.messageService.add({severity: 'success', summary: 'Success', detail: 'Shopping list deleted'});
        this.router.navigate(['/']);
      },
      error: error => {
        console.error(error);
        if (error && error.error && error.error.errors) {
          for (let i = 0; i < error.error.errors.length; i++) {
            this.messageService.add({severity: 'error', summary: 'Error', detail: `${error.error.errors[i]}`});
          }
        } else if (error && error.error && error.error.message) {
          this.messageService.add({severity: 'error', summary: 'Error', detail: `${error.error.message}`});
        } else if (error && error.error && error.error.detail) {
          this.messageService.add({severity: 'error', summary: 'Error', detail: `${error.error.detail}`});
        } else {
          console.error('Could not delete shopping list', error);
          this.messageService.add({severity: 'error', summary: 'Error', detail: `Could not delete shopping list!`});
        }
      }
    });
  }

  selectItem(item: ShoppingListItemDto) {
    console.log(item)
    this.selectedItem = item;
  }

  deleteItem(itemId: number) {
    this.shoppingListService.deleteShoppingListItem(this.authService.getUserId(), this.shoppingListId, itemId).subscribe({
      next: () => {
        this.loadShoppingListDetailDto();
      },
      error: err => {
        console.error(err);
      }
    })
  }

  addItemToPantry(itemId: number) {
    this.shoppingListService.moveShoppingListItemToPantry(this.authService.getUserId(), this.shoppingListId, itemId).subscribe({
      next: () => {
        this.loadShoppingListDetailDto();
        this.messageService.add({severity: 'success', summary: 'Success', detail: 'Item moved to pantry'});
      },
      error: err => {
        console.error(err);
      }
    })
  }

  toggleChecked(itemId: number) {
    let shoppingListItem = this.shoppingListDetailDto.items.find(item => item.id === itemId);
    let shoppingListItemUpdateDto: ShoppingListItemUpdateDto = {
      id: shoppingListItem.id,
      item: shoppingListItem.item,
      checked: !shoppingListItem.checkedById
    }

    this.shoppingListService.updateShoppingListItem(this.authService.getUserId(), this.shoppingListId, shoppingListItemUpdateDto).subscribe({
      next: () => {
        this.loadShoppingListDetailDto();
      },
      error: err => {
        console.error(err);
      }
    })
  }

  confirmMoveAllItemsInCartToPantry() {
    if (this.getShoppingCartItems().length < 1) {
      this.messageService.add({severity: 'warn', summary: 'Info', detail: 'No items in shopping cart'});
      return;
    }
    this.confirmationService.confirm({
      header: "Move items to the pantry",
      message: `Are you sure you want to move all items in the shopping cart to the pantry?`,
      acceptLabel: "Move",
      acceptButtonStyleClass: "p-button-primary p-button-text",
      rejectLabel: "Cancel",
      rejectButtonStyleClass: "p-button-secondary p-button-text",
      accept: () => {
        this.moveAllItemsInCartToPantry();
      },
      reject: () => {
        this.messageService.add({severity: 'info', summary: 'Canceled', detail: 'Move canceled'})
      }
    })
  }


  moveAllItemsInCartToPantry() {
    this.shoppingListService.moveShoppingListItemsToPantry(this.authService.getUserId(), this.shoppingListId).subscribe({
      next: () => {
        this.loadShoppingListDetailDto();
        this.messageService.add({
          severity: 'success',
          summary: 'Success',
          detail: 'Moved items in shopping cart to group pantry'
        });
      },
      error: err => {
        console.error(err);
      }
    })
  }


  confirmDelete() {
    this.confirmationService.confirm({
      header: "Delete Shopping List",
      message: "Are you sure you want to delete this shopping list?",
      acceptLabel: "Delete",
      acceptIcon: "none",
      acceptButtonStyleClass: "p-button-danger p-button-text",
      rejectLabel: "Cancel",
      rejectIcon: "none",
      rejectButtonStyleClass: "p-button-secondary p-button-text",
      accept: () => {
        this.deleteShoppingList();
      },
      reject: () => {
        this.messageService.add({severity: 'info', summary: 'Canceled', detail: 'Delete canceled'})
      }
    })
  }

  dragStart(item: ShoppingListItemDto) {
    this.draggedItem = item;
  }

  dropToShoppingList() {
    // Set checked to false
    if (this.draggedItem.checkedById) {
      this.toggleChecked(this.draggedItem.id);
    }
  }

  dropToPantry() {
    // Set checked to true
    if (!this.draggedItem.checkedById) {
      this.toggleChecked(this.draggedItem.id);
    }
  }


  dragEnd() {
    this.draggedItem = null;
  }

  addItem() {
    if (this.addItemForm.invalid) {
      let errors = this.getAddItemFormErrors();
      this.messageService.addAll(errors.map(error => {
        return {severity: 'error', summary: 'Invalid input', detail: error}
      }));
      return;
    }
    const [unit, amount] = convertQuantity(this.addItemForm.controls.unit.value, this.addItemForm.controls.amount.value)

    let item: ItemCreateDto = {
      description: this.addItemForm.controls.name.value,
      amount: amount,
      unit: unit
    }

    this.shoppingListService.addShoppingListItemToShoppingList(this.authService.getUserId(), this.shoppingListId, item).subscribe({
      next: () => {
        this.loadShoppingListDetailDto();
        this.displayAddItemDialog = false;
        this.messageService.add({severity: 'success', summary: 'Success', detail: 'Item added'});
      },
      error: err => {
        console.error(err);
        this.messageService.add({severity: 'error', summary: 'Error adding item', detail: err.error});
      }
    })

  }

  getAddItemFormErrors() {
    let errors = [];
    if (this.addItemForm.controls.name.errors) {
      if (this.addItemForm.controls.name.errors.required) {
        errors.push("Name is required");
      }
      if (this.addItemForm.controls.name.errors.minlength) {
        errors.push("Name must be at least 2 character long");
      }
      if (this.addItemForm.controls.name.errors.maxlength) {
        errors.push("Name must be at most 255 characters long");
      }
    }
    if (this.addItemForm.controls.amount.errors) {
      if (this.addItemForm.controls.amount.errors.required) {
        errors.push("Amount is required");
      }
      if (this.addItemForm.controls.amount.errors.min) {
        errors.push("Amount must be at least 1");
      }
      if (this.addItemForm.controls.amount.errors.max) {
        errors.push("Amount must be at most 100000");
      }
    }
    if (this.addItemForm.controls.unit.errors) {
      if (this.addItemForm.controls.unit.errors.required) {
        errors.push("Unit is required");
      }
    }
    return errors;
  }

  getEditItemFormErrors() {
    let errors = [];
    if (this.editItemForm.controls.name.errors) {
      if (this.editItemForm.controls.name.errors.required) {
        errors.push("Name is required");
      }
      if (this.editItemForm.controls.name.errors.minlength) {
        errors.push("Name must be at least 2 character long");
      }
      if (this.editItemForm.controls.name.errors.maxlength) {
        errors.push("Name must be at most 255 characters long");
      }
    }
    if (this.editItemForm.controls.amount.errors) {
      if (this.editItemForm.controls.amount.errors.required) {
        errors.push("Amount is required");
      }
      if (this.editItemForm.controls.amount.errors.min) {
        errors.push("Amount must be at least 1");
      }
      if (this.editItemForm.controls.amount.errors.max) {
        errors.push("Amount must be at most 100000");
      }
    }
    if (this.editItemForm.controls.unit.errors) {
      if (this.editItemForm.controls.unit.errors.required) {
        errors.push("Unit is required");
      }
    }
    return errors;
  }

  openAddItemDialog() {
    this.addItemForm.reset();
    this.displayAddItemDialog = true;
  }

  openEditItemDialog(itemToEdit: ShoppingListItemDto) {
    this.itemToEdit = itemToEdit;
    this.editItemForm.reset();
    this.editItemForm.controls.name.setValue(itemToEdit.item.description);
    this.editItemForm.controls.amount.setValue(itemToEdit.item.amount);
    this.editItemForm.controls.unit.setValue(itemToEdit.item.unit);
    this.displayEditItemDialog = true;
  }

  editItem() {
    if (this.editItemForm.invalid) {
      let errors = this.getEditItemFormErrors();
      this.messageService.addAll(errors.map(error => {
        return {severity: 'error', summary: 'Invalid input', detail: error}
      }));
      return;
    }
    const [unit, amount] = convertQuantity(this.editItemForm.controls.unit.value, this.editItemForm.controls.amount.value)
    let itemUpdateDto: ShoppingListItemUpdateDto = {
      id: this.itemToEdit.id,
      item: {
        id: this.itemToEdit.item.id,
        description: this.editItemForm.controls.name.value,
        amount: amount,
        unit: unit
      },
      checked: !!this.itemToEdit.checkedById
    }

    this.shoppingListService.updateShoppingListItem(this.authService.getUserId(), this.shoppingListId, itemUpdateDto).subscribe({
      next: () => {
        this.loadShoppingListDetailDto();
        this.displayEditItemDialog = false;
        this.messageService.add({severity: 'success', summary: 'Success', detail: 'Item updated'});
      },
      error: err => {
        console.error(err);
        this.messageService.add({severity: 'error', summary: 'Error updating item', detail: err.error});
      }
    })

  }

  filteredMissingItems: ItemDetailDto[] = [];
  selectedMissingItem: ItemDetailDto;


  confirmDeleteAllInCart() {
    if (this.getShoppingCartItems().length < 1) {
      this.messageService.add({severity: 'warn', summary: 'Info', detail: 'No items in shopping cart'});
      return;
    }
    this.confirmationService.confirm({
      header: "Delete all items in shopping cart",
      message: `Are you sure you want to delete all items in the shopping cart?`,
      acceptLabel: "Delete",
      acceptButtonStyleClass: "p-button-danger p-button-text",
      rejectLabel: "Cancel",
      rejectButtonStyleClass: "p-button-secondary p-button-text",
      accept: () => {
        this.deleteAllItemsInCart();
      },
      reject: () => {
        this.messageService.add({severity: 'info', summary: 'Canceled', detail: 'Delete canceled'})
      }
    })
  }

  deleteAllItemsInCart() {
    this.shoppingListService.deleteAllItemsInCart(this.authService.getUserId(), this.shoppingListDetailDto.id).subscribe({
      next: () => {
        this.loadShoppingListDetailDto();
        this.messageService.add({
          severity: 'success',
          summary: 'Success',
          detail: 'Deleted all items in shopping cart'
        });
      },
      error: err => {
        console.error(err);
        if (err.error) {
          this.messageService.add({
            severity: 'error',
            summary: 'Error deleting items in shopping cart',
            detail: err.error
          });
        }
        if (err.error.error) {
          this.messageService.add({
            severity: 'error',
            summary: 'Error deleting items in shopping cart',
            detail: err.error.error
          });
        }
        if (err.error.error.errors) {
          err.error.error.errors.forEach((error: any) => {
            this.messageService.add({
              severity: 'error',
              summary: 'Error deleting items in shopping cart',
              detail: error.defaultMessage
            });
          })
        }
      }
    });
  }

  filterMissingItems($event: AutoCompleteCompleteEvent) {
    this.filteredMissingItems = this.allMissingItems.filter(
      item => item.description.toLowerCase().includes($event.query.toLowerCase()))
      .filter(item => !this.getShoppingListItems().map(item => item.item.description).includes(item.description))
      .filter(item => !this.getShoppingCartItems().map(item => item.item.description).includes(item.description));
  }

  selectItemToAdd($event: AutoCompleteSelectEvent) {
    setTimeout(() => {
      this.selectedMissingItem = null;
    });
    // Set addItemForm values
    this.addItemForm.controls.name.setValue($event.value.description);
    this.addItemForm.controls.amount.setValue($event.value.amount);
    this.addItemForm.controls.unit.setValue($event.value.unit);

    // Open add modal
    this.displayAddItemDialog = true;
  }

  protected readonly formatAmount = formatAmount;
}
