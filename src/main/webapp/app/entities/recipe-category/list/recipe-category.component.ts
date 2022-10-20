import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IRecipeCategory } from '../recipe-category.model';
import { RecipeCategoryService } from '../service/recipe-category.service';
import { RecipeCategoryDeleteDialogComponent } from '../delete/recipe-category-delete-dialog.component';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-recipe-category',
  templateUrl: './recipe-category.component.html',
})
export class RecipeCategoryComponent implements OnInit {
  recipeCategories?: IRecipeCategory[];
  isLoading = false;

  constructor(protected recipeCategoryService: RecipeCategoryService, protected dataUtils: DataUtils, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.recipeCategoryService.query().subscribe({
      next: (res: HttpResponse<IRecipeCategory[]>) => {
        this.isLoading = false;
        this.recipeCategories = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IRecipeCategory): number {
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(recipeCategory: IRecipeCategory): void {
    const modalRef = this.modalService.open(RecipeCategoryDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.recipeCategory = recipeCategory;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
