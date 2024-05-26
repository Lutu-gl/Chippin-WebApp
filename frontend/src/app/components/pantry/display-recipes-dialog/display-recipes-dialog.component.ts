import {Component, Input} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {KeyValuePipe, NgForOf, NgIf} from "@angular/common";
import {RecipeListDto} from "../../../dtos/recipe";
import {RouterLink} from "@angular/router";

@Component({
  selector: 'app-display-recipes-dialog',
  standalone: true,
  imports: [
    FormsModule,
    KeyValuePipe,
    NgForOf,
    NgIf,
    RouterLink
  ],
  templateUrl: './display-recipes-dialog.component.html',
  styleUrl: './display-recipes-dialog.component.scss'
})
export class DisplayRecipesDialogComponent {
  @Input() recipes: RecipeListDto[];

}
