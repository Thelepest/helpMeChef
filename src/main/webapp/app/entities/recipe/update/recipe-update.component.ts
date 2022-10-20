import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IRecipe, Recipe } from '../recipe.model';
import { RecipeService } from '../service/recipe.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IRecipeCategory } from 'app/entities/recipe-category/recipe-category.model';
import { RecipeCategoryService } from 'app/entities/recipe-category/service/recipe-category.service';
import { IIngredientQuantity } from 'app/entities/ingredient-quantity/ingredient-quantity.model';
import { IngredientQuantityService } from 'app/entities/ingredient-quantity/service/ingredient-quantity.service';

@Component({
  selector: 'jhi-recipe-update',
  templateUrl: './recipe-update.component.html',
})
export class RecipeUpdateComponent implements OnInit {
  isSaving = false;

  recipeCategoriesSharedCollection: IRecipeCategory[] = [];
  ingredientQuantitiesSharedCollection: IIngredientQuantity[] = [];

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    time: [],
    description: [null, [Validators.required]],
    recipecategory: [],
    ingredientquantities: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected recipeService: RecipeService,
    protected recipeCategoryService: RecipeCategoryService,
    protected ingredientQuantityService: IngredientQuantityService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ recipe }) => {
      this.updateForm(recipe);

      this.loadRelationshipsOptions();
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('helpMeChefApp.error', { ...err, key: 'error.file.' + err.key })),
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const recipe = this.createFromForm();
    if (recipe.id !== undefined) {
      this.subscribeToSaveResponse(this.recipeService.update(recipe));
    } else {
      this.subscribeToSaveResponse(this.recipeService.create(recipe));
    }
  }

  trackRecipeCategoryById(_index: number, item: IRecipeCategory): number {
    return item.id!;
  }

  trackIngredientQuantityById(_index: number, item: IIngredientQuantity): number {
    return item.id!;
  }

  getSelectedIngredientQuantity(option: IIngredientQuantity, selectedVals?: IIngredientQuantity[]): IIngredientQuantity {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IRecipe>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(recipe: IRecipe): void {
    this.editForm.patchValue({
      id: recipe.id,
      name: recipe.name,
      time: recipe.time,
      description: recipe.description,
      recipecategory: recipe.recipecategory,
      ingredientquantities: recipe.ingredientquantities,
    });

    this.recipeCategoriesSharedCollection = this.recipeCategoryService.addRecipeCategoryToCollectionIfMissing(
      this.recipeCategoriesSharedCollection,
      recipe.recipecategory
    );
    this.ingredientQuantitiesSharedCollection = this.ingredientQuantityService.addIngredientQuantityToCollectionIfMissing(
      this.ingredientQuantitiesSharedCollection,
      ...(recipe.ingredientquantities ?? [])
    );
  }

  protected loadRelationshipsOptions(): void {
    this.recipeCategoryService
      .query()
      .pipe(map((res: HttpResponse<IRecipeCategory[]>) => res.body ?? []))
      .pipe(
        map((recipeCategories: IRecipeCategory[]) =>
          this.recipeCategoryService.addRecipeCategoryToCollectionIfMissing(recipeCategories, this.editForm.get('recipecategory')!.value)
        )
      )
      .subscribe((recipeCategories: IRecipeCategory[]) => (this.recipeCategoriesSharedCollection = recipeCategories));

    this.ingredientQuantityService
      .query()
      .pipe(map((res: HttpResponse<IIngredientQuantity[]>) => res.body ?? []))
      .pipe(
        map((ingredientQuantities: IIngredientQuantity[]) =>
          this.ingredientQuantityService.addIngredientQuantityToCollectionIfMissing(
            ingredientQuantities,
            ...(this.editForm.get('ingredientquantities')!.value ?? [])
          )
        )
      )
      .subscribe((ingredientQuantities: IIngredientQuantity[]) => (this.ingredientQuantitiesSharedCollection = ingredientQuantities));
  }

  protected createFromForm(): IRecipe {
    return {
      ...new Recipe(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      time: this.editForm.get(['time'])!.value,
      description: this.editForm.get(['description'])!.value,
      recipecategory: this.editForm.get(['recipecategory'])!.value,
      ingredientquantities: this.editForm.get(['ingredientquantities'])!.value,
    };
  }
}
