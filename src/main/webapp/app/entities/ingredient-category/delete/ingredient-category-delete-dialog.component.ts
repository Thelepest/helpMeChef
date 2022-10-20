import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IIngredientCategory } from '../ingredient-category.model';
import { IngredientCategoryService } from '../service/ingredient-category.service';

@Component({
  templateUrl: './ingredient-category-delete-dialog.component.html',
})
export class IngredientCategoryDeleteDialogComponent {
  ingredientCategory?: IIngredientCategory;

  constructor(protected ingredientCategoryService: IngredientCategoryService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.ingredientCategoryService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
