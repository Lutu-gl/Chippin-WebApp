import {Component} from '@angular/core';
import {NgForOf, NgIf, SlicePipe} from "@angular/common";
import {RecipeDetailDto} from "../../../dtos/recipe";
import {RouterLink} from "@angular/router";

@Component({
  selector: 'app-recipe-info-card-content',
  standalone: true,
  imports: [
    NgForOf,
    NgIf,
    SlicePipe,
    RouterLink
  ],
  templateUrl: './recipe-info-card-content.component.html',
  styleUrl: './recipe-info-card-content.component.scss'
})
export class RecipeInfoCardContentComponent {
  recipes: RecipeDetailDto[] = [];
}
