import {Component, OnInit} from "@angular/core";

import {ActivatedRoute, Router} from "@angular/router";
import {NgForm} from "@angular/forms";
import {Observable} from "rxjs";
import {RecipeCreateWithoutUserDto} from "../../../dtos/recipe";
import {ItemCreateDto, Unit} from "../../../dtos/item";
import {RecipeService} from "../../../services/recipe.service";
import {clone} from "lodash";
import {ConfirmationService, MessageService} from "primeng/api";
import {getStepSize, getSuffix} from "../../../util/unit-helper";




@Component({
  selector: 'app-recipe-create',
  templateUrl: './recipe-create.component.html',
  styleUrl: './recipe-create.component.scss'
})

export class RecipeCreateComponent implements OnInit {
  error = false;
  recipe: RecipeCreateWithoutUserDto = {
    name: '',
    ingredients: [],
    description: '',
    isPublic: false,
    portionSize:1,

  };
  newIngredient: ItemCreateDto = {
    amount: 1,
    unit: Unit.Piece,
    description: ""
  };
  itemToEdit: ItemCreateDto = undefined;
  submitted: boolean = false;
  newItemDialog: boolean = false;
  itemDialog: boolean = false;
  tooShort=false;
  tooLong=false;
  tooSmall=false;

  selectedItems!: ItemCreateDto[] | null;


  constructor(
    private service: RecipeService,
    private router: Router,
    private route: ActivatedRoute,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
  ) {
  }



  ngOnInit(): void {
  }


  public onRecipeSubmit(form: NgForm): void {
    if (form.valid) {
      let observable: Observable<RecipeCreateWithoutUserDto>;

          observable = this.service.createRecipe(this.recipe);

      observable.subscribe({
        next: data => {
          this.messageService.add({severity: 'success', summary: 'Success', detail: `Recipe ${this.recipe.name} successfully created.`});

          this.router.navigate(['/home/recipe']);
        },
        error: error => {
          this.printError(error);
        }
      });
    }
  }

  onIngredientSubmit() {
    if(this.newIngredient.amount<1) {
      this.tooSmall=true;
      return;
    }

      if(this.newIngredient.description.length>1) {
        this.tooShort=false;
        this.tooSmall=false;
      this.recipe.ingredients.push(this.newIngredient);
      this.newIngredient= {
        amount: 0,
        unit: Unit.Piece,
        description: ""}

    this.hideDialog();
      } else {
      this.tooShort=true;
      }
  }


  hideDialog() {
    this.tooSmall=false;
    this.tooShort=false;
    this.itemDialog = false;
    this.newItemDialog = false;
    this.submitted = false;
  }


  handleEnter(event: KeyboardEvent): void {
    event.preventDefault(); // Prevents the default behavior of adding a new line in the textarea
    this.recipe.description += '\n';
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
      console.error('Could not load recipe items', error);
      this.messageService.add({severity: 'error', summary: 'Error', detail: `Could not load recipe!`});
    }
  }

  deleteSelectedItems(index: number) {
    this.confirmationService.confirm({
      message: 'Are you sure you want to remove ' + this.recipe.ingredients[index].description + '?',
      header: 'Confirm',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.recipe.ingredients.splice(index, 1);
      }
    });
  }

  decrement(index: number) {
    this.recipe.ingredients[index].amount -= this.getStepSize(this.recipe.ingredients[index]);
    if(this.recipe.ingredients[index].amount < 0) {
      this.recipe.ingredients[index].amount = 0;
    }

  }

  increment(index: number) {
    this.recipe.ingredients[index].amount += this.getStepSize(this.recipe.ingredients[index]);
    if(this.recipe.ingredients[index].amount > 1000000) {
      this.recipe.ingredients[index].amount = 1000000;
    }
  }

  openNew() {
    this.newIngredient = {
      description: "",
      amount: 1,
      unit: Unit.Piece,
    };
    this.submitted = false;
    this.newItemDialog = true;
  }

  protected readonly Unit = Unit;
  protected readonly clone = clone;
  protected readonly Object = Object;
  protected readonly getSuffix = getSuffix;
  protected readonly getStepSize = getStepSize;
}


