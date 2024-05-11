import {Component, OnInit} from '@angular/core';
import {GroupService} from "../../../services/group.service";
import {ToastrService} from "ngx-toastr";
import {GroupDto} from "../../../dtos/group";
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
  }

  getGroup(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.service.getById(id)
      .subscribe(pGroup => {
        this.group = pGroup;
      });
  }

}
