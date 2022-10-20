import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { IngredientCategoryService } from '../service/ingredient-category.service';
import { IIngredientCategory, IngredientCategory } from '../ingredient-category.model';

import { IngredientCategoryUpdateComponent } from './ingredient-category-update.component';

describe('IngredientCategory Management Update Component', () => {
  let comp: IngredientCategoryUpdateComponent;
  let fixture: ComponentFixture<IngredientCategoryUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let ingredientCategoryService: IngredientCategoryService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [IngredientCategoryUpdateComponent],
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
      .overrideTemplate(IngredientCategoryUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(IngredientCategoryUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    ingredientCategoryService = TestBed.inject(IngredientCategoryService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const ingredientCategory: IIngredientCategory = { id: 456 };

      activatedRoute.data = of({ ingredientCategory });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(ingredientCategory));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IngredientCategory>>();
      const ingredientCategory = { id: 123 };
      jest.spyOn(ingredientCategoryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ingredientCategory });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: ingredientCategory }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(ingredientCategoryService.update).toHaveBeenCalledWith(ingredientCategory);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IngredientCategory>>();
      const ingredientCategory = new IngredientCategory();
      jest.spyOn(ingredientCategoryService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ingredientCategory });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: ingredientCategory }));
      saveSubject.complete();

      // THEN
      expect(ingredientCategoryService.create).toHaveBeenCalledWith(ingredientCategory);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IngredientCategory>>();
      const ingredientCategory = { id: 123 };
      jest.spyOn(ingredientCategoryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ingredientCategory });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(ingredientCategoryService.update).toHaveBeenCalledWith(ingredientCategory);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
