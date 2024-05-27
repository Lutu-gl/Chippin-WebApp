import {NgClass, NgForOf, NgIf, NgStyle} from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ActivityService } from 'src/app/services/activity.service';

interface ExpenseInfo {
  id: number,
  description: string,
  category: string,
}

@Component({
  selector: 'app-expense-info-card-content',
  standalone: true,
  imports: [
    NgForOf,
    NgIf,
    RouterLink,
    NgClass,
    NgStyle
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
        this.expenses = data.map(a => ({ id: a.expenseId, description: a.description, category: a.category }));
      },
      error: error => {
        console.error(error);
      }
    })
  }

  getBackgroundColor(color: string) {
    switch(color) {
      case "EXPENSE": return "#E0F7E0";
      case "EXPENSE_UPDATE": return "#FFEB99";
      case "EXPENSE_DELETE": return "#F7E0E0";
      case "EXPENSE_RECOVER": return "#E0F7E0";
      case "PAYMENT": return "#E0F7E0";
      case "PAYMENT_UPDATE": return "#FFEB99";
      case "PAYMENT_DELETE": return "#F7E0E0";
      case "PAYMENT_RECOVER": return "#E0F7E0";
      default: return "";
    }
  }

}
