import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { UncompatibleIRCategoryService } from '../service/uncompatible-ir-category.service';
import { IUncompatibleIRCategory, UncompatibleIRCategory } from '../uncompatible-ir-category.model';
import { IIngredientCategory } from 'app/entities/ingredient-category/ingredient-category.model';
import { IngredientCategoryService } from 'app/entities/ingredient-category/service/ingredient-category.service';
import { IRecipeCategory } from 'app/entities/recipe-category/recipe-category.model';
import { RecipeCategoryService } from 'app/entities/recipe-category/service/recipe-category.service';

import { UncompatibleIRCategoryUpdateComponent } from './uncompatible-ir-category-update.component';

describe('UncompatibleIRCategory Management Update Component', () => {
  let comp: UncompatibleIRCategoryUpdateComponent;
  let fixture: ComponentFixture<UncompatibleIRCategoryUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let uncompatibleIRCategoryService: UncompatibleIRCategoryService;
  let ingredientCategoryService: IngredientCategoryService;
  let recipeCategoryService: RecipeCategoryService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [UncompatibleIRCategoryUpdateComponent],
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
      .overrideTemplate(UncompatibleIRCategoryUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(UncompatibleIRCategoryUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    uncompatibleIRCategoryService = TestBed.inject(UncompatibleIRCategoryService);
    ingredientCategoryService = TestBed.inject(IngredientCategoryService);
    recipeCategoryService = TestBed.inject(RecipeCategoryService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call IngredientCategory query and add missing value', () => {
      const uncompatibleIRCategory: IUncompatibleIRCategory = { id: 456 };
      const ingredientcategory: IIngredientCategory = { id: 45050 };
      uncompatibleIRCategory.ingredientcategory = ingredientcategory;

      const ingredientCategoryCollection: IIngredientCategory[] = [{ id: 55377 }];
      jest.spyOn(ingredientCategoryService, 'query').mockReturnValue(of(new HttpResponse({ body: ingredientCategoryCollection })));
      const additionalIngredientCategories = [ingredientcategory];
      const expectedCollection: IIngredientCategory[] = [...additionalIngredientCategories, ...ingredientCategoryCollection];
      jest.spyOn(ingredientCategoryService, 'addIngredientCategoryToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ uncompatibleIRCategory });
      comp.ngOnInit();

      expect(ingredientCategoryService.query).toHaveBeenCalled();
      expect(ingredientCategoryService.addIngredientCategoryToCollectionIfMissing).toHaveBeenCalledWith(
        ingredientCategoryCollection,
        ...additionalIngredientCategories
      );
      expect(comp.ingredientCategoriesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call RecipeCategory query and add missing value', () => {
      const uncompatibleIRCategory: IUncompatibleIRCategory = { id: 456 };
      const recipecategory: IRecipeCategory = { id: 69548 };
      uncompatibleIRCategory.recipecategory = recipecategory;

      const recipeCategoryCollection: IRecipeCategory[] = [{ id: 28446 }];
      jest.spyOn(recipeCategoryService, 'query').mockReturnValue(of(new HttpResponse({ body: recipeCategoryCollection })));
      const additionalRecipeCategories = [recipecategory];
      const expectedCollection: IRecipeCategory[] = [...additionalRecipeCategories, ...recipeCategoryCollection];
      jest.spyOn(recipeCategoryService, 'addRecipeCategoryToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ uncompatibleIRCategory });
      comp.ngOnInit();

      expect(recipeCategoryService.query).toHaveBeenCalled();
      expect(recipeCategoryService.addRecipeCategoryToCollectionIfMissing).toHaveBeenCalledWith(
        recipeCategoryCollection,
        ...additionalRecipeCategories
      );
      expect(comp.recipeCategoriesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const uncompatibleIRCategory: IUncompatibleIRCategory = { id: 456 };
      const ingredientcategory: IIngredientCategory = { id: 69416 };
      uncompatibleIRCategory.ingredientcategory = ingredientcategory;
      const recipecategory: IRecipeCategory = { id: 77270 };
      uncompatibleIRCategory.recipecategory = recipecategory;

      activatedRoute.data = of({ uncompatibleIRCategory });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(uncompatibleIRCategory));
      expect(comp.ingredientCategoriesSharedCollection).toContain(ingredientcategory);
      expect(comp.recipeCategoriesSharedCollection).toContain(recipecategory);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<UncompatibleIRCategory>>();
      const uncompatibleIRCategory = { id: 123 };
      jest.spyOn(uncompatibleIRCategoryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ uncompatibleIRCategory });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: uncompatibleIRCategory }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(uncompatibleIRCategoryService.update).toHaveBeenCalledWith(uncompatibleIRCategory);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<UncompatibleIRCategory>>();
      const uncompatibleIRCategory = new UncompatibleIRCategory();
      jest.spyOn(uncompatibleIRCategoryService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ uncompatibleIRCategory });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: uncompatibleIRCategory }));
      saveSubject.complete();

      // THEN
      expect(uncompatibleIRCategoryService.create).toHaveBeenCalledWith(uncompatibleIRCategory);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<UncompatibleIRCategory>>();
      const uncompatibleIRCategory = { id: 123 };
      jest.spyOn(uncompatibleIRCategoryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ uncompatibleIRCategory });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(uncompatibleIRCategoryService.update).toHaveBeenCalledWith(uncompatibleIRCategory);
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

    describe('trackRecipeCategoryById', () => {
      it('Should return tracked RecipeCategory primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackRecipeCategoryById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
