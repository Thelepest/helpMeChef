import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { RecipeService } from '../service/recipe.service';
import { IRecipe, Recipe } from '../recipe.model';
import { IRecipeCategory } from 'app/entities/recipe-category/recipe-category.model';
import { RecipeCategoryService } from 'app/entities/recipe-category/service/recipe-category.service';
import { IIngredientQuantity } from 'app/entities/ingredient-quantity/ingredient-quantity.model';
import { IngredientQuantityService } from 'app/entities/ingredient-quantity/service/ingredient-quantity.service';

import { RecipeUpdateComponent } from './recipe-update.component';

describe('Recipe Management Update Component', () => {
  let comp: RecipeUpdateComponent;
  let fixture: ComponentFixture<RecipeUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let recipeService: RecipeService;
  let recipeCategoryService: RecipeCategoryService;
  let ingredientQuantityService: IngredientQuantityService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [RecipeUpdateComponent],
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
      .overrideTemplate(RecipeUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(RecipeUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    recipeService = TestBed.inject(RecipeService);
    recipeCategoryService = TestBed.inject(RecipeCategoryService);
    ingredientQuantityService = TestBed.inject(IngredientQuantityService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call RecipeCategory query and add missing value', () => {
      const recipe: IRecipe = { id: 456 };
      const recipecategory: IRecipeCategory = { id: 99991 };
      recipe.recipecategory = recipecategory;

      const recipeCategoryCollection: IRecipeCategory[] = [{ id: 9281 }];
      jest.spyOn(recipeCategoryService, 'query').mockReturnValue(of(new HttpResponse({ body: recipeCategoryCollection })));
      const additionalRecipeCategories = [recipecategory];
      const expectedCollection: IRecipeCategory[] = [...additionalRecipeCategories, ...recipeCategoryCollection];
      jest.spyOn(recipeCategoryService, 'addRecipeCategoryToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ recipe });
      comp.ngOnInit();

      expect(recipeCategoryService.query).toHaveBeenCalled();
      expect(recipeCategoryService.addRecipeCategoryToCollectionIfMissing).toHaveBeenCalledWith(
        recipeCategoryCollection,
        ...additionalRecipeCategories
      );
      expect(comp.recipeCategoriesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call IngredientQuantity query and add missing value', () => {
      const recipe: IRecipe = { id: 456 };
      const ingredientquantities: IIngredientQuantity[] = [{ id: 3295 }];
      recipe.ingredientquantities = ingredientquantities;

      const ingredientQuantityCollection: IIngredientQuantity[] = [{ id: 37897 }];
      jest.spyOn(ingredientQuantityService, 'query').mockReturnValue(of(new HttpResponse({ body: ingredientQuantityCollection })));
      const additionalIngredientQuantities = [...ingredientquantities];
      const expectedCollection: IIngredientQuantity[] = [...additionalIngredientQuantities, ...ingredientQuantityCollection];
      jest.spyOn(ingredientQuantityService, 'addIngredientQuantityToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ recipe });
      comp.ngOnInit();

      expect(ingredientQuantityService.query).toHaveBeenCalled();
      expect(ingredientQuantityService.addIngredientQuantityToCollectionIfMissing).toHaveBeenCalledWith(
        ingredientQuantityCollection,
        ...additionalIngredientQuantities
      );
      expect(comp.ingredientQuantitiesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const recipe: IRecipe = { id: 456 };
      const recipecategory: IRecipeCategory = { id: 46818 };
      recipe.recipecategory = recipecategory;
      const ingredientquantities: IIngredientQuantity = { id: 71555 };
      recipe.ingredientquantities = [ingredientquantities];

      activatedRoute.data = of({ recipe });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(recipe));
      expect(comp.recipeCategoriesSharedCollection).toContain(recipecategory);
      expect(comp.ingredientQuantitiesSharedCollection).toContain(ingredientquantities);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Recipe>>();
      const recipe = { id: 123 };
      jest.spyOn(recipeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ recipe });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: recipe }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(recipeService.update).toHaveBeenCalledWith(recipe);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Recipe>>();
      const recipe = new Recipe();
      jest.spyOn(recipeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ recipe });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: recipe }));
      saveSubject.complete();

      // THEN
      expect(recipeService.create).toHaveBeenCalledWith(recipe);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Recipe>>();
      const recipe = { id: 123 };
      jest.spyOn(recipeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ recipe });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(recipeService.update).toHaveBeenCalledWith(recipe);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackRecipeCategoryById', () => {
      it('Should return tracked RecipeCategory primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackRecipeCategoryById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackIngredientQuantityById', () => {
      it('Should return tracked IngredientQuantity primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackIngredientQuantityById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });

  describe('Getting selected relationships', () => {
    describe('getSelectedIngredientQuantity', () => {
      it('Should return option if no IngredientQuantity is selected', () => {
        const option = { id: 123 };
        const result = comp.getSelectedIngredientQuantity(option);
        expect(result === option).toEqual(true);
      });

      it('Should return selected IngredientQuantity for according option', () => {
        const option = { id: 123 };
        const selected = { id: 123 };
        const selected2 = { id: 456 };
        const result = comp.getSelectedIngredientQuantity(option, [selected2, selected]);
        expect(result === selected).toEqual(true);
        expect(result === selected2).toEqual(false);
        expect(result === option).toEqual(false);
      });

      it('Should return option if this IngredientQuantity is not selected', () => {
        const option = { id: 123 };
        const selected = { id: 456 };
        const result = comp.getSelectedIngredientQuantity(option, [selected]);
        expect(result === option).toEqual(true);
        expect(result === selected).toEqual(false);
      });
    });
  });
});
