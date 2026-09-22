import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VehiculoComponent } from './vehiculo-component';

describe('VehiculoComponent', () => {
  let component: VehiculoComponent;
  let fixture: ComponentFixture<VehiculoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VehiculoComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(VehiculoComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
