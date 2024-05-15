import { Component } from '@angular/core';
import {NgForOf, NgIf, SlicePipe} from "@angular/common";
import {AuthService} from "../../../services/auth.service";
import {GroupService} from "../../../services/group.service";
import {ToastrService} from "ngx-toastr";
import {GroupListDto} from "../../../dtos/group";
import {RouterLink} from "@angular/router";

@Component({
  selector: 'app-group-info-card-content',
  standalone: true,
  imports: [
    NgForOf,
    NgIf,
    SlicePipe,
    RouterLink
  ],
  templateUrl: './group-info-card-content.component.html',
  styleUrl: './group-info-card-content.component.scss'
})
export class GroupInfoCardContentComponent {
  constructor(
    public authService: AuthService,
    private groupService: GroupService,
    private notification: ToastrService,
  ) { }
  groups: GroupListDto[] = [];

  ngOnInit(): void {
    if(this.authService.isLoggedIn()){
      this.groupService.getGroups().subscribe({
        next: data => {
          this.groups = data;
        },
        error: error => {
          this.printError(error);
        }
      });
    }
  }

  printError(error): void {
    if (error && error.error && error.error.errors) {
      for (let i = 0; i < error.error.errors.length; i++) {
        this.notification.error(`${error.error.errors[i]}`);
      }
    } else if (error && error.error && error.error.message) { // if no detailed error explanation exists. Give a more general one if available.
      this.notification.error(`${error.error.message}`);
    } else {
      console.error('Error', error);
      if(error.status !== 401) {
        const errorMessage = error.status === 0
          ? 'Is the backend up?'
          : error.message.message;
        this.notification.error(errorMessage, 'Could not connect to the server.');
      }
    }
  }

}
