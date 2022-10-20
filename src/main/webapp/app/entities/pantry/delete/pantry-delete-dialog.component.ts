import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IPantry } from '../pantry.model';
import { PantryService } from '../service/pantry.service';

@Component({
  templateUrl: './pantry-delete-dialog.component.html',
})
export class PantryDeleteDialogComponent {
  pantry?: IPantry;

  constructor(protected pantryService: PantryService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.pantryService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
