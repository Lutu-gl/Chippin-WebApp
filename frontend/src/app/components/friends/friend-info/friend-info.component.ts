import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { lastValueFrom } from 'rxjs';
import { DebtService } from 'src/app/services/debt.service';
import { GroupService } from 'src/app/services/group.service';

interface FriendInfoDescription {
  groupId: number,
  groupName: string,
  debt: number
}

@Component({
  selector: 'app-friend-info',
  templateUrl: './friend-info.component.html',
  styleUrl: './friend-info.component.scss'
})
export class FriendInfoComponent implements OnInit {
  
  friendEmail: string;
  totalDebt: number = 0;
  detailedGroups: FriendInfoDescription[]

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private groupService: GroupService,
    private debtService: DebtService,
    private notification: ToastrService,
  ) {
  }
  
  async ngOnInit() {
    // todo check if actually friend
    this.friendEmail = this.route.snapshot.paramMap.get('email');
    try {
      const groups = await lastValueFrom(this.groupService.getGroups());
      const detailedGroups = [];
      for (let group of groups) {
        const detailedGroup = await lastValueFrom(this.groupService.getById(group.id));
        if (detailedGroup.members.some(member => member.email === this.friendEmail)) {
          const debt = await lastValueFrom(this.debtService.getDebtById(group.id));
          detailedGroups.push({
            groupId: detailedGroup.id,
            groupName: detailedGroup.groupName,
            debt: debt.membersDebts[this.friendEmail]
          });
        }
      }
      this.detailedGroups = detailedGroups;
      this.totalDebt = detailedGroups.map(group => group.debt).reduce((a,b) => a + b, 0);
    } catch (error) {
      console.error(error);
      this.notification.error("Could not get debts!");
    }
    
  }

}
