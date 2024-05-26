import { NgClass, NgForOf, NgIf } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ActivityService } from 'src/app/services/activity.service';

interface ExpenseInfo {
  id: number,
  description: string
}

@Component({
  selector: 'app-expense-info-card-content',
  standalone: true,
  imports: [
    NgForOf,
    NgIf,
    RouterLink,
    NgClass
  ],
  templateUrl: './expense-info-card-content.component.html',
  styleUrl: './expense-info-card-content.component.scss'
})
export class ExpenseInfoCardContentComponent implements OnInit {
  
  expenses: ExpenseInfo[] = [];

  constructor(
    private activityService: ActivityService
  ) {}
  
  ngOnInit(): void {
    this.activityService.getExpenseActivitiesByUser({search:null, from:null, to: null}).subscribe({
      next: data => {
        this.expenses = data.map(a => ({ id: a.expenseId, description: a.description }));
      },
      error: error => {
        console.error(error);
      }
    })
  }

}
