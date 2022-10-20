import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IIngredient } from '../ingredient.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-ingredient-detail',
  templateUrl: './ingredient-detail.component.html',
})
export class IngredientDetailComponent implements OnInit {
  ingredient: IIngredient | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ ingredient }) => {
      this.ingredient = ingredient;
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
