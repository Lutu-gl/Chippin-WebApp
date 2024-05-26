import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BudgetDto } from '../../../dtos/budget';
import { GroupService } from '../../../services/group.service';
import {Category} from '../../../dtos/category';

@Component({
  selector: 'app-budget-create',
  templateUrl: './budget-create.component.html',
  styleUrls: ['./budget-create.component.scss']
})
export class BudgetCreateComponent implements OnInit {
  newBudget: BudgetDto = { name: '', amount: undefined, category: Category.Other }; 
  groupId: number;
  categories = Category; 

  constructor(
    private groupService: GroupService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.groupId = Number(params.get('id'));
    });
  }

  addBudget(): void {
    if (this.newBudget.name && this.newBudget.amount > 0 && this.newBudget.category) {
      console.log(this.newBudget.category)
      this.groupService.createBudget(this.groupId, this.newBudget).subscribe({
        next: budget => {
          this.router.navigate(['/group', this.groupId, 'budgets']); 
        },
        error: error => {
          console.error('Error creating budget:', error);
        }
      });
    }
  }
}
