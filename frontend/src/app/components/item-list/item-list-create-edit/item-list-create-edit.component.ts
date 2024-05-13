import {Component, OnInit} from "@angular/core";

export enum ItemListCreateEditMode {
  create,
  edit,

}
@Component({
  selector: 'app-item-list-create-edit',
  templateUrl: './item-list-create-edit.component.html',
  styleUrl: './item-list-create-edit.component.scss'
})

export class ItemListCreateEditComponent implements OnInit {
  ngOnInit(): void {
  }

}
