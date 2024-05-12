import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditPantryItemDialogComponent } from './edit-pantry-item-dialog.component';

describe('EditPantryItemDialogComponent', () => {
  let component: EditPantryItemDialogComponent;
  let fixture: ComponentFixture<EditPantryItemDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditPantryItemDialogComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(EditPantryItemDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
