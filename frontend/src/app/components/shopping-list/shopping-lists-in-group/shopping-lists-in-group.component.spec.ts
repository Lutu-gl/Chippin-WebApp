import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShoppingListsInGroupComponent } from './shopping-lists-in-group.component';

describe('ShoppingListsInGroupComponent', () => {
  let component: ShoppingListsInGroupComponent;
  let fixture: ComponentFixture<ShoppingListsInGroupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ShoppingListsInGroupComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ShoppingListsInGroupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
