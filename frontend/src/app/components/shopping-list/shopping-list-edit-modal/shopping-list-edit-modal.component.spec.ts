import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShoppingListEditModalComponent } from './shopping-list-edit-modal.component';

describe('ShoppingListEditModalComponent', () => {
  let component: ShoppingListEditModalComponent;
  let fixture: ComponentFixture<ShoppingListEditModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ShoppingListEditModalComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ShoppingListEditModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
