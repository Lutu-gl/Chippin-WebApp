import { ComponentFixture, TestBed } from '@angular/core/testing';

import {RecipeGlobalComponent} from "./recipe-global.component";

describe('RecipeEditComponent', () => {
  let component: RecipeGlobalComponent;
  let fixture: ComponentFixture<RecipeGlobalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RecipeGlobalComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(RecipeGlobalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
