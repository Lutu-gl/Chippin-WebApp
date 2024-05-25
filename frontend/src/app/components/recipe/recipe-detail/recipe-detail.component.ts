import {Component, OnInit} from "@angular/core";
import {ActivatedRoute, Router} from "@angular/router";
import {RecipeCreateWithoutUserDto, RecipeDetailDto, RecipeGlobalListDto} from "../../../dtos/recipe";
import {RecipeService} from "../../../services/recipe.service";
import {Unit} from "../../../dtos/item";
import {clone} from "lodash";
import {ToastrService} from "ngx-toastr";
import {data} from "autoprefixer";

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

  recipeId: number;
  error = false;
  errorMessage = '';

  constructor(
    private route: ActivatedRoute,
    private service: RecipeService,
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

  deleteRecipe() {
    this.service.deleteRecipe(this.recipe.id).subscribe({
      next: res => {
        console.log('deleted recipe: ', res)

      },
      error: err => {
        this.defaultServiceErrorHandling(err);
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
          this.defaultServiceErrorHandling(error)
        }
      });

  }

  public dislike(id:number) {
    window.alert("test");
    this.service.dislikeRecipe(id)
      .subscribe({
        next: data => {

        },
        error: error => {
          this.defaultServiceErrorHandling(error)
        }
      });
  }

  public getScore(recipe: RecipeGlobalListDto): number {
    return recipe.likes-recipe.dislikes;
  }

  protected readonly Unit = Unit;
  protected readonly clone = clone;
  protected readonly RecipeDetailMode = RecipeDetailMode;
}
