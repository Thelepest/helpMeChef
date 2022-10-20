import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IMyConfig } from '../my-config.model';
import { MyConfigService } from '../service/my-config.service';

@Component({
  templateUrl: './my-config-delete-dialog.component.html',
})
export class MyConfigDeleteDialogComponent {
  myConfig?: IMyConfig;

  constructor(protected myConfigService: MyConfigService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.myConfigService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
