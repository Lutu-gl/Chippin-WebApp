import {Component, OnInit} from "@angular/core";
import {ItemListListDto} from "../../dtos/itemlist";

@Component({
  selector: 'app-item-list-list',
  templateUrl: './item-list.component.html',
  styleUrl: './item-list.component.scss'
})

export class ItemListComponent implements OnInit {
  itemLists: ItemListListDto[] = [];

  ngOnInit(): void {
  }

}
