import {Component, OnInit} from "@angular/core";
import {ActivatedRoute, Router} from "@angular/router";
import {RecipeCreateWithoutUserDto, RecipeDetailDto, RecipeGlobalListDto} from "../../../dtos/recipe";
import {RecipeService} from "../../../services/recipe.service";
import {Unit} from "../../../dtos/item";
import {clone} from "lodash";
import {ToastrService} from "ngx-toastr";
import {GroupDto} from "../../../dtos/group";
import {Observable, of} from "rxjs";
import {UserService} from "../../../services/user.service";

export enum RecipeDetailMode {
  owner,
  viewer
}

@Component({
  templateUrl: './recipe-detail.component.html',
  styleUrl: './recipe-detail.component.scss'
})
export class RecipeDetailComponent implements OnInit {
  mode: RecipeDetailMode = RecipeDetailMode.owner;
  recipe: RecipeDetailDto = {
    name: '',
    ingredients: [],
    description: '',
    isPublic: false,
    portionSize:1,
    likes:0,
    dislikes:0
  };
  portion:number = 1;
  group: GroupDto = {
    id: 0,
    members: [],
    groupName: ''
  }
  recipeId: number;
  error = false;
  errorMessage = '';
  groups: GroupDto[] = [];

  constructor(
    private route: ActivatedRoute,
    private service: RecipeService,
    private userService: UserService,
    private router: Router,
    private notification: ToastrService
  ) {
  }

  ngOnInit(): void {
    this.route.data.subscribe(data => {
      this.mode = data.mode;
    });
    const recipeIdparam = this.route.snapshot.paramMap.get('id');
    this.recipeId = recipeIdparam ? +recipeIdparam : Number(recipeIdparam);
    this.service.getRecipeById(this.recipeId)
      .subscribe({
        next: data => {
          this.recipe = data;
        },
        error: error => {
          this.printError(error);
        }
      });

    this.userService.getUserGroups().subscribe({
      next: groups => {
        this.groups = groups;
      },
      error: error => {
        this.printError(error);
      }
    });
  }

  printError(error): void {
    if (error && error.error && error.error.errors) {
      for (let i = 0; i < error.error.errors.length; i++) {
        this.notification.error(`${error.error.errors[i]}`);
      }
    } else if (error && error.error && error.error.message) { // if no detailed error explanation exists. Give a more general one if available.
      this.notification.error(`${error.error.message}`);
    } else {
      window.alert(error);
      console.log(error);
      if(error.status !== 401) {
        const errorMessage = error.status === 0
          ? 'Is the backend up?'
          : error.message.message;
        this.notification.error(errorMessage, 'Could not connect to the server.');
      }
    }
  }

  deleteRecipe() {
    this.service.deleteRecipe(this.recipe.id).subscribe({
      next: res => {
        console.log('deleted recipe: ', res);

      },
      error: err => {
        this.printError(err);
      }
    });
      this.notification.success("Recipe successfully deleted");
      this.router.navigate(['/recipe']);
  }


  public like(id: number) {
    this.service.likeRecipe(id)
      .subscribe({
        next: data => {

        },
        error: error => {
          this.printError(error);
        }
      });

  }

  public updatePortion() {

  }

  public dislike(id:number) {
    window.alert("test");
    this.service.dislikeRecipe(id)
      .subscribe({
        next: data => {

        },
        error: error => {
          this.printError(error);
        }
      });
  }

  public getScore(recipe: RecipeGlobalListDto): number {
    return recipe.likes-recipe.dislikes;
  }

  public removeRecipeIngredientsFromPantry() {
    this.service.removeRecipeIngredientsFromPantry(this.group.id,this.recipe.id, this.portion).subscribe(
      {
        next: data => {
            console.log("Es funktioniert");
            console.log(data);
        },
        error: error => {
          this.printError(error)
        }
      }
    );
  }

  getGroupSuggestions = (input: string): Observable<GroupDto[]> => {
    if (!input) {
      return of([]);
    }
    const filterValue = input.toLowerCase();
    return of(this.groups.filter(group => group.groupName.toLowerCase().includes(filterValue)));
  }

  formatGroup = (group: GroupDto | null): string => {
    return group ? group.groupName : '';
  }

  protected readonly Unit = Unit;
  protected readonly clone = clone;
  protected readonly RecipeDetailMode = RecipeDetailMode;
}
