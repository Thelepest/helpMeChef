import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IMyConfig } from '../my-config.model';

@Component({
  selector: 'jhi-my-config-detail',
  templateUrl: './my-config-detail.component.html',
})
export class MyConfigDetailComponent implements OnInit {
  myConfig: IMyConfig | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ myConfig }) => {
      this.myConfig = myConfig;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
