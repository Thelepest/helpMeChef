import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { IngredientQuantityService } from '../service/ingredient-quantity.service';
import { IIngredientQuantity, IngredientQuantity } from '../ingredient-quantity.model';
import { IIngredient } from 'app/entities/ingredient/ingredient.model';
import { IngredientService } from 'app/entities/ingredient/service/ingredient.service';
import { IQuantity } from 'app/entities/quantity/quantity.model';
import { QuantityService } from 'app/entities/quantity/service/quantity.service';

import { IngredientQuantityUpdateComponent } from './ingredient-quantity-update.component';

describe('IngredientQuantity Management Update Component', () => {
  let comp: IngredientQuantityUpdateComponent;
  let fixture: ComponentFixture<IngredientQuantityUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let ingredientQuantityService: IngredientQuantityService;
  let ingredientService: IngredientService;
  let quantityService: QuantityService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [IngredientQuantityUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(IngredientQuantityUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(IngredientQuantityUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    ingredientQuantityService = TestBed.inject(IngredientQuantityService);
    ingredientService = TestBed.inject(IngredientService);
    quantityService = TestBed.inject(QuantityService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Ingredient query and add missing value', () => {
      const ingredientQuantity: IIngredientQuantity = { id: 456 };
      const ingredient: IIngredient = { id: 5516 };
      ingredientQuantity.ingredient = ingredient;

      const ingredientCollection: IIngredient[] = [{ id: 75232 }];
      jest.spyOn(ingredientService, 'query').mockReturnValue(of(new HttpResponse({ body: ingredientCollection })));
      const additionalIngredients = [ingredient];
      const expectedCollection: IIngredient[] = [...additionalIngredients, ...ingredientCollection];
      jest.spyOn(ingredientService, 'addIngredientToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ ingredientQuantity });
      comp.ngOnInit();

      expect(ingredientService.query).toHaveBeenCalled();
      expect(ingredientService.addIngredientToCollectionIfMissing).toHaveBeenCalledWith(ingredientCollection, ...additionalIngredients);
      expect(comp.ingredientsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Quantity query and add missing value', () => {
      const ingredientQuantity: IIngredientQuantity = { id: 456 };
      const quantity: IQuantity = { id: 868 };
      ingredientQuantity.quantity = quantity;

      const quantityCollection: IQuantity[] = [{ id: 87700 }];
      jest.spyOn(quantityService, 'query').mockReturnValue(of(new HttpResponse({ body: quantityCollection })));
      const additionalQuantities = [quantity];
      const expectedCollection: IQuantity[] = [...additionalQuantities, ...quantityCollection];
      jest.spyOn(quantityService, 'addQuantityToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ ingredientQuantity });
      comp.ngOnInit();

      expect(quantityService.query).toHaveBeenCalled();
      expect(quantityService.addQuantityToCollectionIfMissing).toHaveBeenCalledWith(quantityCollection, ...additionalQuantities);
      expect(comp.quantitiesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const ingredientQuantity: IIngredientQuantity = { id: 456 };
      const ingredient: IIngredient = { id: 68638 };
      ingredientQuantity.ingredient = ingredient;
      const quantity: IQuantity = { id: 55168 };
      ingredientQuantity.quantity = quantity;

      activatedRoute.data = of({ ingredientQuantity });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(ingredientQuantity));
      expect(comp.ingredientsSharedCollection).toContain(ingredient);
      expect(comp.quantitiesSharedCollection).toContain(quantity);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IngredientQuantity>>();
      const ingredientQuantity = { id: 123 };
      jest.spyOn(ingredientQuantityService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ingredientQuantity });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: ingredientQuantity }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(ingredientQuantityService.update).toHaveBeenCalledWith(ingredientQuantity);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IngredientQuantity>>();
      const ingredientQuantity = new IngredientQuantity();
      jest.spyOn(ingredientQuantityService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ingredientQuantity });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: ingredientQuantity }));
      saveSubject.complete();

      // THEN
      expect(ingredientQuantityService.create).toHaveBeenCalledWith(ingredientQuantity);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IngredientQuantity>>();
      const ingredientQuantity = { id: 123 };
      jest.spyOn(ingredientQuantityService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ingredientQuantity });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(ingredientQuantityService.update).toHaveBeenCalledWith(ingredientQuantity);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackIngredientById', () => {
      it('Should return tracked Ingredient primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackIngredientById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackQuantityById', () => {
      it('Should return tracked Quantity primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackQuantityById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
