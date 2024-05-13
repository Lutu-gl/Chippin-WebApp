import {Component, OnInit} from '@angular/core';
import {GroupService} from "../../../services/group.service";
import {ToastrService} from "ngx-toastr";
import {GroupDto} from "../../../dtos/group";
import { BudgetDto } from '../../../dtos/budget';
import {ActivatedRoute, Router} from "@angular/router";

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

  constructor(
    private service: GroupService,
    private router: Router,
    private route: ActivatedRoute,
    private groupService: GroupService,
    private notification: ToastrService,
  ) {
  }

  ngOnInit(): void {
    this.getGroup();
    this.getGroupBudgets();
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


}
