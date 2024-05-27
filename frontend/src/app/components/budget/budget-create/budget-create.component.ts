import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BudgetDto } from '../../../dtos/budget';
import { GroupService } from '../../../services/group.service';
import {Category} from '../../../dtos/category';
import { Observable, of } from 'rxjs';
import { ToastrService } from 'ngx-toastr';


export enum BudgetCreateEditMode {
  create,
  edit,
  info
}

@Component({
  selector: 'app-budget-create',
  templateUrl: './budget-create.component.html',
  styleUrls: ['./budget-create.component.scss']
})
export class BudgetCreateComponent implements OnInit {
  mode: BudgetCreateEditMode = BudgetCreateEditMode.create;
  newBudget: BudgetDto = { name: '', amount: undefined, category: Category, alreadySpent: 0 }; 
  groupId: number;
  categories = Category; 
  budgetId: number;

  constructor(
    private groupService: GroupService,
    private route: ActivatedRoute,
    private router: Router,
    private notification: ToastrService
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.groupId = Number(params.get('id'));
    });

    this.route.data.subscribe(data => {
      this.mode = data.mode;
      if (this.mode === BudgetCreateEditMode.create) {
        console.log("create mode")
        // this.prepareGroupOnCreate();
      } else if (this.mode === BudgetCreateEditMode.info || this.mode === BudgetCreateEditMode.edit) {
        console.log("info mode")
        this.prepareBudget();
      }
    
    });
  }

  private prepareBudget(): void{
    this.budgetId = Number(this.route.snapshot.paramMap.get('budgetId'))

    this.groupService.getByBudgetId(this.groupId, this.budgetId).subscribe({
      next: data =>{
        console.log("data:")
        console.log(data);
        // this.newBudget.name = data.name;
        // this.newBudget.alreadySpend = data.alreadySpend;
        // this.newBudget.category = data.category;
        // this.newBudget.amount = data.amount;
        this.newBudget = data;
      },
      error: error =>{
        console.error(error);
        this.notification.error("Could not get Budget");
      }
    })

  }

  public get heading(): string {
    switch (this.mode) {
      case BudgetCreateEditMode.create:
        return "Create new Budget";
      case BudgetCreateEditMode.edit:
        return "Budget expense";
      case BudgetCreateEditMode.info:
        return "Budet Details";
      default:
        return '?';
    }
  }
  
  public modeIsCreate(): boolean {
    return this.mode === BudgetCreateEditMode.create;
  }

  public modeIsInfo(): boolean {
    return this.mode === BudgetCreateEditMode.info;
  }


  formatCategory(category: Category): string {
    return category ? Category[category] : '';
  }

  categorySelected(category: Category): void {
    if (!category) {
      this.newBudget.category = undefined;
    } else {
      this.newBudget.category = category;
    }
  }

  deleteExistingExpense(): void{

    this.groupService.deleteBudget(this.groupId, this.budgetId).subscribe({
      next: () => {
        this.notification.success("Budget deleted successfully!");
        this.router.navigate(['/group', this.groupId]); 
      },
      error: error => {
        console.error('Error deleting budget:', error);
        this.notification.error("Could not delete budget!");
      }
    })

  }
  
  categorySuggestions = (input: string): Observable<Category[]> =>
    of(Object.values(Category));

  addBudget(): void {
    if (this.newBudget.name && this.newBudget.amount > 0 && this.newBudget.category) {
      this.groupService.createBudget(this.groupId, this.newBudget).subscribe({
        next: budget => {
          this.notification.success("Created budget successfully!");
          this.router.navigate(['/group', this.groupId]); 
        },
        error: error => {
          console.error('Error creating budget:', error);
          this.notification.error("Could not create budget!");
        }
      });
    }
  }
}
