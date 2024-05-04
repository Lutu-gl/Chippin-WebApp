import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {PantryService} from "../../services/pantry.service";
import {ItemDetailDto, Unit} from "../../dtos/item";
import {KeyValuePipe, NgForOf, NgIf} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {debounceTime, Subject} from "rxjs";
import {PantrySearch} from "../../dtos/pantry";

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
  newItem: ItemDetailDto = {
    amount: 0,
    unit: Unit.Piece,
    description: ""
  };
  searchString: string = "";
  searchChangedObservable = new Subject<void>();
  id: number;

  constructor(
    private route: ActivatedRoute,
    private service: PantryService
  ) {
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
        this.id = +params['id'];
        this.service.getPantryById(this.id).subscribe(p => {
          this.items = p.items;
        }, error => {
          console.error('Error loading pantry data from backend: ', error);
        })
      },
      error => {
        console.error('Error: ', error);
      })
    this.searchChangedObservable
      .pipe(debounceTime(300))
      .subscribe({next: () => this.filterPantry()});
  }

  filterPantry() {
    console.log('Success!');
    let search: PantrySearch = {
      details: this.searchString
    };

    this.service.filterPantry(this.id, search).subscribe(p => {
      this.items = p.items;
    })
  }

  searchChanged() {
    this.searchChangedObservable.next();
  }

  protected readonly Unit = Unit;
}
