import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IQuantity } from '../quantity.model';
import { QuantityService } from '../service/quantity.service';

@Component({
  templateUrl: './quantity-delete-dialog.component.html',
})
export class QuantityDeleteDialogComponent {
  quantity?: IQuantity;

  constructor(protected quantityService: QuantityService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.quantityService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
