import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IPantry, Pantry } from '../pantry.model';
import { PantryService } from '../service/pantry.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IIngredientQuantity } from 'app/entities/ingredient-quantity/ingredient-quantity.model';
import { IngredientQuantityService } from 'app/entities/ingredient-quantity/service/ingredient-quantity.service';

@Component({
  selector: 'jhi-pantry-update',
  templateUrl: './pantry-update.component.html',
})
export class PantryUpdateComponent implements OnInit {
  isSaving = false;

  usersSharedCollection: IUser[] = [];
  ingredientQuantitiesSharedCollection: IIngredientQuantity[] = [];

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    active: [null, [Validators.required]],
    description: [],
    createdAt: [null, [Validators.required]],
    user: [],
    ingredientquantities: [],
  });

  constructor(
    protected pantryService: PantryService,
    protected userService: UserService,
    protected ingredientQuantityService: IngredientQuantityService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ pantry }) => {
      if (pantry.id === undefined) {
        const today = dayjs().startOf('day');
        pantry.createdAt = today;
      }

      this.updateForm(pantry);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const pantry = this.createFromForm();
    if (pantry.id !== undefined) {
      this.subscribeToSaveResponse(this.pantryService.update(pantry));
    } else {
      this.subscribeToSaveResponse(this.pantryService.create(pantry));
    }
  }

  trackUserById(_index: number, item: IUser): string {
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

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPantry>>): void {
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

  protected updateForm(pantry: IPantry): void {
    this.editForm.patchValue({
      id: pantry.id,
      name: pantry.name,
      active: pantry.active,
      description: pantry.description,
      createdAt: pantry.createdAt ? pantry.createdAt.format(DATE_TIME_FORMAT) : null,
      user: pantry.user,
      ingredientquantities: pantry.ingredientquantities,
    });

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing(this.usersSharedCollection, pantry.user);
    this.ingredientQuantitiesSharedCollection = this.ingredientQuantityService.addIngredientQuantityToCollectionIfMissing(
      this.ingredientQuantitiesSharedCollection,
      ...(pantry.ingredientquantities ?? [])
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing(users, this.editForm.get('user')!.value)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

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

  protected createFromForm(): IPantry {
    return {
      ...new Pantry(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      active: this.editForm.get(['active'])!.value,
      description: this.editForm.get(['description'])!.value,
      createdAt: this.editForm.get(['createdAt'])!.value ? dayjs(this.editForm.get(['createdAt'])!.value, DATE_TIME_FORMAT) : undefined,
      user: this.editForm.get(['user'])!.value,
      ingredientquantities: this.editForm.get(['ingredientquantities'])!.value,
    };
  }
}
