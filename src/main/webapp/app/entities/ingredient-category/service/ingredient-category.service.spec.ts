import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IIngredientCategory, IngredientCategory } from '../ingredient-category.model';

import { IngredientCategoryService } from './ingredient-category.service';

describe('IngredientCategory Service', () => {
  let service: IngredientCategoryService;
  let httpMock: HttpTestingController;
  let elemDefault: IIngredientCategory;
  let expectedResult: IIngredientCategory | IIngredientCategory[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(IngredientCategoryService);
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

    it('should create a IngredientCategory', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new IngredientCategory()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a IngredientCategory', () => {
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

    it('should partial update a IngredientCategory', () => {
      const patchObject = Object.assign(
        {
          image: 'BBBBBB',
        },
        new IngredientCategory()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of IngredientCategory', () => {
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

    it('should delete a IngredientCategory', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addIngredientCategoryToCollectionIfMissing', () => {
      it('should add a IngredientCategory to an empty array', () => {
        const ingredientCategory: IIngredientCategory = { id: 123 };
        expectedResult = service.addIngredientCategoryToCollectionIfMissing([], ingredientCategory);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(ingredientCategory);
      });

      it('should not add a IngredientCategory to an array that contains it', () => {
        const ingredientCategory: IIngredientCategory = { id: 123 };
        const ingredientCategoryCollection: IIngredientCategory[] = [
          {
            ...ingredientCategory,
          },
          { id: 456 },
        ];
        expectedResult = service.addIngredientCategoryToCollectionIfMissing(ingredientCategoryCollection, ingredientCategory);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a IngredientCategory to an array that doesn't contain it", () => {
        const ingredientCategory: IIngredientCategory = { id: 123 };
        const ingredientCategoryCollection: IIngredientCategory[] = [{ id: 456 }];
        expectedResult = service.addIngredientCategoryToCollectionIfMissing(ingredientCategoryCollection, ingredientCategory);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(ingredientCategory);
      });

      it('should add only unique IngredientCategory to an array', () => {
        const ingredientCategoryArray: IIngredientCategory[] = [{ id: 123 }, { id: 456 }, { id: 84036 }];
        const ingredientCategoryCollection: IIngredientCategory[] = [{ id: 123 }];
        expectedResult = service.addIngredientCategoryToCollectionIfMissing(ingredientCategoryCollection, ...ingredientCategoryArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const ingredientCategory: IIngredientCategory = { id: 123 };
        const ingredientCategory2: IIngredientCategory = { id: 456 };
        expectedResult = service.addIngredientCategoryToCollectionIfMissing([], ingredientCategory, ingredientCategory2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(ingredientCategory);
        expect(expectedResult).toContain(ingredientCategory2);
      });

      it('should accept null and undefined values', () => {
        const ingredientCategory: IIngredientCategory = { id: 123 };
        expectedResult = service.addIngredientCategoryToCollectionIfMissing([], null, ingredientCategory, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(ingredientCategory);
      });

      it('should return initial array if no IngredientCategory is added', () => {
        const ingredientCategoryCollection: IIngredientCategory[] = [{ id: 123 }];
        expectedResult = service.addIngredientCategoryToCollectionIfMissing(ingredientCategoryCollection, undefined, null);
        expect(expectedResult).toEqual(ingredientCategoryCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
