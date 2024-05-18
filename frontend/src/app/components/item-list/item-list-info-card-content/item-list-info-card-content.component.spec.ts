import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ItemListInfoCardContentComponent } from './item-list-info-card-content.component';

describe('ItemListInfoCardContentComponent', () => {
  let component: ItemListInfoCardContentComponent;
  let fixture: ComponentFixture<ItemListInfoCardContentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ItemListInfoCardContentComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ItemListInfoCardContentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
