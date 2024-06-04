import {Component, OnInit} from "@angular/core";

import {ActivatedRoute, Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";
import {NgForm, NgModel} from "@angular/forms";
import {Observable} from "rxjs";
import {RecipeCreateWithoutUserDto, RecipeDetailDto} from "../../../dtos/recipe";
import {ItemCreateDto, ItemDetailDto, PantryItemDetailDto, Unit} from "../../../dtos/item";
import {RecipeService} from "../../../services/recipe.service";
import {clone} from "lodash";
import {ConfirmationService, MessageService} from "primeng/api";


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
    dislikes:0,
  };
  newIngredient: ItemCreateDto = {
    amount: 0,
    unit: Unit.Piece,
    description: ""
  };
  itemToEdit: ItemDetailDto = undefined;
  recipeId:number;


  submitted: boolean = false;
  newItemDialog: boolean = false;
  itemDialog: boolean = false;
  changeItem:boolean=false;
  items!: ItemDetailDto[];
  item!: ItemDetailDto;

  selectedItems!: ItemCreateDto[] | null;


  constructor(
    private service: RecipeService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService
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
          this.printError(error)
        }
      });
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
          this.printError(error);
        }
      });
    }
  }

  onIngredientSubmit() {

    this.service.createItem(this.recipeId,this.newIngredient)
      .subscribe({
        next: data => {
          this.recipe.ingredients.push(data);
        },
        error: error => {
          this.printError(error)
        }
      });
    this.newIngredient= {
      amount: 0,
      unit: Unit.Piece,
      description: ""}

    this.hideDialog();
  }

  public deleteIngredient(id: number, index:number) {
    this.confirmationService.confirm({
      message: 'Are you sure you want to remove ' + this.recipe.ingredients[index].description + '?',
      header: 'Confirm',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.recipe.ingredients.splice(index, 1);
        this.service.deleteIngredient(this.recipeId,id).subscribe({
          next: res => {
            console.log('deleted recipe: ', res);

          },
          error: err => {
            this.printError(err);
          }
        });
      }
    });




  }

  changeIngredient(index:number) {
    this.itemToEdit=this.recipe.ingredients[index];

    this.changeItem=true;
  }


  openNew() {
    this.newIngredient = {
      description: "",
      amount: 0,
      unit: Unit.Piece,
    };
    this.submitted = false;
    this.newItemDialog = true;
  }

  getRecipeSuffix(item: ItemCreateDto): String {
    switch (item.unit) {
      case Unit.Piece:
        return item.amount == 1 ? " Piece" : " Pieces";
      case Unit.Gram:
        return item.amount < 1000 ? "g" : "kg";
      case Unit.Milliliter:
        return item.amount < 1000 ? "ml" : "l";
      default:
        console.error("Unknown Unit");
        return "";
    }
  }





  deleteRecipe() {
    this.service.deleteRecipe(this.recipe.id);
    this.notification.success("Recipe successfully deleted");
    this.router.navigate(['/recipe']);
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

  hideDialog() {
    this.itemDialog = false;
    this.newItemDialog = false;
    this.submitted = false;
  }

  hideEditDialog() {
    this.changeItem=false;
    this.submitted = false;
  }

  handleEnter(event: KeyboardEvent): void {
    event.preventDefault(); // Prevents the default behavior of adding a new line in the textarea
    this.recipe.description += '\n';
  }

  onIngredientChange() {
    const index = this.recipe.ingredients.findIndex(o => o.id === this.itemToEdit.id);

    if(index!== -1 ) {
      this.recipe.ingredients[index]=this.itemToEdit;
    }

    //TODO  Actually change recipe
    this.itemToEdit= null;
    this.hideEditDialog();
  }


  protected readonly Unit = Unit;
  protected readonly clone = clone;
  protected readonly Object = Object;
}


