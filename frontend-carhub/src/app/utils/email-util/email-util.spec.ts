import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmailUtil } from './email-util';

describe('EmailUtil', () => {
  let component: EmailUtil;
  let fixture: ComponentFixture<EmailUtil>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EmailUtil],
    }).compileComponents();

    fixture = TestBed.createComponent(EmailUtil);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
