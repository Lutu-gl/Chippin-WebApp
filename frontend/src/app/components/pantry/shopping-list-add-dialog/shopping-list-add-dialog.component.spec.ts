import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShoppingListAddDialogComponent } from './shopping-list-add-dialog.component';

describe('ShoppingListAddDialogComponent', () => {
  let component: ShoppingListAddDialogComponent;
  let fixture: ComponentFixture<ShoppingListAddDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ShoppingListAddDialogComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ShoppingListAddDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
