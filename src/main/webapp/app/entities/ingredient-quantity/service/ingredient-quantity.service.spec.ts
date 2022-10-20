import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IIngredientQuantity, IngredientQuantity } from '../ingredient-quantity.model';

import { IngredientQuantityService } from './ingredient-quantity.service';

describe('IngredientQuantity Service', () => {
  let service: IngredientQuantityService;
  let httpMock: HttpTestingController;
  let elemDefault: IIngredientQuantity;
  let expectedResult: IIngredientQuantity | IIngredientQuantity[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(IngredientQuantityService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
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

    it('should create a IngredientQuantity', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new IngredientQuantity()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a IngredientQuantity', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a IngredientQuantity', () => {
      const patchObject = Object.assign({}, new IngredientQuantity());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of IngredientQuantity', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
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

    it('should delete a IngredientQuantity', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addIngredientQuantityToCollectionIfMissing', () => {
      it('should add a IngredientQuantity to an empty array', () => {
        const ingredientQuantity: IIngredientQuantity = { id: 123 };
        expectedResult = service.addIngredientQuantityToCollectionIfMissing([], ingredientQuantity);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(ingredientQuantity);
      });

      it('should not add a IngredientQuantity to an array that contains it', () => {
        const ingredientQuantity: IIngredientQuantity = { id: 123 };
        const ingredientQuantityCollection: IIngredientQuantity[] = [
          {
            ...ingredientQuantity,
          },
          { id: 456 },
        ];
        expectedResult = service.addIngredientQuantityToCollectionIfMissing(ingredientQuantityCollection, ingredientQuantity);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a IngredientQuantity to an array that doesn't contain it", () => {
        const ingredientQuantity: IIngredientQuantity = { id: 123 };
        const ingredientQuantityCollection: IIngredientQuantity[] = [{ id: 456 }];
        expectedResult = service.addIngredientQuantityToCollectionIfMissing(ingredientQuantityCollection, ingredientQuantity);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(ingredientQuantity);
      });

      it('should add only unique IngredientQuantity to an array', () => {
        const ingredientQuantityArray: IIngredientQuantity[] = [{ id: 123 }, { id: 456 }, { id: 47601 }];
        const ingredientQuantityCollection: IIngredientQuantity[] = [{ id: 123 }];
        expectedResult = service.addIngredientQuantityToCollectionIfMissing(ingredientQuantityCollection, ...ingredientQuantityArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const ingredientQuantity: IIngredientQuantity = { id: 123 };
        const ingredientQuantity2: IIngredientQuantity = { id: 456 };
        expectedResult = service.addIngredientQuantityToCollectionIfMissing([], ingredientQuantity, ingredientQuantity2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(ingredientQuantity);
        expect(expectedResult).toContain(ingredientQuantity2);
      });

      it('should accept null and undefined values', () => {
        const ingredientQuantity: IIngredientQuantity = { id: 123 };
        expectedResult = service.addIngredientQuantityToCollectionIfMissing([], null, ingredientQuantity, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(ingredientQuantity);
      });

      it('should return initial array if no IngredientQuantity is added', () => {
        const ingredientQuantityCollection: IIngredientQuantity[] = [{ id: 123 }];
        expectedResult = service.addIngredientQuantityToCollectionIfMissing(ingredientQuantityCollection, undefined, null);
        expect(expectedResult).toEqual(ingredientQuantityCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
