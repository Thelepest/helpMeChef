import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { QuantityDetailComponent } from './quantity-detail.component';

describe('Quantity Management Detail Component', () => {
  let comp: QuantityDetailComponent;
  let fixture: ComponentFixture<QuantityDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [QuantityDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ quantity: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(QuantityDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(QuantityDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load quantity on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.quantity).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
