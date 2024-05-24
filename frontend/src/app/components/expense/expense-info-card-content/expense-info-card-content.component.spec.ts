import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExpenseInfoCardContentComponent } from './expense-info-card-content.component';

describe('ExpenseInfoCardContentComponent', () => {
  let component: ExpenseInfoCardContentComponent;
  let fixture: ComponentFixture<ExpenseInfoCardContentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExpenseInfoCardContentComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ExpenseInfoCardContentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
