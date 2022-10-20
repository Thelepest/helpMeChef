import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IRecipeCategory } from '../recipe-category.model';
import { RecipeCategoryService } from '../service/recipe-category.service';

@Component({
  templateUrl: './recipe-category-delete-dialog.component.html',
})
export class RecipeCategoryDeleteDialogComponent {
  recipeCategory?: IRecipeCategory;

  constructor(protected recipeCategoryService: RecipeCategoryService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.recipeCategoryService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
