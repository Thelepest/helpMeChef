import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { PantryService } from '../service/pantry.service';
import { IPantry, Pantry } from '../pantry.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IIngredientQuantity } from 'app/entities/ingredient-quantity/ingredient-quantity.model';
import { IngredientQuantityService } from 'app/entities/ingredient-quantity/service/ingredient-quantity.service';

import { PantryUpdateComponent } from './pantry-update.component';

describe('Pantry Management Update Component', () => {
  let comp: PantryUpdateComponent;
  let fixture: ComponentFixture<PantryUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let pantryService: PantryService;
  let userService: UserService;
  let ingredientQuantityService: IngredientQuantityService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [PantryUpdateComponent],
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
      .overrideTemplate(PantryUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PantryUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    pantryService = TestBed.inject(PantryService);
    userService = TestBed.inject(UserService);
    ingredientQuantityService = TestBed.inject(IngredientQuantityService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const pantry: IPantry = { id: 456 };
      const user: IUser = { id: '44988586-5be0-4743-a4be-cb06766a4d4e' };
      pantry.user = user;

      const userCollection: IUser[] = [{ id: 'f2018a34-8501-4578-bd96-589fd814b596' }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ pantry });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(userCollection, ...additionalUsers);
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call IngredientQuantity query and add missing value', () => {
      const pantry: IPantry = { id: 456 };
      const ingredientquantities: IIngredientQuantity[] = [{ id: 30752 }];
      pantry.ingredientquantities = ingredientquantities;

      const ingredientQuantityCollection: IIngredientQuantity[] = [{ id: 41438 }];
      jest.spyOn(ingredientQuantityService, 'query').mockReturnValue(of(new HttpResponse({ body: ingredientQuantityCollection })));
      const additionalIngredientQuantities = [...ingredientquantities];
      const expectedCollection: IIngredientQuantity[] = [...additionalIngredientQuantities, ...ingredientQuantityCollection];
      jest.spyOn(ingredientQuantityService, 'addIngredientQuantityToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ pantry });
      comp.ngOnInit();

      expect(ingredientQuantityService.query).toHaveBeenCalled();
      expect(ingredientQuantityService.addIngredientQuantityToCollectionIfMissing).toHaveBeenCalledWith(
        ingredientQuantityCollection,
        ...additionalIngredientQuantities
      );
      expect(comp.ingredientQuantitiesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const pantry: IPantry = { id: 456 };
      const user: IUser = { id: 'b2960823-3fff-4047-82e8-288e6979e24a' };
      pantry.user = user;
      const ingredientquantities: IIngredientQuantity = { id: 44296 };
      pantry.ingredientquantities = [ingredientquantities];

      activatedRoute.data = of({ pantry });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(pantry));
      expect(comp.usersSharedCollection).toContain(user);
      expect(comp.ingredientQuantitiesSharedCollection).toContain(ingredientquantities);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Pantry>>();
      const pantry = { id: 123 };
      jest.spyOn(pantryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ pantry });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: pantry }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(pantryService.update).toHaveBeenCalledWith(pantry);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Pantry>>();
      const pantry = new Pantry();
      jest.spyOn(pantryService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ pantry });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: pantry }));
      saveSubject.complete();

      // THEN
      expect(pantryService.create).toHaveBeenCalledWith(pantry);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Pantry>>();
      const pantry = { id: 123 };
      jest.spyOn(pantryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ pantry });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(pantryService.update).toHaveBeenCalledWith(pantry);
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

    describe('trackIngredientQuantityById', () => {
      it('Should return tracked IngredientQuantity primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackIngredientQuantityById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });

  describe('Getting selected relationships', () => {
    describe('getSelectedIngredientQuantity', () => {
      it('Should return option if no IngredientQuantity is selected', () => {
        const option = { id: 123 };
        const result = comp.getSelectedIngredientQuantity(option);
        expect(result === option).toEqual(true);
      });

      it('Should return selected IngredientQuantity for according option', () => {
        const option = { id: 123 };
        const selected = { id: 123 };
        const selected2 = { id: 456 };
        const result = comp.getSelectedIngredientQuantity(option, [selected2, selected]);
        expect(result === selected).toEqual(true);
        expect(result === selected2).toEqual(false);
        expect(result === option).toEqual(false);
      });

      it('Should return option if this IngredientQuantity is not selected', () => {
        const option = { id: 123 };
        const selected = { id: 456 };
        const result = comp.getSelectedIngredientQuantity(option, [selected]);
        expect(result === option).toEqual(true);
        expect(result === selected).toEqual(false);
      });
    });
  });
});
