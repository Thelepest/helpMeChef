import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IUncompatibleIRCategory, UncompatibleIRCategory } from '../uncompatible-ir-category.model';
import { UncompatibleIRCategoryService } from '../service/uncompatible-ir-category.service';
import { IIngredientCategory } from 'app/entities/ingredient-category/ingredient-category.model';
import { IngredientCategoryService } from 'app/entities/ingredient-category/service/ingredient-category.service';
import { IRecipeCategory } from 'app/entities/recipe-category/recipe-category.model';
import { RecipeCategoryService } from 'app/entities/recipe-category/service/recipe-category.service';

@Component({
  selector: 'jhi-uncompatible-ir-category-update',
  templateUrl: './uncompatible-ir-category-update.component.html',
})
export class UncompatibleIRCategoryUpdateComponent implements OnInit {
  isSaving = false;

  ingredientCategoriesSharedCollection: IIngredientCategory[] = [];
  recipeCategoriesSharedCollection: IRecipeCategory[] = [];

  editForm = this.fb.group({
    id: [],
    ingredientcategory: [],
    recipecategory: [],
  });

  constructor(
    protected uncompatibleIRCategoryService: UncompatibleIRCategoryService,
    protected ingredientCategoryService: IngredientCategoryService,
    protected recipeCategoryService: RecipeCategoryService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ uncompatibleIRCategory }) => {
      this.updateForm(uncompatibleIRCategory);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const uncompatibleIRCategory = this.createFromForm();
    if (uncompatibleIRCategory.id !== undefined) {
      this.subscribeToSaveResponse(this.uncompatibleIRCategoryService.update(uncompatibleIRCategory));
    } else {
      this.subscribeToSaveResponse(this.uncompatibleIRCategoryService.create(uncompatibleIRCategory));
    }
  }

  trackIngredientCategoryById(_index: number, item: IIngredientCategory): number {
    return item.id!;
  }

  trackRecipeCategoryById(_index: number, item: IRecipeCategory): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IUncompatibleIRCategory>>): void {
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

  protected updateForm(uncompatibleIRCategory: IUncompatibleIRCategory): void {
    this.editForm.patchValue({
      id: uncompatibleIRCategory.id,
      ingredientcategory: uncompatibleIRCategory.ingredientcategory,
      recipecategory: uncompatibleIRCategory.recipecategory,
    });

    this.ingredientCategoriesSharedCollection = this.ingredientCategoryService.addIngredientCategoryToCollectionIfMissing(
      this.ingredientCategoriesSharedCollection,
      uncompatibleIRCategory.ingredientcategory
    );
    this.recipeCategoriesSharedCollection = this.recipeCategoryService.addRecipeCategoryToCollectionIfMissing(
      this.recipeCategoriesSharedCollection,
      uncompatibleIRCategory.recipecategory
    );
  }

  protected loadRelationshipsOptions(): void {
    this.ingredientCategoryService
      .query()
      .pipe(map((res: HttpResponse<IIngredientCategory[]>) => res.body ?? []))
      .pipe(
        map((ingredientCategories: IIngredientCategory[]) =>
          this.ingredientCategoryService.addIngredientCategoryToCollectionIfMissing(
            ingredientCategories,
            this.editForm.get('ingredientcategory')!.value
          )
        )
      )
      .subscribe((ingredientCategories: IIngredientCategory[]) => (this.ingredientCategoriesSharedCollection = ingredientCategories));

    this.recipeCategoryService
      .query()
      .pipe(map((res: HttpResponse<IRecipeCategory[]>) => res.body ?? []))
      .pipe(
        map((recipeCategories: IRecipeCategory[]) =>
          this.recipeCategoryService.addRecipeCategoryToCollectionIfMissing(recipeCategories, this.editForm.get('recipecategory')!.value)
        )
      )
      .subscribe((recipeCategories: IRecipeCategory[]) => (this.recipeCategoriesSharedCollection = recipeCategories));
  }

  protected createFromForm(): IUncompatibleIRCategory {
    return {
      ...new UncompatibleIRCategory(),
      id: this.editForm.get(['id'])!.value,
      ingredientcategory: this.editForm.get(['ingredientcategory'])!.value,
      recipecategory: this.editForm.get(['recipecategory'])!.value,
    };
  }
}
