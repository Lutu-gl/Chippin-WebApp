import {Component, OnInit} from '@angular/core';
import {ShoppingListDetailDto, ShoppingListItemDto, ShoppingListItemUpdateDto} from "../../../dtos/shoppingList";
import {ShoppingListService} from "../../../services/shopping-list.service";
import {ActivatedRoute, Router, RouterLink} from "@angular/router";
import {ToastrService} from "ngx-toastr";
import {NgClass, NgForOf, NgIf} from "@angular/common";
import {AuthService} from "../../../services/auth.service";
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {ConfirmDeleteDialogComponent} from "../../confirm-delete-dialog/confirm-delete-dialog.component";
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
import {DisplayedUnit, ItemCreateDto} from "../../../dtos/item";
import {valuesIn} from "lodash";
import {InputGroupModule} from "primeng/inputgroup";
import {PaginatorModule} from "primeng/paginator";
import {InputGroupAddonModule} from "primeng/inputgroupaddon";
import {convertQuantity, displayQuantity} from "../../../util/unit-helper";

@Component({
  selector: 'app-shopping-list-detail',
  standalone: true,
  imports: [
    RouterLink,
    NgForOf,
    NgIf,
    FormsModule,
    NgClass,
    ConfirmDeleteDialogComponent,
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
  ],
  templateUrl: './shopping-list-detail.component.html',
  styleUrl: './shopping-list-detail.component.scss'
})
export class ShoppingListDetailComponent implements OnInit {

  constructor(private shoppingListService: ShoppingListService,
              private route: ActivatedRoute,
              private notification: ToastrService,
              private router: Router,
              private authService: AuthService,
              private confirmationService: ConfirmationService,
              private messageService: MessageService) {
    this.addItemForm = new FormGroup({
      name: new FormControl('', [Validators.required, Validators.minLength(1), Validators.maxLength(255)]),
      amount: new FormControl('', [Validators.required, Validators.min(1), Validators.max(100000)]),
      unit: new FormControl('', Validators.required)

    })
  }

  shoppingListDetailDto: ShoppingListDetailDto;
  groupId: number;
  shoppingListId: number;
  selectedItem: ShoppingListItemDto;
  draggedItem: ShoppingListItemDto;
  shoppingListItemMenuItems: MenuItem[] | undefined;
  shoppingCartItemMenuItems: MenuItem[] | undefined;
  displayAddItemDialog: boolean = false;
  selectedUnit: any;
  units: any[]
  addItemForm: FormGroup = new FormGroup({});

  ngOnInit(): void {
    this.route.params.subscribe({
      next: params => {
        this.groupId = +params['id'];
        this.shoppingListId = +params['shoppingListId'];
      },
      error: err => {
        this.notification.error('Error loading shopping list');
        this.router.navigate(['/group', this.groupId]);
        console.error(err);
      }
    });

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

        }
      },
      {
        label: "Delete",
        icon: "pi pi-trash",
        command: () => {
          // Delete item
        }
      }
    ]

    this.shoppingCartItemMenuItems = [
      {
        label: "Move to pantry",
        icon: "pi pi-check",
        command: () => {
          // Move item to pantry
        }
      },
      {
        label: "Delete",
        icon: "pi pi-trash",
        command: () => {
          // Delete item
        }
      },
      {
        label: "Move back to shopping list",
        icon: "pi pi-undo",
        command: () => {
          // Move item back to shopping list
        }
      }

    ]

    this.loadShoppingListDetailDto();
  }

  loadShoppingListDetailDto(): void {
    this.shoppingListService.getShoppingListById(this.shoppingListId).subscribe({
      next: shoppingList => {
        this.shoppingListDetailDto = shoppingList;
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
      error: err => {
        console.error(err);
        let error = err.error;
        this.messageService.add({severity: 'error', summary: 'Error deleting shopping list', detail: error});
      }
    });
  }

  selectItem(item: ShoppingListItemDto) {
    this.selectedItem = item;
  }

  deleteItem(itemId: number) {
    this.shoppingListService.deleteShoppingListItem(this.authService.getUserId(), this.shoppingListId, itemId).subscribe({
      next: value => {
        this.loadShoppingListDetailDto();
      },
      error: err => {
        console.error(err);
      }
    })
  }

  addItemToPantry(itemId: number) {
    this.shoppingListService.moveShoppingListItemToPantry(this.authService.getUserId(), this.shoppingListId, itemId).subscribe({
      next: value => {
        this.loadShoppingListDetailDto();
        this.notification.success("Item moved to pantry");
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
      next: value => {
        this.loadShoppingListDetailDto();
      },
      error: err => {
        console.error(err);
      }
    })
  }

  moveItemsInCartToPantry() {
    if (this.getShoppingCartItems().length < 1) {
      this.messageService.add({severity: 'warn', summary: 'Info', detail: 'No items in shopping cart'});
      return;
    }
    this.shoppingListService.moveShoppingListItemsToPantry(this.authService.getUserId(), this.shoppingListId).subscribe({
      next: value => {
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

  drop() {
    this.toggleChecked(this.draggedItem.id);
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
      next: value => {
        this.loadShoppingListDetailDto();
        this.displayAddItemDialog = false;
        this.notification.success("Item added to shopping list");
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
        errors.push("Name must be at least 1 character long");
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

  openAddItemDialog() {
    this.addItemForm.reset();
    this.displayAddItemDialog = true;

  }

  protected readonly displayQuantity = displayQuantity;
}
