import {Component, OnInit} from "@angular/core";
import {RecipeGlobalListDto, RecipeListDto, RecipeSearch} from "../../../dtos/recipe";
import {ActivatedRoute, Router} from "@angular/router";
import {RecipeService} from "../../../services/recipe.service";
import {debounceTime, Subject} from "rxjs";
import {ConfirmationService, MessageService} from "primeng/api";



@Component({
  selector: 'recipe-global',
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
    private service: RecipeService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService
  ) {
  }

  ngOnInit(): void {

    this.service.getPublicRecipeOrderedByLikes()
      .subscribe({
        next: data => {
          this.recipes = data;

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

  public like(id: number) {
    this.service.likeRecipe(id)
      .pipe(debounceTime(1000))
      .subscribe({
        next: data => {

        },
        error: error => {
          this.messageService.add({severity: 'error', summary: 'Error', detail: `You are liking too fast. This like will not be saved`});
        }
      });

    let likeRecipe = this.recipes.find(recipe => recipe.id === id);
    let index = this.recipes.findIndex(recipe => recipe.id === id);

    likeRecipe.likes++;
    if(likeRecipe.dislikedByUser) {
      likeRecipe.dislikes--;
    }
    likeRecipe.likedByUser=true;
    likeRecipe.dislikedByUser=false;

    this.recipes[index] =likeRecipe;


  }

  public dislike(id:number) {
    this.service.dislikeRecipe(id)
      .pipe(debounceTime(1000))
      .subscribe({
        next: data => {

        },
        error: error => {
          this.messageService.add({severity: 'error', summary: 'Error', detail: `You are disliking too fast. This dislike will not be saved`});
        }
      });


    let dislikeRecipe = this.recipes.find(recipe => recipe.id === id);
    let index = this.recipes.findIndex(recipe => recipe.id === id);

    dislikeRecipe.dislikes++;
    if(dislikeRecipe.likedByUser) {
      dislikeRecipe.likes--;
    }
    dislikeRecipe.dislikedByUser=true;
    dislikeRecipe.likedByUser=false;

    this.recipes[index] =dislikeRecipe;

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
        this.printError(err);
      }
    });
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
