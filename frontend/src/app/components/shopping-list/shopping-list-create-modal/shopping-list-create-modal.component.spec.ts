import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShoppingListCreateModalComponent } from './shopping-list-create-modal.component';

describe('ShoppingListCreateModalComponent', () => {
  let component: ShoppingListCreateModalComponent;
  let fixture: ComponentFixture<ShoppingListCreateModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ShoppingListCreateModalComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ShoppingListCreateModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
