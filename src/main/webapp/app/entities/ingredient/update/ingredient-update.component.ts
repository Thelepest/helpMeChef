import { Component, OnInit, ElementRef } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IIngredient, Ingredient } from '../ingredient.model';
import { IngredientService } from '../service/ingredient.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IIngredientCategory } from 'app/entities/ingredient-category/ingredient-category.model';
import { IngredientCategoryService } from 'app/entities/ingredient-category/service/ingredient-category.service';

@Component({
  selector: 'jhi-ingredient-update',
  templateUrl: './ingredient-update.component.html',
})
export class IngredientUpdateComponent implements OnInit {
  isSaving = false;

  ingredientCategoriesSharedCollection: IIngredientCategory[] = [];
  ingredientsSharedCollection: IIngredient[] = [];

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    image: [],
    imageContentType: [],
    ingredientcategory: [],
    parent: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected ingredientService: IngredientService,
    protected ingredientCategoryService: IngredientCategoryService,
    protected elementRef: ElementRef,
    protected activatedRoute: ActivatedRoute,
    protected fb: UntypedFormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ ingredient }) => {
      this.updateForm(ingredient);

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

  clearInputImage(field: string, fieldContentType: string, idInput: string): void {
    this.editForm.patchValue({
      [field]: null,
      [fieldContentType]: null,
    });
    if (idInput && this.elementRef.nativeElement.querySelector('#' + idInput)) {
      this.elementRef.nativeElement.querySelector('#' + idInput).value = null;
    }
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const ingredient = this.createFromForm();
    if (ingredient.id !== undefined) {
      this.subscribeToSaveResponse(this.ingredientService.update(ingredient));
    } else {
      this.subscribeToSaveResponse(this.ingredientService.create(ingredient));
    }
  }

  trackIngredientCategoryById(_index: number, item: IIngredientCategory): number {
    return item.id!;
  }

  trackIngredientById(_index: number, item: IIngredient): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IIngredient>>): void {
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

  protected updateForm(ingredient: IIngredient): void {
    this.editForm.patchValue({
      id: ingredient.id,
      name: ingredient.name,
      image: ingredient.image,
      imageContentType: ingredient.imageContentType,
      ingredientcategory: ingredient.ingredientcategory,
      parent: ingredient.parent,
    });

    this.ingredientCategoriesSharedCollection = this.ingredientCategoryService.addIngredientCategoryToCollectionIfMissing(
      this.ingredientCategoriesSharedCollection,
      ingredient.ingredientcategory
    );
    this.ingredientsSharedCollection = this.ingredientService.addIngredientToCollectionIfMissing(
      this.ingredientsSharedCollection,
      ingredient.parent
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

    this.ingredientService
      .query()
      .pipe(map((res: HttpResponse<IIngredient[]>) => res.body ?? []))
      .pipe(
        map((ingredients: IIngredient[]) =>
          this.ingredientService.addIngredientToCollectionIfMissing(ingredients, this.editForm.get('parent')!.value)
        )
      )
      .subscribe((ingredients: IIngredient[]) => (this.ingredientsSharedCollection = ingredients));
  }

  protected createFromForm(): IIngredient {
    return {
      ...new Ingredient(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      imageContentType: this.editForm.get(['imageContentType'])!.value,
      image: this.editForm.get(['image'])!.value,
      ingredientcategory: this.editForm.get(['ingredientcategory'])!.value,
      parent: this.editForm.get(['parent'])!.value,
    };
  }
}
