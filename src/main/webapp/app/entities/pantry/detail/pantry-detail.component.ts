import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IPantry } from '../pantry.model';

@Component({
  selector: 'jhi-pantry-detail',
  templateUrl: './pantry-detail.component.html',
})
export class PantryDetailComponent implements OnInit {
  pantry: IPantry | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ pantry }) => {
      this.pantry = pantry;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
