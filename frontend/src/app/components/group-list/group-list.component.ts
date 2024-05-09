import { Component, OnInit } from '@angular/core';
import {GroupService} from "../../services/group.service";
import {GroupDto} from "../../dtos/group";

@Component({
  selector: 'app-group-list',
  templateUrl: './group-list.component.html',
  styleUrl: './group-list.component.scss'
})
export class GroupListComponent implements OnInit {
  groups: GroupDto[] = [];

  constructor(private groupService: GroupService) { }

  ngOnInit(): void {
    this.groupService.getGroups().subscribe({
       next: data => {
         this.groups = data;
         console.log(this.groups);
       },
      error: error => {
        if (error && error.error && error.error.errors) {
          for (let i = 0; i < error.error.errors.length; i++) {
            console.error(`${error.error.errors[i]}`); // TODO this.notification.error(`${error.error.errors[i]}`); ?
          }
        } else if (error && error.error && error.error.message) { // if no detailed error explanation exists. Give a more general one if available.
          console.error(`${error.error.message}`) ; // TODO this.notification.error(`${error.error.message}`);
        } else {
          console.error('Error getting groups', error);
        }
      }
    });
  }
}
