import {Component, EventEmitter, Input, OnInit, Output, WritableSignal} from '@angular/core';
import {DialogModule} from "primeng/dialog";
import {ChipsModule} from "primeng/chips";
import {ButtonModule} from "primeng/button";
import {MultiSelectModule} from "primeng/multiselect";
import {Category} from "../../../dtos/category";
import {valuesIn} from "lodash";
import {AutoCompleteCompleteEvent, AutoCompleteModule} from "primeng/autocomplete";
import {GroupDetailDto, GroupDto} from "../../../dtos/group";
import {AuthService} from 'src/app/services/auth.service';
import {GroupService} from "../../../services/group.service";
import {MessageService} from "primeng/api";
import {FormsModule} from "@angular/forms";
import {ShoppingListCreateEditDto} from "../../../dtos/shoppingList";
import {ShoppingListService} from "../../../services/shopping-list.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-shopping-list-create-modal',
  standalone: true,
  imports: [
    DialogModule,
    ChipsModule,
    ButtonModule,
    MultiSelectModule,
    AutoCompleteModule,
    FormsModule
  ],
  templateUrl: './shopping-list-create-modal.component.html',
  styleUrl: './shopping-list-create-modal.component.scss'
})
export class ShoppingListCreateModalComponent implements OnInit {
  @Input() visible: boolean = false;
  @Output() visibleChange = new EventEmitter<boolean>();
  @Output() update = new EventEmitter();
  @Input() preChosenGroupId: number | null = null;
  categories: string[] = [];
  filteredGroups: GroupDetailDto[];
  allGroups: GroupDetailDto[] = [];
  shoppingListToCreate: ShoppingListCreateEditDto = {
    name: '',
    categories: [],
    group: null
  };

  constructor(
    private groupService: GroupService,
    private messageService: MessageService,
    private authService: AuthService,
    private shoppingListService: ShoppingListService,
    private router: Router
  ) {
  }

  ngOnInit() {}

  ngOnChanges(): void {
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
    if (this.preChosenGroupId) {
      this.groupService.getById(this.preChosenGroupId).subscribe({
        next: group => {
          this.shoppingListToCreate.group = group;
        },
        error: error => {
          console.error(error);
          this.messageService.add({severity: 'error', summary: 'Error', detail: 'Could not load group'});
        }
      });
    }
  }

  hide() {
    this.clear();
    this.visibleChange.emit(false);
  }

  clear() {
    this.shoppingListToCreate = {
      name: '',
      categories: [],
      group: null
    };
  }

  filterGroups($event: AutoCompleteCompleteEvent) {
    this.filteredGroups = this.allGroups.filter(
      group => group.groupName.toLowerCase().includes($event.query.toLowerCase())
    )

  }

  onSave() {
    // Check if the shopping list is valid
    if (!this.shoppingListToCreate.name || this.shoppingListToCreate.name.trim() === '') {
      this.messageService.add({
        severity: 'error',
        summary: 'Error',
        detail: 'Please enter a name for the shopping list'
      });
      return;
    }

    // Save the shopping list
    this.shoppingListService.createShoppingList(this.authService.getUserId(), this.shoppingListToCreate).subscribe({
      next: shoppingList => {
        this.messageService.add({severity: 'success', summary: 'Success', detail: 'Shopping list created'});
        this.router.navigate(['/shopping-list', shoppingList.id])
        this.update.emit();
      },
      error: () => {
        this.messageService.add({severity: 'error', summary: 'Error', detail: 'Could not create shopping list'});
      }
    })


    this.clear();
    this.visibleChange.emit(false);
  }
}
