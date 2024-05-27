import {Component, OnInit} from "@angular/core";
import {RecipeListDto, RecipeSearch} from "../../dtos/recipe";
import {ActivatedRoute} from "@angular/router";
import {RecipeService} from "../../services/recipe.service";
import {debounceTime, Subject} from "rxjs";


@Component({
  templateUrl: './recipe.component.html',
  styleUrl: './recipe.component.scss'
})

export class RecipeComponent implements OnInit {
  recipes: RecipeListDto[] = [];
  error = false;
  errorMessage = '';
  searchString: string = "";
  searchChangedObservable = new Subject<void>();

  constructor(
    private route: ActivatedRoute,
    private service: RecipeService
  ) {
  }

  ngOnInit(): void {
    this.service.getRecipesFromUser()
      .subscribe({
        next: data => {
          this.recipes = data;
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

    this.service.searchOwnRecipes(search).subscribe({
      next: res => {
        this.recipes = res;
      },
      error: err => {
        this.defaultServiceErrorHandling(err);
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
