import {Component, OnInit} from "@angular/core";
import {RecipeGlobalListDto, RecipeListDto, RecipeSearch} from "../../../dtos/recipe";
import {ActivatedRoute, Router} from "@angular/router";
import {RecipeService} from "../../../services/recipe.service";
import {debounceTime, Subject} from "rxjs";



@Component({
  selector: 'app-recipe-global',
  templateUrl: './recipe-global.component.html',
  styleUrl: './recipe-global.component.scss'
})

export class RecipeGlobalComponent implements OnInit {
  recipes: RecipeGlobalListDto[] = [];
  error = false;
  errorMessage = '';
  searchString: string = "";
  searchChangedObservable = new Subject<void>();

  constructor(
    private route: ActivatedRoute,
    private router: Router,
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

    this.searchChangedObservable
      .pipe(debounceTime(300))
      .subscribe({next: () => this.filterRecipe()});
  }

  searchChanged() {
    this.searchChangedObservable.next();
  }

  public like(id: number) {
    this.service.likeRecipe(id)
      .subscribe({
        next: data => {

        },
        error: error => {
          this.defaultServiceErrorHandling(error)
        }
      });

    this.router.navigate(['/recipe/global']);

  }

  public dislike(id:number) {
    this.service.dislikeRecipe(id)
      .subscribe({
        next: data => {

        },
        error: error => {
          this.defaultServiceErrorHandling(error)
        }
      });
    this.router.navigate(['/recipe/global']);


  }

  public getScore(recipe: RecipeGlobalListDto): number {
    return recipe.likes-recipe.dislikes;
  }

  filterRecipe() {
    let search: RecipeSearch = {
      details: this.searchString
    };

    this.service.searchGlobalRecipes(search).subscribe({
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
