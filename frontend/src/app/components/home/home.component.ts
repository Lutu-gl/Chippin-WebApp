import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {GroupListDto} from "../../dtos/group";
import {GroupService} from "../../services/group.service";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

  constructor(
    public authService: AuthService,
    private groupService: GroupService) { }
  groups: GroupListDto[] = [];
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
