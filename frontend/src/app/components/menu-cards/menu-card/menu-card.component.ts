import {Component, Input} from '@angular/core';
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-menu-card',
  standalone: true,
  imports: [
    NgIf
  ],
  templateUrl: './menu-card.component.html',
  styleUrl: './menu-card.component.scss'
})
export class MenuCardComponent {
  @Input() longCard: boolean = false;
}
