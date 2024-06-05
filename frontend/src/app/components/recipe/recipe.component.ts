import {Component, OnInit} from "@angular/core";
import {RecipeListDto, RecipeSearch} from "../../dtos/recipe";
import {ActivatedRoute} from "@angular/router";
import {RecipeService} from "../../services/recipe.service";
import {debounceTime, Subject} from "rxjs";
import {ConfirmationService, MessageService} from "primeng/api";


@Component({
  selector:'./recipe',
  templateUrl: './recipe.component.html',
  styleUrl: './recipe.component.scss'
})

export class RecipeComponent implements OnInit {
  recipes: RecipeListDto[] = [];
  error = false;
  errorMessage = '';
  searchString: string = "";
  searchChangedObservable = new Subject<void>();
  isRecipeInfoDialogVisible: boolean = false;
  hasRecipes = false;

  constructor(
    private route: ActivatedRoute,
    private service: RecipeService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService
  ) {
  }

  ngOnInit(): void {
    console.log("Your");
    this.service.getRecipesFromUser()
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

    this.service.searchOwnRecipes(search).subscribe({
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
