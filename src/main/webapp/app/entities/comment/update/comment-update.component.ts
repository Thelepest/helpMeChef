import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IComment, Comment } from '../comment.model';
import { CommentService } from '../service/comment.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IRecipe } from 'app/entities/recipe/recipe.model';
import { RecipeService } from 'app/entities/recipe/service/recipe.service';

@Component({
  selector: 'jhi-comment-update',
  templateUrl: './comment-update.component.html',
})
export class CommentUpdateComponent implements OnInit {
  isSaving = false;

  usersSharedCollection: IUser[] = [];
  recipesSharedCollection: IRecipe[] = [];

  editForm = this.fb.group({
    id: [],
    title: [null, [Validators.required]],
    body: [null, [Validators.required]],
    user: [],
    recipe: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected commentService: CommentService,
    protected userService: UserService,
    protected recipeService: RecipeService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ comment }) => {
      this.updateForm(comment);

      this.loadRelationshipsOptions();
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

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const comment = this.createFromForm();
    if (comment.id !== undefined) {
      this.subscribeToSaveResponse(this.commentService.update(comment));
    } else {
      this.subscribeToSaveResponse(this.commentService.create(comment));
    }
  }

  trackUserById(_index: number, item: IUser): string {
    return item.id!;
  }

  trackRecipeById(_index: number, item: IRecipe): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IComment>>): void {
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

  protected updateForm(comment: IComment): void {
    this.editForm.patchValue({
      id: comment.id,
      title: comment.title,
      body: comment.body,
      user: comment.user,
      recipe: comment.recipe,
    });

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing(this.usersSharedCollection, comment.user);
    this.recipesSharedCollection = this.recipeService.addRecipeToCollectionIfMissing(this.recipesSharedCollection, comment.recipe);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing(users, this.editForm.get('user')!.value)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.recipeService
      .query()
      .pipe(map((res: HttpResponse<IRecipe[]>) => res.body ?? []))
      .pipe(map((recipes: IRecipe[]) => this.recipeService.addRecipeToCollectionIfMissing(recipes, this.editForm.get('recipe')!.value)))
      .subscribe((recipes: IRecipe[]) => (this.recipesSharedCollection = recipes));
  }

  protected createFromForm(): IComment {
    return {
      ...new Comment(),
      id: this.editForm.get(['id'])!.value,
      title: this.editForm.get(['title'])!.value,
      body: this.editForm.get(['body'])!.value,
      user: this.editForm.get(['user'])!.value,
      recipe: this.editForm.get(['recipe'])!.value,
    };
  }
}
