import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IQuantity } from '../quantity.model';
import { QuantityService } from '../service/quantity.service';
import { QuantityDeleteDialogComponent } from '../delete/quantity-delete-dialog.component';

@Component({
  selector: 'jhi-quantity',
  templateUrl: './quantity.component.html',
})
export class QuantityComponent implements OnInit {
  quantities?: IQuantity[];
  isLoading = false;

  constructor(protected quantityService: QuantityService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.quantityService.query().subscribe({
      next: (res: HttpResponse<IQuantity[]>) => {
        this.isLoading = false;
        this.quantities = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IQuantity): number {
    return item.id!;
  }

  delete(quantity: IQuantity): void {
    const modalRef = this.modalService.open(QuantityDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.quantity = quantity;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
