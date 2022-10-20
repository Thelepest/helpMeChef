import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { CommentService } from '../service/comment.service';
import { IComment, Comment } from '../comment.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IRecipe } from 'app/entities/recipe/recipe.model';
import { RecipeService } from 'app/entities/recipe/service/recipe.service';

import { CommentUpdateComponent } from './comment-update.component';

describe('Comment Management Update Component', () => {
  let comp: CommentUpdateComponent;
  let fixture: ComponentFixture<CommentUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let commentService: CommentService;
  let userService: UserService;
  let recipeService: RecipeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [CommentUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(CommentUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CommentUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    commentService = TestBed.inject(CommentService);
    userService = TestBed.inject(UserService);
    recipeService = TestBed.inject(RecipeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const comment: IComment = { id: 456 };
      const user: IUser = { id: '3dc4748f-cb87-4cb9-83b5-cbb4e2478f36' };
      comment.user = user;

      const userCollection: IUser[] = [{ id: 'df883b00-a789-4cab-8194-ef1c588271d3' }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ comment });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(userCollection, ...additionalUsers);
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Recipe query and add missing value', () => {
      const comment: IComment = { id: 456 };
      const recipe: IRecipe = { id: 30812 };
      comment.recipe = recipe;

      const recipeCollection: IRecipe[] = [{ id: 28937 }];
      jest.spyOn(recipeService, 'query').mockReturnValue(of(new HttpResponse({ body: recipeCollection })));
      const additionalRecipes = [recipe];
      const expectedCollection: IRecipe[] = [...additionalRecipes, ...recipeCollection];
      jest.spyOn(recipeService, 'addRecipeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ comment });
      comp.ngOnInit();

      expect(recipeService.query).toHaveBeenCalled();
      expect(recipeService.addRecipeToCollectionIfMissing).toHaveBeenCalledWith(recipeCollection, ...additionalRecipes);
      expect(comp.recipesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const comment: IComment = { id: 456 };
      const user: IUser = { id: '0f4befb6-0d4a-48dd-9683-9d660b70d510' };
      comment.user = user;
      const recipe: IRecipe = { id: 28667 };
      comment.recipe = recipe;

      activatedRoute.data = of({ comment });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(comment));
      expect(comp.usersSharedCollection).toContain(user);
      expect(comp.recipesSharedCollection).toContain(recipe);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Comment>>();
      const comment = { id: 123 };
      jest.spyOn(commentService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ comment });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: comment }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(commentService.update).toHaveBeenCalledWith(comment);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Comment>>();
      const comment = new Comment();
      jest.spyOn(commentService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ comment });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: comment }));
      saveSubject.complete();

      // THEN
      expect(commentService.create).toHaveBeenCalledWith(comment);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Comment>>();
      const comment = { id: 123 };
      jest.spyOn(commentService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ comment });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(commentService.update).toHaveBeenCalledWith(comment);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackUserById', () => {
      it('Should return tracked User primary key', () => {
        const entity = { id: 'ABC' };
        const trackResult = comp.trackUserById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackRecipeById', () => {
      it('Should return tracked Recipe primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackRecipeById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
