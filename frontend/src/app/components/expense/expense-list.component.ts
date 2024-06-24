import { Component, Input, OnChanges, SimpleChanges,  } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { ActivityDetailDto, ActivitySerachDto } from 'src/app/dtos/activity';
import { ActivityService } from 'src/app/services/activity.service';
export enum ActivityType {
  expense,
  payment,
}
@Component({
  selector: 'app-expense-list',
  templateUrl: './expense-list.component.html',
  styleUrl: './expense-list.component.scss'
})
export class ExpenseListComponent implements OnChanges {

  @Input() groupId!: number;
  @Input() activityType!: ActivityType;

  searchCriteria: ActivitySerachDto = {
    search: null,
    to: null,
    from: null
  };

  onSearchChange(): void {
    this.fetchExpenses();
  }

  onSearchChange2(): void {
    this.fetchPayments();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if(this.activityType === ActivityType.expense) {
      if (changes['groupId'] && this.groupId) {
        this.fetchExpenses();
      }
    } else if(this.activityType === ActivityType.payment) {
      if (changes['groupId'] && this.groupId) {
        this.fetchPayments();
      }
    }
  }

  fetchExpenses(): void {
    this.activityService.getExpenseActivitiesFromGroup(this.groupId, this.searchCriteria).subscribe({
      next: data => {
        this.expenseList = data;
      },
      error: error => {
        if (error && error.error && error.error.errors) {
          for (let i = 0; i < error.error.errors.length; i++) {
            this.notification.error(`${error.error.errors[i]}`);
          }
        } else if (error && error.error && error.error.message) { // if no detailed error explanation exists. Give a more general one if available.
          this.notification.error(`${error.error.message}`);
        } else if (error && error.error.detail) {
          this.notification.error(`${error.error.detail}`);
        } else {
          console.error('Error getting expense activities', error);
          this.notification.error("Could not get expense activities!");
        }
      }
    });
  }

  fetchPayments(): void {
    this.activityService.getPaymentActivitiesFromGroup(this.groupId, this.searchCriteria).subscribe({
      next: data => {
        this.expenseList = data;
      },
      error: error => {
        if (error && error.error && error.error.errors) {
          for (let i = 0; i < error.error.errors.length; i++) {
            this.notification.error(`${error.error.errors[i]}`);
          }
        } else if (error && error.error && error.error.message) { // if no detailed error explanation exists. Give a more general one if available.
          this.notification.error(`${error.error.message}`);
        } else if (error && error.error.detail) {
          this.notification.error(`${error.error.detail}`);
        } else {
          console.error('Error getting payment activities', error);
          this.notification.error("Could not get payment activities!");
        }
      }
    });
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

  expenseList: ActivityDetailDto[] = [];


  constructor(
    private activityService: ActivityService,
    private notification: ToastrService
  ) {
  }

  protected readonly ActivityType = ActivityType;

  heading() {
    if (ActivityType.expense === this.activityType) {
      return 'Expenses';
    } else if (ActivityType.payment === this.activityType) {
      return 'Payments';
    }
  }
}
