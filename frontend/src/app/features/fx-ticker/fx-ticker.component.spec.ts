import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FxTickerComponent } from './fx-ticker.component';

describe('FxTickerComponent', () => {
  let component: FxTickerComponent;
  let fixture: ComponentFixture<FxTickerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FxTickerComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FxTickerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
