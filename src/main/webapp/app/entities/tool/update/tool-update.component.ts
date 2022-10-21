import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ITool, Tool } from '../tool.model';
import { ToolService } from '../service/tool.service';

@Component({
  selector: 'jhi-tool-update',
  templateUrl: './tool-update.component.html',
})
export class ToolUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    description: [],
  });

  constructor(protected toolService: ToolService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ tool }) => {
      this.updateForm(tool);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const tool = this.createFromForm();
    if (tool.id !== undefined) {
      this.subscribeToSaveResponse(this.toolService.update(tool));
    } else {
      this.subscribeToSaveResponse(this.toolService.create(tool));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITool>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(tool: ITool): void {
    this.editForm.patchValue({
      id: tool.id,
      name: tool.name,
      description: tool.description,
    });
  }

  protected createFromForm(): ITool {
    return {
      ...new Tool(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      description: this.editForm.get(['description'])!.value,
    };
  }
}
