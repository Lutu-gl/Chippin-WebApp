import {Component, OnChanges, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {MenuItem} from "primeng/api";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit, OnChanges {
  email: string | undefined;
  menuitems: MenuItem[] | undefined;

  constructor(public authService: AuthService) { }

  ngOnInit() {
    this.email = this.authService.getEmail();
    this.menuitems = [
      {
        label: 'Settings',
        icon: 'pi pi-cog',
        routerLink: '/settings'
      },
      {
        label: 'Logout',
        icon: 'pi pi-power-off',
        command: () => this.authService.logoutUser()
      }
    ]
  }

  ngOnChanges() {
    setTimeout(() => {
      this.email = this.authService.getEmail();
    }, 200)
  }

}
