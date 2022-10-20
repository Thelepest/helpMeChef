import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ITool, Tool } from '../tool.model';
import { ToolService } from '../service/tool.service';
import { IRecipe } from 'app/entities/recipe/recipe.model';
import { RecipeService } from 'app/entities/recipe/service/recipe.service';

@Component({
  selector: 'jhi-tool-update',
  templateUrl: './tool-update.component.html',
})
export class ToolUpdateComponent implements OnInit {
  isSaving = false;

  recipesSharedCollection: IRecipe[] = [];

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    description: [],
    recipes: [],
  });

  constructor(
    protected toolService: ToolService,
    protected recipeService: RecipeService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ tool }) => {
      this.updateForm(tool);

      this.loadRelationshipsOptions();
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

  trackRecipeById(_index: number, item: IRecipe): number {
    return item.id!;
  }

  getSelectedRecipe(option: IRecipe, selectedVals?: IRecipe[]): IRecipe {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
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
      recipes: tool.recipes,
    });

    this.recipesSharedCollection = this.recipeService.addRecipeToCollectionIfMissing(this.recipesSharedCollection, ...(tool.recipes ?? []));
  }

  protected loadRelationshipsOptions(): void {
    this.recipeService
      .query()
      .pipe(map((res: HttpResponse<IRecipe[]>) => res.body ?? []))
      .pipe(
        map((recipes: IRecipe[]) =>
          this.recipeService.addRecipeToCollectionIfMissing(recipes, ...(this.editForm.get('recipes')!.value ?? []))
        )
      )
      .subscribe((recipes: IRecipe[]) => (this.recipesSharedCollection = recipes));
  }

  protected createFromForm(): ITool {
    return {
      ...new Tool(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      description: this.editForm.get(['description'])!.value,
      recipes: this.editForm.get(['recipes'])!.value,
    };
  }
}
