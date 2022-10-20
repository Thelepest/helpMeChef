import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IMyConfig, MyConfig } from '../my-config.model';

import { MyConfigService } from './my-config.service';

describe('MyConfig Service', () => {
  let service: MyConfigService;
  let httpMock: HttpTestingController;
  let elemDefault: IMyConfig;
  let expectedResult: IMyConfig | IMyConfig[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(MyConfigService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      mcKey: 'AAAAAAA',
      mcValue: 'AAAAAAA',
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

    it('should create a MyConfig', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new MyConfig()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a MyConfig', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          mcKey: 'BBBBBB',
          mcValue: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a MyConfig', () => {
      const patchObject = Object.assign({}, new MyConfig());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of MyConfig', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          mcKey: 'BBBBBB',
          mcValue: 'BBBBBB',
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

    it('should delete a MyConfig', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addMyConfigToCollectionIfMissing', () => {
      it('should add a MyConfig to an empty array', () => {
        const myConfig: IMyConfig = { id: 123 };
        expectedResult = service.addMyConfigToCollectionIfMissing([], myConfig);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(myConfig);
      });

      it('should not add a MyConfig to an array that contains it', () => {
        const myConfig: IMyConfig = { id: 123 };
        const myConfigCollection: IMyConfig[] = [
          {
            ...myConfig,
          },
          { id: 456 },
        ];
        expectedResult = service.addMyConfigToCollectionIfMissing(myConfigCollection, myConfig);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a MyConfig to an array that doesn't contain it", () => {
        const myConfig: IMyConfig = { id: 123 };
        const myConfigCollection: IMyConfig[] = [{ id: 456 }];
        expectedResult = service.addMyConfigToCollectionIfMissing(myConfigCollection, myConfig);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(myConfig);
      });

      it('should add only unique MyConfig to an array', () => {
        const myConfigArray: IMyConfig[] = [{ id: 123 }, { id: 456 }, { id: 13803 }];
        const myConfigCollection: IMyConfig[] = [{ id: 123 }];
        expectedResult = service.addMyConfigToCollectionIfMissing(myConfigCollection, ...myConfigArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const myConfig: IMyConfig = { id: 123 };
        const myConfig2: IMyConfig = { id: 456 };
        expectedResult = service.addMyConfigToCollectionIfMissing([], myConfig, myConfig2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(myConfig);
        expect(expectedResult).toContain(myConfig2);
      });

      it('should accept null and undefined values', () => {
        const myConfig: IMyConfig = { id: 123 };
        expectedResult = service.addMyConfigToCollectionIfMissing([], null, myConfig, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(myConfig);
      });

      it('should return initial array if no MyConfig is added', () => {
        const myConfigCollection: IMyConfig[] = [{ id: 123 }];
        expectedResult = service.addMyConfigToCollectionIfMissing(myConfigCollection, undefined, null);
        expect(expectedResult).toEqual(myConfigCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
