import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FriendsInfoCardContentComponent } from './friends-info-card-content.component';

describe('FriendsInfoCardContentComponent', () => {
  let component: FriendsInfoCardContentComponent;
  let fixture: ComponentFixture<FriendsInfoCardContentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FriendsInfoCardContentComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(FriendsInfoCardContentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
