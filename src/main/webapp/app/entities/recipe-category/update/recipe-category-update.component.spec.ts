import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { RecipeCategoryService } from '../service/recipe-category.service';
import { IRecipeCategory, RecipeCategory } from '../recipe-category.model';

import { RecipeCategoryUpdateComponent } from './recipe-category-update.component';

describe('RecipeCategory Management Update Component', () => {
  let comp: RecipeCategoryUpdateComponent;
  let fixture: ComponentFixture<RecipeCategoryUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let recipeCategoryService: RecipeCategoryService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [RecipeCategoryUpdateComponent],
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
      .overrideTemplate(RecipeCategoryUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(RecipeCategoryUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    recipeCategoryService = TestBed.inject(RecipeCategoryService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const recipeCategory: IRecipeCategory = { id: 456 };

      activatedRoute.data = of({ recipeCategory });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(recipeCategory));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<RecipeCategory>>();
      const recipeCategory = { id: 123 };
      jest.spyOn(recipeCategoryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ recipeCategory });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: recipeCategory }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(recipeCategoryService.update).toHaveBeenCalledWith(recipeCategory);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<RecipeCategory>>();
      const recipeCategory = new RecipeCategory();
      jest.spyOn(recipeCategoryService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ recipeCategory });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: recipeCategory }));
      saveSubject.complete();

      // THEN
      expect(recipeCategoryService.create).toHaveBeenCalledWith(recipeCategory);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<RecipeCategory>>();
      const recipeCategory = { id: 123 };
      jest.spyOn(recipeCategoryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ recipeCategory });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(recipeCategoryService.update).toHaveBeenCalledWith(recipeCategory);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
