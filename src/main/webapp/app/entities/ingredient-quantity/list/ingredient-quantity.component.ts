import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IIngredientQuantity } from '../ingredient-quantity.model';
import { IngredientQuantityService } from '../service/ingredient-quantity.service';
import { IngredientQuantityDeleteDialogComponent } from '../delete/ingredient-quantity-delete-dialog.component';

@Component({
  selector: 'jhi-ingredient-quantity',
  templateUrl: './ingredient-quantity.component.html',
})
export class IngredientQuantityComponent implements OnInit {
  ingredientQuantities?: IIngredientQuantity[];
  isLoading = false;

  constructor(protected ingredientQuantityService: IngredientQuantityService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.ingredientQuantityService.query().subscribe({
      next: (res: HttpResponse<IIngredientQuantity[]>) => {
        this.isLoading = false;
        this.ingredientQuantities = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IIngredientQuantity): number {
    return item.id!;
  }

  delete(ingredientQuantity: IIngredientQuantity): void {
    const modalRef = this.modalService.open(IngredientQuantityDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.ingredientQuantity = ingredientQuantity;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
