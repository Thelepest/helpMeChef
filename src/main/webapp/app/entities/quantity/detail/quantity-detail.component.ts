import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IQuantity } from '../quantity.model';

@Component({
  selector: 'jhi-quantity-detail',
  templateUrl: './quantity-detail.component.html',
})
export class QuantityDetailComponent implements OnInit {
  quantity: IQuantity | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ quantity }) => {
      this.quantity = quantity;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
