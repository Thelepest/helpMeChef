import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IRecipe } from '../recipe.model';
import { RecipeService } from '../service/recipe.service';
import { RecipeDeleteDialogComponent } from '../delete/recipe-delete-dialog.component';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-recipe',
  templateUrl: './recipe.component.html',
})
export class RecipeComponent implements OnInit {
  recipes?: IRecipe[];
  isLoading = false;

  constructor(protected recipeService: RecipeService, protected dataUtils: DataUtils, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.recipeService.query().subscribe({
      next: (res: HttpResponse<IRecipe[]>) => {
        this.isLoading = false;
        this.recipes = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IRecipe): number {
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(recipe: IRecipe): void {
    const modalRef = this.modalService.open(RecipeDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.recipe = recipe;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
