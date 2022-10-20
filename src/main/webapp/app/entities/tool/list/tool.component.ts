import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ITool } from '../tool.model';
import { ToolService } from '../service/tool.service';
import { ToolDeleteDialogComponent } from '../delete/tool-delete-dialog.component';

@Component({
  selector: 'jhi-tool',
  templateUrl: './tool.component.html',
})
export class ToolComponent implements OnInit {
  tools?: ITool[];
  isLoading = false;

  constructor(protected toolService: ToolService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.toolService.query().subscribe({
      next: (res: HttpResponse<ITool[]>) => {
        this.isLoading = false;
        this.tools = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: ITool): number {
    return item.id!;
  }

  delete(tool: ITool): void {
    const modalRef = this.modalService.open(ToolDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.tool = tool;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
