import { Component, OnInit } from '@angular/core';
import {GroupService} from "../../services/group.service";
import {GroupDetailDto, GroupListDto} from "../../dtos/group";
import {ToastrService} from "ngx-toastr";
import {MessageService} from "primeng/api";

@Component({
  selector: 'app-group-list',
  templateUrl: './group-list.component.html',
  styleUrl: './group-list.component.scss'
})
export class GroupListComponent implements OnInit {
  groups: GroupListDto[] = [];
  // map of group id to balance summary
  groupBalanceSummaries: { [p: string]: number } = {};
  constructor(
    private groupService: GroupService,
    private messageService: MessageService,
) { }

  ngOnInit(): void {
    this.groupService.getGroupsWithDebtInfos().subscribe({
       next: data => {
         this.groups = data.sort((a, b) => a.groupName.localeCompare(b.groupName))
          this.groups.forEach(group => {
            this.groupBalanceSummaries[group.id] = this.calculateBalanceSummary(group.membersDebts);
          });
       },
      error: error => {
        if (error && error.error && error.error.errors) {
          for (let i = 0; i < error.error.errors.length; i++) {
            this.messageService.add({severity:'error', summary:'Error', detail:`${error.error.errors[i]}`});
          }
        } else if (error && error.error && error.error.message) {
          this.messageService.add({severity:'error', summary:'Error', detail:`${error.error.message}`});
        } else if (error && error.error && error.error.detail) {
          this.messageService.add({severity:'error', summary:'Error', detail:`${error.error.detail}`});
        } else {
          console.error('Error getting group', error);
          this.messageService.add({severity:'error', summary:'Error', detail:`Getting group did not work!`});
        }
      }
    });
  }

  protected calculateBalanceSummary(membersDebts: { [p: string]: number }
  ): number {
    let balanceSummary = 0;
    for (let membersDebtsKey in membersDebts) {
      let memberDebt = membersDebts[membersDebtsKey];
      if (memberDebt) {
        balanceSummary += memberDebt;
      }
    }
    return balanceSummary;
  }
}
