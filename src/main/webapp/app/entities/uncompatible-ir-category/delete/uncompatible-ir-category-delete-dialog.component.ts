import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IUncompatibleIRCategory } from '../uncompatible-ir-category.model';
import { UncompatibleIRCategoryService } from '../service/uncompatible-ir-category.service';

@Component({
  templateUrl: './uncompatible-ir-category-delete-dialog.component.html',
})
export class UncompatibleIRCategoryDeleteDialogComponent {
  uncompatibleIRCategory?: IUncompatibleIRCategory;

  constructor(protected uncompatibleIRCategoryService: UncompatibleIRCategoryService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.uncompatibleIRCategoryService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
