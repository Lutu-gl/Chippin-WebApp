import { Component, OnInit } from '@angular/core';
import {GroupService} from "../../services/group.service";
import {GroupListDto} from "../../dtos/group";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-group-list',
  templateUrl: './group-list.component.html',
  styleUrl: './group-list.component.scss'
})
export class GroupListComponent implements OnInit {
  groups: GroupListDto[] = [];

  constructor(
    private groupService: GroupService,
    private notification: ToastrService,
) { }

  ngOnInit(): void {
    this.groupService.getGroups().subscribe({
       next: data => {
         this.groups = data;
       },
      error: error => {
        if (error && error.error && error.error.errors) {
          for (let i = 0; i < error.error.errors.length; i++) {
            this.notification.error(`${error.error.errors[i]}`);
          }
        } else if (error && error.error && error.error.message) { // if no detailed error explanation exists. Give a more general one if available.
          this.notification.error(`${error.error.message}`);
        } else {
          console.error('Error getting groups', error);
        }
      }
    });
  }
}
