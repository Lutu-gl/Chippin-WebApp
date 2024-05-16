import { Component } from '@angular/core';
import {NgForOf, NgIf, SlicePipe} from "@angular/common";
import {RouterLink} from "@angular/router";
import {ItemListDetailDto} from "../../../dtos/itemlist";

@Component({
  selector: 'app-item-list-info-card-content',
  standalone: true,
  imports: [
    NgForOf,
    NgIf,
    SlicePipe,
    RouterLink
  ],
  templateUrl: './item-list-info-card-content.component.html',
  styleUrl: './item-list-info-card-content.component.scss'
})
export class ItemListInfoCardContentComponent {

  itemLists: ItemListDetailDto[] = [];
}
