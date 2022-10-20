import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ToolService } from '../service/tool.service';
import { ITool, Tool } from '../tool.model';
import { IRecipe } from 'app/entities/recipe/recipe.model';
import { RecipeService } from 'app/entities/recipe/service/recipe.service';

import { ToolUpdateComponent } from './tool-update.component';

describe('Tool Management Update Component', () => {
  let comp: ToolUpdateComponent;
  let fixture: ComponentFixture<ToolUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let toolService: ToolService;
  let recipeService: RecipeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ToolUpdateComponent],
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
      .overrideTemplate(ToolUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ToolUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    toolService = TestBed.inject(ToolService);
    recipeService = TestBed.inject(RecipeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Recipe query and add missing value', () => {
      const tool: ITool = { id: 456 };
      const recipes: IRecipe[] = [{ id: 5894 }];
      tool.recipes = recipes;

      const recipeCollection: IRecipe[] = [{ id: 35271 }];
      jest.spyOn(recipeService, 'query').mockReturnValue(of(new HttpResponse({ body: recipeCollection })));
      const additionalRecipes = [...recipes];
      const expectedCollection: IRecipe[] = [...additionalRecipes, ...recipeCollection];
      jest.spyOn(recipeService, 'addRecipeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ tool });
      comp.ngOnInit();

      expect(recipeService.query).toHaveBeenCalled();
      expect(recipeService.addRecipeToCollectionIfMissing).toHaveBeenCalledWith(recipeCollection, ...additionalRecipes);
      expect(comp.recipesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const tool: ITool = { id: 456 };
      const recipes: IRecipe = { id: 30208 };
      tool.recipes = [recipes];

      activatedRoute.data = of({ tool });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(tool));
      expect(comp.recipesSharedCollection).toContain(recipes);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Tool>>();
      const tool = { id: 123 };
      jest.spyOn(toolService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tool });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: tool }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(toolService.update).toHaveBeenCalledWith(tool);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Tool>>();
      const tool = new Tool();
      jest.spyOn(toolService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tool });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: tool }));
      saveSubject.complete();

      // THEN
      expect(toolService.create).toHaveBeenCalledWith(tool);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Tool>>();
      const tool = { id: 123 };
      jest.spyOn(toolService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tool });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(toolService.update).toHaveBeenCalledWith(tool);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackRecipeById', () => {
      it('Should return tracked Recipe primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackRecipeById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });

  describe('Getting selected relationships', () => {
    describe('getSelectedRecipe', () => {
      it('Should return option if no Recipe is selected', () => {
        const option = { id: 123 };
        const result = comp.getSelectedRecipe(option);
        expect(result === option).toEqual(true);
      });

      it('Should return selected Recipe for according option', () => {
        const option = { id: 123 };
        const selected = { id: 123 };
        const selected2 = { id: 456 };
        const result = comp.getSelectedRecipe(option, [selected2, selected]);
        expect(result === selected).toEqual(true);
        expect(result === selected2).toEqual(false);
        expect(result === option).toEqual(false);
      });

      it('Should return option if this Recipe is not selected', () => {
        const option = { id: 123 };
        const selected = { id: 456 };
        const result = comp.getSelectedRecipe(option, [selected]);
        expect(result === option).toEqual(true);
        expect(result === selected).toEqual(false);
      });
    });
  });
});
