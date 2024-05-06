import {Component, EventEmitter, Input, Output, ViewChild} from '@angular/core';
import {ItemDetailDto, Unit} from "../../dtos/item";
import {FormsModule, NgForm} from "@angular/forms";
import {KeyValuePipe, NgForOf, NgIf} from "@angular/common";
import {clone} from "lodash";

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
  @Input() itemToEdit: ItemDetailDto = undefined;
  @Input() pantryId: number = undefined;
  @Output() confirm = new EventEmitter<void>();
  @ViewChild('modalClose') modalClose;
  isFormValid: boolean;

  constructor(
  ) {
  }

  onSubmit(form: NgForm) {
    this.isFormValid = form.valid;
    if (form.valid) {
      this.modalClose.nativeElement.click();
    }
  }

  protected readonly Unit = Unit;
}
