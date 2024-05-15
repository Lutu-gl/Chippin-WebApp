import { Component, Input, OnChanges, SimpleChanges,  } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { ActivityDetailDto } from 'src/app/dtos/activity';
import { ActivityService } from 'src/app/services/activity.service';

@Component({
  selector: 'app-expense-list',
  templateUrl: './expense-list.component.html',
  styleUrl: './expense-list.component.scss'
})
export class ExpenseListComponent implements OnChanges {
  
  @Input() groupId!: number;

  expenseList: ActivityDetailDto[] = [];

  constructor(
    private activityService: ActivityService,
    private notification: ToastrService
  ) {
  }


  ngOnChanges(changes: SimpleChanges): void {
    if (changes['groupId'] && this.groupId) {
      this.activityService.getExpenseActivitiesFromGroup(this.groupId).subscribe({
        next: data => {
          this.expenseList = data;
        },
        error: error => {
          console.error(error);
          this.notification.error("Could not get expense activities!");
        }
      });
    }
  }

}
