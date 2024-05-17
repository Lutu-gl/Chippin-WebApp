import {Component, EventEmitter, Input, Output, ViewChild} from '@angular/core';
import {PantryItemDetailDto, Unit} from "../../../dtos/item";
import {FormsModule, NgForm} from "@angular/forms";
import {KeyValuePipe, NgForOf, NgIf} from "@angular/common";

@Component({
  selector: 'app-edit-pantry-item-dialog',
  standalone: true,
  imports: [
    FormsModule,
    KeyValuePipe,
    NgForOf,
    NgIf
  ],
  templateUrl: './edit-pantry-item-dialog.component.html',
  styleUrl: './edit-pantry-item-dialog.component.scss'
})

export class EditPantryItemDialogComponent {
  @Input() itemToEdit: PantryItemDetailDto = undefined;
  @Input() pantryId: number = undefined;
  @Output() confirm = new EventEmitter<void>();
  @ViewChild('modalClose') modalClose;
  isFormValid: boolean;
  newLowerLimit: number = 0;

  constructor() {
  }

  onSubmit(form: NgForm) {
    this.isFormValid = form.valid;
    if (form.valid) {
      this.newLowerLimit = 0;
      this.modalClose.nativeElement.click();
    }
  }

  markImportant() {
    if(this.itemToEdit.lowerLimit !== null) {
      this.newLowerLimit = this.itemToEdit.lowerLimit;
      this.itemToEdit.lowerLimit = null;
    } else {
      this.itemToEdit.lowerLimit = this.newLowerLimit;
    }
  }

  protected readonly Unit = Unit;
}
