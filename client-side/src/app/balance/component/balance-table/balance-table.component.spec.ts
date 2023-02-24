import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BalanceTableComponent } from './balance-table.component';

describe('BalanceTableComponent', () => {
  let component: BalanceTableComponent;
  let fixture: ComponentFixture<BalanceTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BalanceTableComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BalanceTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
