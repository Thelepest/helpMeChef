import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IQuantity, Quantity } from '../quantity.model';

import { QuantityService } from './quantity.service';

describe('Quantity Service', () => {
  let service: QuantityService;
  let httpMock: HttpTestingController;
  let elemDefault: IQuantity;
  let expectedResult: IQuantity | IQuantity[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(QuantityService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      name: 'AAAAAAA',
      amount: 0,
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

    it('should create a Quantity', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Quantity()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Quantity', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          amount: 1,
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

    it('should partial update a Quantity', () => {
      const patchObject = Object.assign(
        {
          name: 'BBBBBB',
          amount: 1,
        },
        new Quantity()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Quantity', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          amount: 1,
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

    it('should delete a Quantity', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addQuantityToCollectionIfMissing', () => {
      it('should add a Quantity to an empty array', () => {
        const quantity: IQuantity = { id: 123 };
        expectedResult = service.addQuantityToCollectionIfMissing([], quantity);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(quantity);
      });

      it('should not add a Quantity to an array that contains it', () => {
        const quantity: IQuantity = { id: 123 };
        const quantityCollection: IQuantity[] = [
          {
            ...quantity,
          },
          { id: 456 },
        ];
        expectedResult = service.addQuantityToCollectionIfMissing(quantityCollection, quantity);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Quantity to an array that doesn't contain it", () => {
        const quantity: IQuantity = { id: 123 };
        const quantityCollection: IQuantity[] = [{ id: 456 }];
        expectedResult = service.addQuantityToCollectionIfMissing(quantityCollection, quantity);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(quantity);
      });

      it('should add only unique Quantity to an array', () => {
        const quantityArray: IQuantity[] = [{ id: 123 }, { id: 456 }, { id: 6852 }];
        const quantityCollection: IQuantity[] = [{ id: 123 }];
        expectedResult = service.addQuantityToCollectionIfMissing(quantityCollection, ...quantityArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const quantity: IQuantity = { id: 123 };
        const quantity2: IQuantity = { id: 456 };
        expectedResult = service.addQuantityToCollectionIfMissing([], quantity, quantity2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(quantity);
        expect(expectedResult).toContain(quantity2);
      });

      it('should accept null and undefined values', () => {
        const quantity: IQuantity = { id: 123 };
        expectedResult = service.addQuantityToCollectionIfMissing([], null, quantity, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(quantity);
      });

      it('should return initial array if no Quantity is added', () => {
        const quantityCollection: IQuantity[] = [{ id: 123 }];
        expectedResult = service.addQuantityToCollectionIfMissing(quantityCollection, undefined, null);
        expect(expectedResult).toEqual(quantityCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
