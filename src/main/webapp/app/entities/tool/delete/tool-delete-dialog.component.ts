import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ITool } from '../tool.model';
import { ToolService } from '../service/tool.service';

@Component({
  templateUrl: './tool-delete-dialog.component.html',
})
export class ToolDeleteDialogComponent {
  tool?: ITool;

  constructor(protected toolService: ToolService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.toolService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
