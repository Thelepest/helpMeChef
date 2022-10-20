import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IPantry, Pantry } from '../pantry.model';

import { PantryService } from './pantry.service';

describe('Pantry Service', () => {
  let service: PantryService;
  let httpMock: HttpTestingController;
  let elemDefault: IPantry;
  let expectedResult: IPantry | IPantry[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(PantryService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      name: 'AAAAAAA',
      active: false,
      description: 'AAAAAAA',
      createdAt: currentDate,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          createdAt: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Pantry', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
          createdAt: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          createdAt: currentDate,
        },
        returnedFromService
      );

      service.create(new Pantry()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Pantry', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          active: true,
          description: 'BBBBBB',
          createdAt: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          createdAt: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Pantry', () => {
      const patchObject = Object.assign(
        {
          name: 'BBBBBB',
          description: 'BBBBBB',
          createdAt: currentDate.format(DATE_TIME_FORMAT),
        },
        new Pantry()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          createdAt: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Pantry', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          active: true,
          description: 'BBBBBB',
          createdAt: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          createdAt: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Pantry', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addPantryToCollectionIfMissing', () => {
      it('should add a Pantry to an empty array', () => {
        const pantry: IPantry = { id: 123 };
        expectedResult = service.addPantryToCollectionIfMissing([], pantry);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(pantry);
      });

      it('should not add a Pantry to an array that contains it', () => {
        const pantry: IPantry = { id: 123 };
        const pantryCollection: IPantry[] = [
          {
            ...pantry,
          },
          { id: 456 },
        ];
        expectedResult = service.addPantryToCollectionIfMissing(pantryCollection, pantry);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Pantry to an array that doesn't contain it", () => {
        const pantry: IPantry = { id: 123 };
        const pantryCollection: IPantry[] = [{ id: 456 }];
        expectedResult = service.addPantryToCollectionIfMissing(pantryCollection, pantry);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(pantry);
      });

      it('should add only unique Pantry to an array', () => {
        const pantryArray: IPantry[] = [{ id: 123 }, { id: 456 }, { id: 22277 }];
        const pantryCollection: IPantry[] = [{ id: 123 }];
        expectedResult = service.addPantryToCollectionIfMissing(pantryCollection, ...pantryArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const pantry: IPantry = { id: 123 };
        const pantry2: IPantry = { id: 456 };
        expectedResult = service.addPantryToCollectionIfMissing([], pantry, pantry2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(pantry);
        expect(expectedResult).toContain(pantry2);
      });

      it('should accept null and undefined values', () => {
        const pantry: IPantry = { id: 123 };
        expectedResult = service.addPantryToCollectionIfMissing([], null, pantry, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(pantry);
      });

      it('should return initial array if no Pantry is added', () => {
        const pantryCollection: IPantry[] = [{ id: 123 }];
        expectedResult = service.addPantryToCollectionIfMissing(pantryCollection, undefined, null);
        expect(expectedResult).toEqual(pantryCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
