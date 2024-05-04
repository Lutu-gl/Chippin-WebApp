import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {PantryService} from "../../services/pantry.service";
import {ItemDetailDto, Unit} from "../../dtos/item";
import {KeyValuePipe, NgForOf, NgIf} from "@angular/common";
import {FormsModule} from "@angular/forms";

@Component({
  selector: 'app-pantry',
  standalone: true,
  imports: [
    NgForOf,
    KeyValuePipe,
    NgIf,
    FormsModule
  ],
  templateUrl: './pantry.component.html',
  styleUrl: './pantry.component.scss'
})
export class PantryComponent implements OnInit {

  items: ItemDetailDto[];

  constructor(
    private route: ActivatedRoute,
    private service: PantryService
  ) {
  }

  ngOnInit(): void {
    this.route.data.subscribe();

    this.route.params.subscribe(params => {
        let id: number = +params['id'];
        this.service.getPantryById(id).subscribe(p => {
          this.items = p.items;
          console.log('Success!');
        }, error => {
          console.error('Error loading pantry data from backend: ', error);
        })
      },
      error => {
        console.error('Error: ', error);
      })
  }

  protected readonly Unit = Unit;
}
