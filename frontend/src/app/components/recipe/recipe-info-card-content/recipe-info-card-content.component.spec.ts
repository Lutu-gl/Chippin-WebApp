import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RecipeInfoCardContentComponent } from './recipe-info-card-content.component';

describe('RecipeInfoCardContentComponent', () => {
  let component: RecipeInfoCardContentComponent;
  let fixture: ComponentFixture<RecipeInfoCardContentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RecipeInfoCardContentComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(RecipeInfoCardContentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
