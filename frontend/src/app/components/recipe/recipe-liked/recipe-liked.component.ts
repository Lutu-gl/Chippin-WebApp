import {Component, OnInit} from "@angular/core";
import {RecipeListDto, RecipeSearch} from "../../../dtos/recipe";
import {RecipeService} from "../../../services/recipe.service";
import {debounceTime, Subject} from "rxjs";
import {MessageService} from "primeng/api";


@Component({
  selector: './recipe-like',
  templateUrl: './recipe-liked.component.html',
  styleUrl: './recipe-liked.component.scss'
})

export class RecipeLikedComponent implements OnInit {
  recipes: RecipeListDto[] = [];
  error = false;
  hasRecipes = false;
  searchString: string = "";
  searchChangedObservable = new Subject<void>();

  constructor(
    private service: RecipeService,
    private messageService: MessageService,
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
          this.printError(error)
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
        this.printError(err);
      }
    });
  }

  noRecipes(): boolean {
    return !this.hasRecipes;
  }

  printError(error): void {
    if (error && error.error && error.error.errors) {
      for (let i = 0; i < error.error.errors.length; i++) {
        this.messageService.add({severity: 'error', summary: 'Error', detail: `${error.error.errors[i]}`});
      }
    } else if (error && error.error && error.error.message) {
      this.messageService.add({severity: 'error', summary: 'Error', detail: `${error.error.message}`});
    } else if (error && error.error && error.error.detail) {
      this.messageService.add({severity: 'error', summary: 'Error', detail: `${error.error.detail}`});
    } else {
      console.error('Could not load pantry items', error);
      this.messageService.add({severity: 'error', summary: 'Error', detail: `Could not load Recipe!`});
    }
  }
}
