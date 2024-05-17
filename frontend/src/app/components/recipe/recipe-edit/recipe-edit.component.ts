import {Component, OnInit} from "@angular/core";

import {ActivatedRoute, Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";
import {NgForm, NgModel} from "@angular/forms";
import {Observable} from "rxjs";
import {RecipeCreateWithoutUserDto, RecipeDetailDto} from "../../../dtos/recipe";
import {ItemCreateDto, Unit} from "../../../dtos/item";
import {RecipeService} from "../../../services/recipe.service";
import {clone} from "lodash";


@Component({
  selector: 'app-recipe-edit',
  templateUrl: './recipe-edit.component.html',
  styleUrl: './recipe-edit.component.scss'
})

export class RecipeEditComponent implements OnInit {
  error = false;
  errorMessage = '';
  recipe: RecipeDetailDto = {
    name: '',
    ingredients: [],
    description: '',
    isPublic: false,
    portionSize:1,
    likes:0,
    dislikes:0
  };
  newIngredient: ItemCreateDto = {
    amount: 0,
    unit: Unit.Piece,
    description: ""
  };
  itemToEdit: ItemCreateDto = undefined;
  selectedIndexToDelete: number;
  selectedIndexToEdit:number;
  deleteWhatString: string;
  recipeId:number;


  constructor(
    private service: RecipeService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
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


  public dynamicCssClassesForInput(input: NgModel): any {
    return {
      'is-invalid': !input.valid && !input.pristine,
    };
  }

  public onRecipeSubmit(form: NgForm): void {
    if (form.valid) {
      let observable: Observable<RecipeCreateWithoutUserDto>;

      observable = this.service.updateRecipe(this.recipe);

      observable.subscribe({
        next: data => {
          this.notification.success(`Recipe ${this.recipe.name} successfully changed.`);
          this.router.navigate(['/recipe']);
        },
        error: error => {
          this.defaultServiceErrorHandling(error);
        }
      });
    }
  }

  onIngredientSubmit(form: NgForm) {
    console.log('is form valid?', form.valid);
    //if (form.valid) {
    this.service.createItem(this.recipeId,this.newIngredient)
      .subscribe({
        next: data => {
          this.recipe.ingredients.push(data);
        },
        error: error => {
          this.defaultServiceErrorHandling(error)
        }
      });
    this.newIngredient= {
      amount: 0,
      unit: Unit.Piece,
      description: ""}

    //}
  }

  public removeIngredient(index: number) {
    this.service.deleteIngredient(this.recipeId,this.recipe.ingredients[index].id);
    this.recipe.ingredients.splice(index, 1);
    this.selectedIndexToDelete=undefined;
  }


  changeAmount(item: ItemCreateDto, amountChanged: number) {
    item.amount+=amountChanged;
  }


  selectEditItem(item: ItemCreateDto) {
    this.itemToEdit = item;
  }

  getUnitStep(unit: Unit, largeStep: boolean, positive: boolean): number {
    let value: number = 0;
    let prefixNum = positive === true ? 1 : -1;
    switch (unit) {
      case Unit.Piece:
        value = prefixNum * 1;
        break
      case Unit.Gram:
        value = prefixNum * 10;
        break;
      case Unit.Milliliter:
        value = prefixNum * 10;
        break;
      default:
        console.error("Undefined unit");
    }

    return largeStep ? value * 10 : value;
  }

  getUnitStepString(value: number): string {
    if (value > 0) return "+" + value;
    else return value.toString();

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

  selectIndexToDelete(index:number): void {
    this.selectedIndexToDelete=index;
    this.deleteWhatString=this.recipe.ingredients[index].description.toString();
  }

  selectIndexToEdit(index:number):void {
    this.selectedIndexToEdit=index;
  }

  formatAmount(fNumber:number): number {
    if (fNumber == null || isNaN(fNumber)) {
      return 0.0;
    }
    return parseFloat(fNumber.toFixed(1));
  }




  protected readonly Unit = Unit;
  protected readonly clone = clone;
}


