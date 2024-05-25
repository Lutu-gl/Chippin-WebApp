import {ComponentFixture, TestBed} from '@angular/core/testing';

import {EditShoppingListItemModalComponent} from './edit-shopping-list-item-modal.component';

describe('EditShoppingListItemModalComponent', () => {
  let component: EditShoppingListItemModalComponent;
  let fixture: ComponentFixture<EditShoppingListItemModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditShoppingListItemModalComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(EditShoppingListItemModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
