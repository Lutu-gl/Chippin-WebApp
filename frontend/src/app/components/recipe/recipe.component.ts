import {Component, OnInit} from "@angular/core";
import {RecipeListDto} from "../../dtos/recipe";


@Component({
  selector: 'app-recipe-list',
  templateUrl: './recipe.component.html',
  styleUrl: './recipe.component.scss'
})

export class RecipeComponent implements OnInit {
  recipes: RecipeListDto[] = [];

  ngOnInit(): void {
  }

}
