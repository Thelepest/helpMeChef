import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IPantry } from '../pantry.model';
import { PantryService } from '../service/pantry.service';
import { PantryDeleteDialogComponent } from '../delete/pantry-delete-dialog.component';

@Component({
  selector: 'jhi-pantry',
  templateUrl: './pantry.component.html',
})
export class PantryComponent implements OnInit {
  pantries?: IPantry[];
  isLoading = false;

  constructor(protected pantryService: PantryService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.pantryService.query().subscribe({
      next: (res: HttpResponse<IPantry[]>) => {
        this.isLoading = false;
        this.pantries = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IPantry): number {
    return item.id!;
  }

  delete(pantry: IPantry): void {
    const modalRef = this.modalService.open(PantryDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.pantry = pantry;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
