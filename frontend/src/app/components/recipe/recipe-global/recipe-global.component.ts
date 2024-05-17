import {Component, OnInit} from "@angular/core";
import {RecipeListDto} from "../../../dtos/recipe";
import {ActivatedRoute} from "@angular/router";
import {RecipeService} from "../../../services/recipe.service";


@Component({
  selector: 'app-recipe-global',
  templateUrl: './recipe-global.component.html',
  styleUrl: './recipe-global.component.scss'
})

export class RecipeGlobalComponent implements OnInit {
  recipes: RecipeListDto[] = [];
  error = false;
  errorMessage = '';

  constructor(
    private route: ActivatedRoute,
    private service: RecipeService
  ) {
  }

  ngOnInit(): void {

    this.service.getPublicRecipeOrderedByLikes()
      .subscribe({
        next: data => {
          this.recipes = data;

        },
        error: error => {
          this.defaultServiceErrorHandling(error)
        }
      });
  }

  private defaultServiceErrorHandling(error: any) {
    console.log(error);
    this.error = true;
    if (typeof error.error === 'object') {
      this.errorMessage = error.error.error;
    } else {
      this.errorMessage = error.error;
    }
  }
}
