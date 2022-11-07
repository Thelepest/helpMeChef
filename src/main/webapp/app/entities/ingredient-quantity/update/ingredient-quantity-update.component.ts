import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IIngredientQuantity, IngredientQuantity } from '../ingredient-quantity.model';
import { IngredientQuantityService } from '../service/ingredient-quantity.service';
import { IIngredient } from 'app/entities/ingredient/ingredient.model';
import { IngredientService } from 'app/entities/ingredient/service/ingredient.service';
import { IQuantity } from 'app/entities/quantity/quantity.model';
import { QuantityService } from 'app/entities/quantity/service/quantity.service';

@Component({
  selector: 'jhi-ingredient-quantity-update',
  templateUrl: './ingredient-quantity-update.component.html',
})
export class IngredientQuantityUpdateComponent implements OnInit {
  isSaving = false;

  ingredientsSharedCollection: IIngredient[] = [];
  quantitiesSharedCollection: IQuantity[] = [];

  editForm = this.fb.group({
    id: [],
    ingredient: [],
    quantity: [],
  });

  constructor(
    protected ingredientQuantityService: IngredientQuantityService,
    protected ingredientService: IngredientService,
    protected quantityService: QuantityService,
    protected activatedRoute: ActivatedRoute,
    protected fb: UntypedFormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ ingredientQuantity }) => {
      this.updateForm(ingredientQuantity);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const ingredientQuantity = this.createFromForm();
    if (ingredientQuantity.id !== undefined) {
      this.subscribeToSaveResponse(this.ingredientQuantityService.update(ingredientQuantity));
    } else {
      this.subscribeToSaveResponse(this.ingredientQuantityService.create(ingredientQuantity));
    }
  }

  trackIngredientById(_index: number, item: IIngredient): number {
    return item.id!;
  }

  trackQuantityById(_index: number, item: IQuantity): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IIngredientQuantity>>): void {
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

  protected updateForm(ingredientQuantity: IIngredientQuantity): void {
    this.editForm.patchValue({
      id: ingredientQuantity.id,
      ingredient: ingredientQuantity.ingredient,
      quantity: ingredientQuantity.quantity,
    });

    this.ingredientsSharedCollection = this.ingredientService.addIngredientToCollectionIfMissing(
      this.ingredientsSharedCollection,
      ingredientQuantity.ingredient
    );
    this.quantitiesSharedCollection = this.quantityService.addQuantityToCollectionIfMissing(
      this.quantitiesSharedCollection,
      ingredientQuantity.quantity
    );
  }

  protected loadRelationshipsOptions(): void {
    this.ingredientService
      .query()
      .pipe(map((res: HttpResponse<IIngredient[]>) => res.body ?? []))
      .pipe(
        map((ingredients: IIngredient[]) =>
          this.ingredientService.addIngredientToCollectionIfMissing(ingredients, this.editForm.get('ingredient')!.value)
        )
      )
      .subscribe((ingredients: IIngredient[]) => (this.ingredientsSharedCollection = ingredients));

    this.quantityService
      .query()
      .pipe(map((res: HttpResponse<IQuantity[]>) => res.body ?? []))
      .pipe(
        map((quantities: IQuantity[]) =>
          this.quantityService.addQuantityToCollectionIfMissing(quantities, this.editForm.get('quantity')!.value)
        )
      )
      .subscribe((quantities: IQuantity[]) => (this.quantitiesSharedCollection = quantities));
  }

  protected createFromForm(): IIngredientQuantity {
    return {
      ...new IngredientQuantity(),
      id: this.editForm.get(['id'])!.value,
      ingredient: this.editForm.get(['ingredient'])!.value,
      quantity: this.editForm.get(['quantity'])!.value,
    };
  }
}
