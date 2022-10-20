import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IQuantity, Quantity } from '../quantity.model';
import { QuantityService } from '../service/quantity.service';

@Component({
  selector: 'jhi-quantity-update',
  templateUrl: './quantity-update.component.html',
})
export class QuantityUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    amount: [null, [Validators.required]],
    description: [],
  });

  constructor(protected quantityService: QuantityService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ quantity }) => {
      this.updateForm(quantity);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const quantity = this.createFromForm();
    if (quantity.id !== undefined) {
      this.subscribeToSaveResponse(this.quantityService.update(quantity));
    } else {
      this.subscribeToSaveResponse(this.quantityService.create(quantity));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IQuantity>>): void {
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

  protected updateForm(quantity: IQuantity): void {
    this.editForm.patchValue({
      id: quantity.id,
      name: quantity.name,
      amount: quantity.amount,
      description: quantity.description,
    });
  }

  protected createFromForm(): IQuantity {
    return {
      ...new Quantity(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      amount: this.editForm.get(['amount'])!.value,
      description: this.editForm.get(['description'])!.value,
    };
  }
}
