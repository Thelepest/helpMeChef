import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IMyConfig, MyConfig } from '../my-config.model';
import { MyConfigService } from '../service/my-config.service';

@Component({
  selector: 'jhi-my-config-update',
  templateUrl: './my-config-update.component.html',
})
export class MyConfigUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    mcKey: [null, [Validators.required]],
    mcValue: [null, [Validators.required]],
  });

  constructor(protected myConfigService: MyConfigService, protected activatedRoute: ActivatedRoute, protected fb: UntypedFormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ myConfig }) => {
      this.updateForm(myConfig);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const myConfig = this.createFromForm();
    if (myConfig.id !== undefined) {
      this.subscribeToSaveResponse(this.myConfigService.update(myConfig));
    } else {
      this.subscribeToSaveResponse(this.myConfigService.create(myConfig));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMyConfig>>): void {
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

  protected updateForm(myConfig: IMyConfig): void {
    this.editForm.patchValue({
      id: myConfig.id,
      mcKey: myConfig.mcKey,
      mcValue: myConfig.mcValue,
    });
  }

  protected createFromForm(): IMyConfig {
    return {
      ...new MyConfig(),
      id: this.editForm.get(['id'])!.value,
      mcKey: this.editForm.get(['mcKey'])!.value,
      mcValue: this.editForm.get(['mcValue'])!.value,
    };
  }
}
