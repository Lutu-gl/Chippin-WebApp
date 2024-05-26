import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DisplayRecipesDialogComponent } from './display-recipes-dialog.component';

describe('DisplayRecipesDialogComponent', () => {
  let component: DisplayRecipesDialogComponent;
  let fixture: ComponentFixture<DisplayRecipesDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DisplayRecipesDialogComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(DisplayRecipesDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
