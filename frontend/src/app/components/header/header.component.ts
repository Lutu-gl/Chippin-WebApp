import {AfterContentInit, AfterViewInit, Component, OnChanges, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {MenuItem} from "primeng/api";
import {NavigationEnd, Router} from "@angular/router";
import {filter} from "rxjs";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {
  email: string | undefined = "";
  menuitems: MenuItem[] | undefined;

  constructor(public authService: AuthService, private router: Router) {
  }

  ngOnInit() {
    this.menuitems = [
      {
        label: 'Settings',
        icon: 'pi pi-cog',
        routerLink: '/settings'
      },
      {
        label: 'Logout',
        icon: 'pi pi-power-off',
        command: () => {
          this.authService.logoutUser()
          this.router.navigate(['/login'])
        }
      }
    ]
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: NavigationEnd) => {
      this.email = this.authService.getEmail();
    })
  }

}
