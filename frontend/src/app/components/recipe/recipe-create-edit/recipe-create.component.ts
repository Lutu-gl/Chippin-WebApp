import {Component, OnInit} from "@angular/core";

import {ActivatedRoute, Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";
import {NgForm, NgModel} from "@angular/forms";
import {Observable} from "rxjs";
import {RecipeCreateWithoutUserDto} from "../../../dtos/recipe";
import {ItemCreateDto, Unit} from "../../../dtos/item";
import {RecipeService} from "../../../services/recipe.service";
import {clone} from "lodash";

export enum RecipeCreateEditMode {
  create,
  edit,
}

@Component({
  selector: 'app-recipe-create',
  templateUrl: './recipe-create.component.html',
  styleUrl: './recipe-create.component.scss'
})

export class RecipeCreateComponent implements OnInit {
  error = false;
  errorMessage = '';
  recipe: RecipeCreateWithoutUserDto = {
    name: '',
    ingredients: [],
    description: '',
    isPublic: false,
    portionSize:1
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


  constructor(
    private service: RecipeService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
  ) {
  }



  ngOnInit(): void {
  }


  public dynamicCssClassesForInput(input: NgModel): any {
    return {
      'is-invalid': !input.valid && !input.pristine,
    };
  }

  public onRecipeSubmit(form: NgForm): void {
    if (form.valid) {
      let observable: Observable<RecipeCreateWithoutUserDto>;

          observable = this.service.createRecipe(this.recipe);

      observable.subscribe({
        next: data => {
          this.notification.success(`Recipe ${this.recipe.name} successfully created.`);
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
      this.recipe.ingredients.push(this.newIngredient);
      this.newIngredient= {
        amount: 0,
        unit: Unit.Piece,
        description: ""}

    //}
  }

  public removeIngredient(index: number) {
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


