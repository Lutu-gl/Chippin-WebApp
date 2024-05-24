import { ComponentFixture, TestBed } from '@angular/core/testing';

import {RecipeLikedComponent} from "./recipe-liked.component";
import {RecipeService} from "../../../services/recipe.service";

describe('RecipeEditComponent', () => {
  let component: RecipeLikedComponent;
  let fixture: ComponentFixture<RecipeLikedComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RecipeService]
    })
      .compileComponents();

    fixture = TestBed.createComponent(RecipeLikedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
