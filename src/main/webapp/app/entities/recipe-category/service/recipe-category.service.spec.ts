import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IRecipeCategory, RecipeCategory } from '../recipe-category.model';

import { RecipeCategoryService } from './recipe-category.service';

describe('RecipeCategory Service', () => {
  let service: RecipeCategoryService;
  let httpMock: HttpTestingController;
  let elemDefault: IRecipeCategory;
  let expectedResult: IRecipeCategory | IRecipeCategory[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(RecipeCategoryService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      name: 'AAAAAAA',
      imageContentType: 'image/png',
      image: 'AAAAAAA',
      description: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a RecipeCategory', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new RecipeCategory()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a RecipeCategory', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          image: 'BBBBBB',
          description: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a RecipeCategory', () => {
      const patchObject = Object.assign(
        {
          name: 'BBBBBB',
          description: 'BBBBBB',
        },
        new RecipeCategory()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of RecipeCategory', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          image: 'BBBBBB',
          description: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a RecipeCategory', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addRecipeCategoryToCollectionIfMissing', () => {
      it('should add a RecipeCategory to an empty array', () => {
        const recipeCategory: IRecipeCategory = { id: 123 };
        expectedResult = service.addRecipeCategoryToCollectionIfMissing([], recipeCategory);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(recipeCategory);
      });

      it('should not add a RecipeCategory to an array that contains it', () => {
        const recipeCategory: IRecipeCategory = { id: 123 };
        const recipeCategoryCollection: IRecipeCategory[] = [
          {
            ...recipeCategory,
          },
          { id: 456 },
        ];
        expectedResult = service.addRecipeCategoryToCollectionIfMissing(recipeCategoryCollection, recipeCategory);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a RecipeCategory to an array that doesn't contain it", () => {
        const recipeCategory: IRecipeCategory = { id: 123 };
        const recipeCategoryCollection: IRecipeCategory[] = [{ id: 456 }];
        expectedResult = service.addRecipeCategoryToCollectionIfMissing(recipeCategoryCollection, recipeCategory);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(recipeCategory);
      });

      it('should add only unique RecipeCategory to an array', () => {
        const recipeCategoryArray: IRecipeCategory[] = [{ id: 123 }, { id: 456 }, { id: 15550 }];
        const recipeCategoryCollection: IRecipeCategory[] = [{ id: 123 }];
        expectedResult = service.addRecipeCategoryToCollectionIfMissing(recipeCategoryCollection, ...recipeCategoryArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const recipeCategory: IRecipeCategory = { id: 123 };
        const recipeCategory2: IRecipeCategory = { id: 456 };
        expectedResult = service.addRecipeCategoryToCollectionIfMissing([], recipeCategory, recipeCategory2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(recipeCategory);
        expect(expectedResult).toContain(recipeCategory2);
      });

      it('should accept null and undefined values', () => {
        const recipeCategory: IRecipeCategory = { id: 123 };
        expectedResult = service.addRecipeCategoryToCollectionIfMissing([], null, recipeCategory, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(recipeCategory);
      });

      it('should return initial array if no RecipeCategory is added', () => {
        const recipeCategoryCollection: IRecipeCategory[] = [{ id: 123 }];
        expectedResult = service.addRecipeCategoryToCollectionIfMissing(recipeCategoryCollection, undefined, null);
        expect(expectedResult).toEqual(recipeCategoryCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
