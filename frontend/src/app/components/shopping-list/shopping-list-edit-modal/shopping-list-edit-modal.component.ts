import {Component, EventEmitter, Input, Output} from '@angular/core';
import {AutoCompleteCompleteEvent, AutoCompleteModule} from "primeng/autocomplete";
import {ButtonModule} from "primeng/button";
import {DialogModule} from "primeng/dialog";
import {InputTextModule} from "primeng/inputtext";
import {MultiSelectModule} from "primeng/multiselect";
import {PaginatorModule} from "primeng/paginator";
import {MessageService, SharedModule} from "primeng/api";
import {GroupDetailDto} from "../../../dtos/group";
import {ShoppingListCreateEditDto, ShoppingListDetailDto} from "../../../dtos/shoppingList";
import {GroupService} from "../../../services/group.service";
import {AuthService} from "../../../services/auth.service";
import {ShoppingListService} from "../../../services/shopping-list.service";
import {Router} from "@angular/router";
import {valuesIn} from "lodash";
import {Category} from "../../../dtos/category";

@Component({
  selector: 'app-shopping-list-edit-modal',
  standalone: true,
    imports: [
        AutoCompleteModule,
        ButtonModule,
        DialogModule,
        InputTextModule,
        MultiSelectModule,
        PaginatorModule,
        SharedModule
    ],
  templateUrl: './shopping-list-edit-modal.component.html',
  styleUrl: './shopping-list-edit-modal.component.scss'
})
export class ShoppingListEditModalComponent {
  @Input() shoppingList: ShoppingListDetailDto;
  @Input() visible: boolean = false;
  @Output() visibleChange = new EventEmitter<boolean>();
  @Output() update = new EventEmitter();
  categories: string[] = [];
  filteredGroups: GroupDetailDto[];
  allGroups: GroupDetailDto[] = [];
  shoppingListToEdit: ShoppingListCreateEditDto;

  constructor(
    private groupService: GroupService,
    private messageService: MessageService,
    private authService: AuthService,
    private shoppingListService: ShoppingListService,
    private router: Router
  ) {
  }

  ngOnInit(): void {
    this.categories = valuesIn(Category);
    this.groupService.getGroups().subscribe({
        next: data => {
          this.allGroups = data;
        },
        error: error => {
          console.error(error);
          this.messageService.add({severity: 'error', summary: 'Error', detail: 'Could not load groups'});
        }
      }
    );
    this.shoppingListToEdit = {
      name: this.shoppingList.name,
      categories: this.shoppingList.categories,
      group: this.shoppingList.group
    }
  }

  hide() {
    this.visibleChange.emit(false);
  }

  filterGroups($event: AutoCompleteCompleteEvent) {
    this.filteredGroups = this.allGroups.filter(
      group => group.groupName.toLowerCase().includes($event.query.toLowerCase())
    )

  }

  onSave() {
    // Check if the shopping list is valid
    if (!this.shoppingListToEdit.name || this.shoppingListToEdit.name.trim() === '') {
      this.messageService.add({
        severity: 'error',
        summary: 'Error',
        detail: 'Please enter a name for the shopping list'
      });
      return;
    }
    // Save the shopping list
    this.shoppingListService.updateShoppingList(this.shoppingList.id, this.shoppingListToEdit).subscribe({
      next: () => {
        this.messageService.add({severity: 'success', summary: 'Success', detail: 'Shopping list updated'});
        this.update.emit();
      },
      error: () => {
        this.messageService.add({severity: 'error', summary: 'Error', detail: 'Could not update shopping list'});
      }
    })

    this.visibleChange.emit(false);
  }
}
