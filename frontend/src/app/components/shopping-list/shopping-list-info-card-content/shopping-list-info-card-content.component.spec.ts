import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShoppingListInfoCardContentComponent } from './shopping-list-info-card-content.component';

describe('ShoppingListInfoCardContentComponent', () => {
  let component: ShoppingListInfoCardContentComponent;
  let fixture: ComponentFixture<ShoppingListInfoCardContentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ShoppingListInfoCardContentComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ShoppingListInfoCardContentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
