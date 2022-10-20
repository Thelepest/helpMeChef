import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IIngredientQuantity } from '../ingredient-quantity.model';
import { IngredientQuantityService } from '../service/ingredient-quantity.service';

@Component({
  templateUrl: './ingredient-quantity-delete-dialog.component.html',
})
export class IngredientQuantityDeleteDialogComponent {
  ingredientQuantity?: IIngredientQuantity;

  constructor(protected ingredientQuantityService: IngredientQuantityService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.ingredientQuantityService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
