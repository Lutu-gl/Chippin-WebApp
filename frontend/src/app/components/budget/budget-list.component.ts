import { Component, OnInit, Input } from '@angular/core';
import { GroupService } from '../../services/group.service';
import { BudgetDto } from '../../dtos/budget';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-budget-list',
  templateUrl: './budget-list.component.html',
  styleUrls: ['./budget-list.component.scss']
})
export class BudgetListComponent implements OnInit {
  @Input() groupId: number;
  budgets: BudgetDto[] = [];

  constructor(
    private groupService: GroupService,
    private route: ActivatedRoute,
  ) { }

  ngOnInit(): void {
    if (this.groupId === undefined) {
      this.route.paramMap.subscribe(params => {
        this.groupId = Number(params.get('id'));
        this.getGroupBudgets();
      });
    } else {
      this.getGroupBudgets();
    }
  }

  getGroupBudgets(): void {

    this.groupService.getGroupBudgets(this.groupId)
      .subscribe(budgets => {
        this.budgets = budgets;
      }, error => {
        console.error('Failed to load budgets', error);
      });
  }
}
