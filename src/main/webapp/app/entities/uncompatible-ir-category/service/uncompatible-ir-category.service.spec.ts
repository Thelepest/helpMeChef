import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IUncompatibleIRCategory, UncompatibleIRCategory } from '../uncompatible-ir-category.model';

import { UncompatibleIRCategoryService } from './uncompatible-ir-category.service';

describe('UncompatibleIRCategory Service', () => {
  let service: UncompatibleIRCategoryService;
  let httpMock: HttpTestingController;
  let elemDefault: IUncompatibleIRCategory;
  let expectedResult: IUncompatibleIRCategory | IUncompatibleIRCategory[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(UncompatibleIRCategoryService);
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

    it('should create a UncompatibleIRCategory', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new UncompatibleIRCategory()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a UncompatibleIRCategory', () => {
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

    it('should partial update a UncompatibleIRCategory', () => {
      const patchObject = Object.assign({}, new UncompatibleIRCategory());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of UncompatibleIRCategory', () => {
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

    it('should delete a UncompatibleIRCategory', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addUncompatibleIRCategoryToCollectionIfMissing', () => {
      it('should add a UncompatibleIRCategory to an empty array', () => {
        const uncompatibleIRCategory: IUncompatibleIRCategory = { id: 123 };
        expectedResult = service.addUncompatibleIRCategoryToCollectionIfMissing([], uncompatibleIRCategory);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(uncompatibleIRCategory);
      });

      it('should not add a UncompatibleIRCategory to an array that contains it', () => {
        const uncompatibleIRCategory: IUncompatibleIRCategory = { id: 123 };
        const uncompatibleIRCategoryCollection: IUncompatibleIRCategory[] = [
          {
            ...uncompatibleIRCategory,
          },
          { id: 456 },
        ];
        expectedResult = service.addUncompatibleIRCategoryToCollectionIfMissing(uncompatibleIRCategoryCollection, uncompatibleIRCategory);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a UncompatibleIRCategory to an array that doesn't contain it", () => {
        const uncompatibleIRCategory: IUncompatibleIRCategory = { id: 123 };
        const uncompatibleIRCategoryCollection: IUncompatibleIRCategory[] = [{ id: 456 }];
        expectedResult = service.addUncompatibleIRCategoryToCollectionIfMissing(uncompatibleIRCategoryCollection, uncompatibleIRCategory);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(uncompatibleIRCategory);
      });

      it('should add only unique UncompatibleIRCategory to an array', () => {
        const uncompatibleIRCategoryArray: IUncompatibleIRCategory[] = [{ id: 123 }, { id: 456 }, { id: 67952 }];
        const uncompatibleIRCategoryCollection: IUncompatibleIRCategory[] = [{ id: 123 }];
        expectedResult = service.addUncompatibleIRCategoryToCollectionIfMissing(
          uncompatibleIRCategoryCollection,
          ...uncompatibleIRCategoryArray
        );
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const uncompatibleIRCategory: IUncompatibleIRCategory = { id: 123 };
        const uncompatibleIRCategory2: IUncompatibleIRCategory = { id: 456 };
        expectedResult = service.addUncompatibleIRCategoryToCollectionIfMissing([], uncompatibleIRCategory, uncompatibleIRCategory2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(uncompatibleIRCategory);
        expect(expectedResult).toContain(uncompatibleIRCategory2);
      });

      it('should accept null and undefined values', () => {
        const uncompatibleIRCategory: IUncompatibleIRCategory = { id: 123 };
        expectedResult = service.addUncompatibleIRCategoryToCollectionIfMissing([], null, uncompatibleIRCategory, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(uncompatibleIRCategory);
      });

      it('should return initial array if no UncompatibleIRCategory is added', () => {
        const uncompatibleIRCategoryCollection: IUncompatibleIRCategory[] = [{ id: 123 }];
        expectedResult = service.addUncompatibleIRCategoryToCollectionIfMissing(uncompatibleIRCategoryCollection, undefined, null);
        expect(expectedResult).toEqual(uncompatibleIRCategoryCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
