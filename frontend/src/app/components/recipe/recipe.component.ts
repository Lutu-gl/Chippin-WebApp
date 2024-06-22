import {Component, OnInit} from "@angular/core";
import {RecipeGlobalListDto, RecipeListDto, RecipeSearch} from "../../dtos/recipe";
import {RecipeService} from "../../services/recipe.service";
import {debounceTime, Subject} from "rxjs";
import {MessageService} from "primeng/api";


@Component({
  selector:'./recipe',
  templateUrl: './recipe.component.html',
  styleUrl: './recipe.component.scss'
})

export class RecipeComponent implements OnInit {
  recipes: RecipeListDto[] = [];
  error = false;
  searchString: string = "";
  searchChangedObservable = new Subject<void>();
  hasRecipes = false;
  rows:number=20;
  currentPage:number=1;
  totalRecords:number;
  paginatedRecipes:RecipeListDto[]=[];

  constructor(
    private service: RecipeService,
    private messageService: MessageService,
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
          this.totalRecords=this.recipes.length;
          this.paginate({ first: 0, rows: this.rows });
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
        this.totalRecords=this.recipes.length;
        this.paginate({ first: 0, rows: this.rows });
      },
      error: err => {
        this.printError(err);
      }
    });
  }

  paginate(event: any) {
    this.currentPage = event.first / event.rows + 1;
    this.paginatedRecipes = this.recipes.slice(event.first, event.first + event.rows);
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
