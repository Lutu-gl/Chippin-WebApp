import { Component, Input, OnChanges, SimpleChanges,  } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { ActivityDetailDto } from 'src/app/dtos/activity';
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

  ngOnChanges(changes: SimpleChanges): void {
    if(this.activityType === ActivityType.expense) {
      if (changes['groupId'] && this.groupId) {
        this.activityService.getExpenseActivitiesFromGroup(this.groupId).subscribe({
          next: data => {
            this.expenseList = data;
          },
          error: error => {
            console.log(error);
            if (error && error.error && error.error.errors) {
              //this.notification.error(`${error.error.errors.join('. \n')}`);
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
    } else if(this.activityType === ActivityType.payment) {
      if (changes['groupId'] && this.groupId) {
        this.activityService.getPaymentActivitiesFromGroup(this.groupId).subscribe({
          next: data => {
            this.expenseList = data;
          },
          error: error => {
            console.log(error);
            if (error && error.error && error.error.errors) {
              //this.notification.error(`${error.error.errors.join('. \n')}`);
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
