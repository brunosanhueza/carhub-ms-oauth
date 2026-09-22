import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FilterData } from './filter-data';

describe('FilterData', () => {
  let component: FilterData;
  let fixture: ComponentFixture<FilterData>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FilterData],
    }).compileComponents();

    fixture = TestBed.createComponent(FilterData);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
