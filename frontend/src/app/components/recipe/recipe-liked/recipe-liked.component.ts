import {Component, OnInit} from "@angular/core";
import {ActivatedRoute} from "@angular/router";
import {RecipeListDto, RecipeSearch} from "../../../dtos/recipe";
import {RecipeService} from "../../../services/recipe.service";
import {debounceTime, Subject} from "rxjs";


@Component({
  selector: './recipe-like',
  templateUrl: './recipe-liked.component.html',
  styleUrl: './recipe-liked.component.scss'
})

export class RecipeLikedComponent implements OnInit {
  recipes: RecipeListDto[] = [];
  error = false;
  errorMessage = '';
  hasRecipes = false;
  searchString: string = "";
  searchChangedObservable = new Subject<void>();

  constructor(
    private route: ActivatedRoute,
    private service: RecipeService
  ) {
  }

  ngOnInit(): void {
    console.log("Like");
    this.service.getLikedRecipesFromUser()
      .subscribe({
        next: data => {
          this.recipes = data;
          if(data.length!=0) {
            this.hasRecipes=true;
          }
        },
        error: error => {
          this.defaultServiceErrorHandling(error)
        }
      });
    this.searchChangedObservable
      .pipe(debounceTime(300))
      .subscribe({next: () => this.filterRecipe()});
  }

  searchChanged() {
    this.searchChangedObservable.next();
  }
  filterRecipe() {
    let search: RecipeSearch = {
      details: this.searchString
    };

    this.service.searchLikedRecipes(search).subscribe({
      next: res => {
        this.recipes = res;
      },
      error: err => {
        this.defaultServiceErrorHandling(err);
      }
    });
  }

  noRecipes(): boolean {
    return !this.hasRecipes;
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
