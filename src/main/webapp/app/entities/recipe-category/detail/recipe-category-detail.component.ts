import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IRecipeCategory } from '../recipe-category.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-recipe-category-detail',
  templateUrl: './recipe-category-detail.component.html',
})
export class RecipeCategoryDetailComponent implements OnInit {
  recipeCategory: IRecipeCategory | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ recipeCategory }) => {
      this.recipeCategory = recipeCategory;
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  previousState(): void {
    window.history.back();
  }
}
