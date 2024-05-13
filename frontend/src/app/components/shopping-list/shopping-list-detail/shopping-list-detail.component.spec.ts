import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShoppingListDetailComponent } from './shopping-list-detail.component';

describe('ShoppingListDetailComponent', () => {
  let component: ShoppingListDetailComponent;
  let fixture: ComponentFixture<ShoppingListDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ShoppingListDetailComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ShoppingListDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
