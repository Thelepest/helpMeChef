import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IIngredient } from '../ingredient.model';
import { IngredientService } from '../service/ingredient.service';
import { IngredientDeleteDialogComponent } from '../delete/ingredient-delete-dialog.component';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-ingredient',
  templateUrl: './ingredient.component.html',
})
export class IngredientComponent implements OnInit {
  ingredients?: IIngredient[];
  isLoading = false;

  constructor(protected ingredientService: IngredientService, protected dataUtils: DataUtils, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.ingredientService.query().subscribe({
      next: (res: HttpResponse<IIngredient[]>) => {
        this.isLoading = false;
        this.ingredients = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IIngredient): number {
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(ingredient: IIngredient): void {
    const modalRef = this.modalService.open(IngredientDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.ingredient = ingredient;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
