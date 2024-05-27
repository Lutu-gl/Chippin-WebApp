import {Component, OnInit} from '@angular/core';
import {GroupService} from "../../../services/group.service";
import {ToastrService} from "ngx-toastr";
import {GroupDto} from "../../../dtos/group";
import { BudgetDto } from '../../../dtos/budget';
import {ActivatedRoute, Router} from "@angular/router";
import {DebtGroupDetailDto} from "../../../dtos/debt";
import {DebtService} from "../../../services/debt.service";
import {ActivityType} from "../../expense/expense-list.component";

@Component({
  selector: 'app-group-info',
  templateUrl: './group-info.component.html',
  styleUrl: './group-info.component.scss'
})
export class GroupInfoComponent implements OnInit {

  group: GroupDto = {
    groupName: '',
    members: []
  };

  budgets: BudgetDto[] = []
  debt: DebtGroupDetailDto

  constructor(
    private service: GroupService,
    private debtService: DebtService,
    private router: Router,
    private route: ActivatedRoute,
    private groupService: GroupService,
    private notification: ToastrService,
  ) {
  }

  ngOnInit(): void {
    this.getGroup();
    this.getGroupBudgets();
    this.getDebt();
  }

  getGroup(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.service.getById(id)
      .subscribe(pGroup => {
        this.group = pGroup;
      });
  }

  getGroupBudgets(): void{
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.service.getGroupBudgets(id)
      .subscribe(budgets =>{
        this.budgets = budgets;
      })
  }

  getDebt(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.debtService.getDebtById(id)
      .subscribe(debt => {
        this.debt = debt;
        console.log(debt);
      });
  }

  objectKeys(obj: any): string[] {
    return Object.keys(obj).sort();
  }

  getSortedMembers(): any[] {
    return this.group.members.sort((a, b) => a.email.localeCompare(b.email));
  }

  getBorderColor(value: number): string {
    return value > 0 ? 'green' : 'red';
  }

    protected readonly ActivityType = ActivityType;
  protected readonly parseFloat = parseFloat;
}
