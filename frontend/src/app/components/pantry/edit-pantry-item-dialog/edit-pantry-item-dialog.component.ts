import {Component, EventEmitter, Input, Output, ViewChild} from '@angular/core';
import {PantryItemDetailDto, PantryItemMergeDto, Unit} from "../../../dtos/item";
import {FormsModule, NgForm} from "@angular/forms";
import {KeyValuePipe, NgClass, NgForOf, NgIf} from "@angular/common";

@Component({
  selector: 'app-edit-pantry-item-dialog',
  standalone: true,
  imports: [
    FormsModule,
    KeyValuePipe,
    NgForOf,
    NgIf,
    NgClass
  ],
  templateUrl: './edit-pantry-item-dialog.component.html',
  styleUrl: './edit-pantry-item-dialog.component.scss'
})

export class EditPantryItemDialogComponent {
  @Input() items: PantryItemDetailDto[] = undefined;
  @Input() itemToEdit: PantryItemDetailDto = undefined;
  @Input() itemToMerge: PantryItemDetailDto = undefined;
  @Input() itemMergeDto: PantryItemMergeDto = undefined;
  @Output() confirmEdit = new EventEmitter<void>();
  @Output() confirmMerge = new EventEmitter<void>();
  @ViewChild('modalClose') modalClose;
  itemToMergeWith: PantryItemDetailDto = null;
  isFormValid: boolean;
  newLowerLimit: number = 0;
  merge: boolean = false;
  edit: boolean = true;

  constructor() {
  }

  reset() {
    this.modalClose.nativeElement.click();
    this.newLowerLimit = 0;
    this.itemToMerge = null;
    this.itemMergeDto = null;
    this.itemToMergeWith = null;
    this.merge = false;
    this.edit = true;
  }

  openEdit() {
    this.edit = true;
    this.merge = false;
  }

  openMerge() {
    this.merge = true;
    this.edit = false;
  }

  onSubmit(form: NgForm) {
    this.isFormValid = form.valid;
    if (form.valid) {
      this.reset();
    }
  }

  markImportant() {
    if (this.itemToEdit.lowerLimit !== null) {
      this.newLowerLimit = this.itemToEdit.lowerLimit;
      this.itemToEdit.lowerLimit = null;
    } else {
      this.itemToEdit.lowerLimit = this.newLowerLimit;
    }
  }

  prepareItemToMerge() {
    if (this.itemToMergeWith === undefined || this.itemToMergeWith === null) {
      this.itemMergeDto.itemToDeleteId = null;
      this.itemMergeDto.result = null;
      return;
    } else {
      this.itemMergeDto.result = {
        id: this.itemToMerge.id,
        description: this.itemToMerge.description,
        unit: this.itemToMerge.unit,
        amount: this.itemMergeDto.result?.unit === this.itemToMergeWith?.unit ? this.itemToMerge.amount + this.itemToMergeWith.amount : this.itemToMerge.amount,
        lowerLimit: this.itemToMerge.lowerLimit
      }
      this.itemMergeDto.itemToDeleteId = this.itemToMergeWith.id;
    }
  }

  filterItems(item: PantryItemDetailDto): PantryItemDetailDto[] {
    return this.items.filter(pantryItem => pantryItem.id !== item.id);
  }

  protected readonly Unit = Unit;
}
