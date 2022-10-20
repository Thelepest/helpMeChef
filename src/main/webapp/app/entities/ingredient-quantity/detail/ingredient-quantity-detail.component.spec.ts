import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { IngredientQuantityDetailComponent } from './ingredient-quantity-detail.component';

describe('IngredientQuantity Management Detail Component', () => {
  let comp: IngredientQuantityDetailComponent;
  let fixture: ComponentFixture<IngredientQuantityDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [IngredientQuantityDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ ingredientQuantity: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(IngredientQuantityDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(IngredientQuantityDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load ingredientQuantity on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.ingredientQuantity).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
