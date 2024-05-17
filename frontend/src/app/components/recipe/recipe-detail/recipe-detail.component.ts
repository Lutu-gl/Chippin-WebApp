import {Component, OnInit} from "@angular/core";
import {ActivatedRoute} from "@angular/router";
import {RecipeCreateWithoutUserDto, RecipeDetailDto} from "../../../dtos/recipe";
import {RecipeService} from "../../../services/recipe.service";
import {Unit} from "../../../dtos/item";
import {clone} from "lodash";


@Component({
  templateUrl: './recipe-detail.component.html',
  styleUrl: './recipe-detail.component.scss'
})

export class RecipeDetailComponent implements OnInit {
  recipe: RecipeDetailDto = {
    name: '',
    ingredients: [],
    description: '',
    isPublic: false,
    portionSize:1,
    likes:0,
    dislikes:0
  };

  recipeId: number;
  error = false;
  errorMessage = '';

  constructor(
    private route: ActivatedRoute,
    private service: RecipeService
  ) {
  }

  ngOnInit(): void {
    const recipeIdparam = this.route.snapshot.paramMap.get('id');
    this.recipeId = recipeIdparam ? +recipeIdparam : Number(recipeIdparam);
    this.service.getRecipeById(this.recipeId)
      .subscribe({
        next: data => {
          this.recipe = data;
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
  //TODO
  deleteRecipe() {

  }

  protected readonly Unit = Unit;
  protected readonly clone = clone;
}
