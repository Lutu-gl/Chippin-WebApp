import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GroupInfoCardContentComponent } from './group-info-card-content.component';

describe('GroupInfoCardContentComponent', () => {
  let component: GroupInfoCardContentComponent;
  let fixture: ComponentFixture<GroupInfoCardContentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GroupInfoCardContentComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(GroupInfoCardContentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
