import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IMyConfig } from '../my-config.model';
import { MyConfigService } from '../service/my-config.service';
import { MyConfigDeleteDialogComponent } from '../delete/my-config-delete-dialog.component';

@Component({
  selector: 'jhi-my-config',
  templateUrl: './my-config.component.html',
})
export class MyConfigComponent implements OnInit {
  myConfigs?: IMyConfig[];
  isLoading = false;

  constructor(protected myConfigService: MyConfigService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.myConfigService.query().subscribe({
      next: (res: HttpResponse<IMyConfig[]>) => {
        this.isLoading = false;
        this.myConfigs = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IMyConfig): number {
    return item.id!;
  }

  delete(myConfig: IMyConfig): void {
    const modalRef = this.modalService.open(MyConfigDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.myConfig = myConfig;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
