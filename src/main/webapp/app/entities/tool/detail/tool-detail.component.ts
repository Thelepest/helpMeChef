import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ITool } from '../tool.model';

@Component({
  selector: 'jhi-tool-detail',
  templateUrl: './tool-detail.component.html',
})
export class ToolDetailComponent implements OnInit {
  tool: ITool | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ tool }) => {
      this.tool = tool;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
