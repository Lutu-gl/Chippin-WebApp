import {ComponentFixture, TestBed} from '@angular/core/testing';

import {AddShoppingListItemModalComponent} from './add-shopping-list-item-modal.component';

describe('AddShoppingListItemModalComponent', () => {
  let component: AddShoppingListItemModalComponent;
  let fixture: ComponentFixture<AddShoppingListItemModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddShoppingListItemModalComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(AddShoppingListItemModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
