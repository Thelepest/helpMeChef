import { Component, OnInit, ElementRef } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IRecipeCategory, RecipeCategory } from '../recipe-category.model';
import { RecipeCategoryService } from '../service/recipe-category.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-recipe-category-update',
  templateUrl: './recipe-category-update.component.html',
})
export class RecipeCategoryUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    image: [null, [Validators.required]],
    imageContentType: [],
    description: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected recipeCategoryService: RecipeCategoryService,
    protected elementRef: ElementRef,
    protected activatedRoute: ActivatedRoute,
    protected fb: UntypedFormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ recipeCategory }) => {
      this.updateForm(recipeCategory);
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('helpMeChefApp.error', { ...err, key: 'error.file.' + err.key })),
    });
  }

  clearInputImage(field: string, fieldContentType: string, idInput: string): void {
    this.editForm.patchValue({
      [field]: null,
      [fieldContentType]: null,
    });
    if (idInput && this.elementRef.nativeElement.querySelector('#' + idInput)) {
      this.elementRef.nativeElement.querySelector('#' + idInput).value = null;
    }
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const recipeCategory = this.createFromForm();
    if (recipeCategory.id !== undefined) {
      this.subscribeToSaveResponse(this.recipeCategoryService.update(recipeCategory));
    } else {
      this.subscribeToSaveResponse(this.recipeCategoryService.create(recipeCategory));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IRecipeCategory>>): void {
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

  protected updateForm(recipeCategory: IRecipeCategory): void {
    this.editForm.patchValue({
      id: recipeCategory.id,
      name: recipeCategory.name,
      image: recipeCategory.image,
      imageContentType: recipeCategory.imageContentType,
      description: recipeCategory.description,
    });
  }

  protected createFromForm(): IRecipeCategory {
    return {
      ...new RecipeCategory(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      imageContentType: this.editForm.get(['imageContentType'])!.value,
      image: this.editForm.get(['image'])!.value,
      description: this.editForm.get(['description'])!.value,
    };
  }
}
