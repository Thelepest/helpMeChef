import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IIngredientQuantity } from '../ingredient-quantity.model';

@Component({
  selector: 'jhi-ingredient-quantity-detail',
  templateUrl: './ingredient-quantity-detail.component.html',
})
export class IngredientQuantityDetailComponent implements OnInit {
  ingredientQuantity: IIngredientQuantity | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ ingredientQuantity }) => {
      this.ingredientQuantity = ingredientQuantity;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
