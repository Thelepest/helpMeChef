import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IIngredientCategory } from '../ingredient-category.model';
import { IngredientCategoryService } from '../service/ingredient-category.service';
import { IngredientCategoryDeleteDialogComponent } from '../delete/ingredient-category-delete-dialog.component';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-ingredient-category',
  templateUrl: './ingredient-category.component.html',
})
export class IngredientCategoryComponent implements OnInit {
  ingredientCategories?: IIngredientCategory[];
  isLoading = false;

  constructor(
    protected ingredientCategoryService: IngredientCategoryService,
    protected dataUtils: DataUtils,
    protected modalService: NgbModal
  ) {}

  loadAll(): void {
    this.isLoading = true;

    this.ingredientCategoryService.query().subscribe({
      next: (res: HttpResponse<IIngredientCategory[]>) => {
        this.isLoading = false;
        this.ingredientCategories = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IIngredientCategory): number {
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(ingredientCategory: IIngredientCategory): void {
    const modalRef = this.modalService.open(IngredientCategoryDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.ingredientCategory = ingredientCategory;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
