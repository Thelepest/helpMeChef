import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IUncompatibleIRCategory } from '../uncompatible-ir-category.model';

@Component({
  selector: 'jhi-uncompatible-ir-category-detail',
  templateUrl: './uncompatible-ir-category-detail.component.html',
})
export class UncompatibleIRCategoryDetailComponent implements OnInit {
  uncompatibleIRCategory: IUncompatibleIRCategory | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ uncompatibleIRCategory }) => {
      this.uncompatibleIRCategory = uncompatibleIRCategory;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
