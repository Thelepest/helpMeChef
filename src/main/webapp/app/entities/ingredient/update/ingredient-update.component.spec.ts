import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { IngredientService } from '../service/ingredient.service';
import { IIngredient, Ingredient } from '../ingredient.model';
import { IIngredientCategory } from 'app/entities/ingredient-category/ingredient-category.model';
import { IngredientCategoryService } from 'app/entities/ingredient-category/service/ingredient-category.service';

import { IngredientUpdateComponent } from './ingredient-update.component';

describe('Ingredient Management Update Component', () => {
  let comp: IngredientUpdateComponent;
  let fixture: ComponentFixture<IngredientUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let ingredientService: IngredientService;
  let ingredientCategoryService: IngredientCategoryService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [IngredientUpdateComponent],
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
      .overrideTemplate(IngredientUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(IngredientUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    ingredientService = TestBed.inject(IngredientService);
    ingredientCategoryService = TestBed.inject(IngredientCategoryService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call IngredientCategory query and add missing value', () => {
      const ingredient: IIngredient = { id: 456 };
      const ingredientcategory: IIngredientCategory = { id: 46490 };
      ingredient.ingredientcategory = ingredientcategory;

      const ingredientCategoryCollection: IIngredientCategory[] = [{ id: 32880 }];
      jest.spyOn(ingredientCategoryService, 'query').mockReturnValue(of(new HttpResponse({ body: ingredientCategoryCollection })));
      const additionalIngredientCategories = [ingredientcategory];
      const expectedCollection: IIngredientCategory[] = [...additionalIngredientCategories, ...ingredientCategoryCollection];
      jest.spyOn(ingredientCategoryService, 'addIngredientCategoryToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ ingredient });
      comp.ngOnInit();

      expect(ingredientCategoryService.query).toHaveBeenCalled();
      expect(ingredientCategoryService.addIngredientCategoryToCollectionIfMissing).toHaveBeenCalledWith(
        ingredientCategoryCollection,
        ...additionalIngredientCategories
      );
      expect(comp.ingredientCategoriesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Ingredient query and add missing value', () => {
      const ingredient: IIngredient = { id: 456 };
      const parent: IIngredient = { id: 51471 };
      ingredient.parent = parent;

      const ingredientCollection: IIngredient[] = [{ id: 6818 }];
      jest.spyOn(ingredientService, 'query').mockReturnValue(of(new HttpResponse({ body: ingredientCollection })));
      const additionalIngredients = [parent];
      const expectedCollection: IIngredient[] = [...additionalIngredients, ...ingredientCollection];
      jest.spyOn(ingredientService, 'addIngredientToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ ingredient });
      comp.ngOnInit();

      expect(ingredientService.query).toHaveBeenCalled();
      expect(ingredientService.addIngredientToCollectionIfMissing).toHaveBeenCalledWith(ingredientCollection, ...additionalIngredients);
      expect(comp.ingredientsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const ingredient: IIngredient = { id: 456 };
      const ingredientcategory: IIngredientCategory = { id: 57857 };
      ingredient.ingredientcategory = ingredientcategory;
      const parent: IIngredient = { id: 56188 };
      ingredient.parent = parent;

      activatedRoute.data = of({ ingredient });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(ingredient));
      expect(comp.ingredientCategoriesSharedCollection).toContain(ingredientcategory);
      expect(comp.ingredientsSharedCollection).toContain(parent);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Ingredient>>();
      const ingredient = { id: 123 };
      jest.spyOn(ingredientService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ingredient });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: ingredient }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(ingredientService.update).toHaveBeenCalledWith(ingredient);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Ingredient>>();
      const ingredient = new Ingredient();
      jest.spyOn(ingredientService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ingredient });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: ingredient }));
      saveSubject.complete();

      // THEN
      expect(ingredientService.create).toHaveBeenCalledWith(ingredient);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Ingredient>>();
      const ingredient = { id: 123 };
      jest.spyOn(ingredientService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ingredient });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(ingredientService.update).toHaveBeenCalledWith(ingredient);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackIngredientCategoryById', () => {
      it('Should return tracked IngredientCategory primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackIngredientCategoryById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackIngredientById', () => {
      it('Should return tracked Ingredient primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackIngredientById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
