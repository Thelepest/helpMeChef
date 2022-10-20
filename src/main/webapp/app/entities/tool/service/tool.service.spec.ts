import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ITool, Tool } from '../tool.model';

import { ToolService } from './tool.service';

describe('Tool Service', () => {
  let service: ToolService;
  let httpMock: HttpTestingController;
  let elemDefault: ITool;
  let expectedResult: ITool | ITool[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ToolService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      name: 'AAAAAAA',
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

    it('should create a Tool', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Tool()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Tool', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
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

    it('should partial update a Tool', () => {
      const patchObject = Object.assign({}, new Tool());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Tool', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
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

    it('should delete a Tool', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addToolToCollectionIfMissing', () => {
      it('should add a Tool to an empty array', () => {
        const tool: ITool = { id: 123 };
        expectedResult = service.addToolToCollectionIfMissing([], tool);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(tool);
      });

      it('should not add a Tool to an array that contains it', () => {
        const tool: ITool = { id: 123 };
        const toolCollection: ITool[] = [
          {
            ...tool,
          },
          { id: 456 },
        ];
        expectedResult = service.addToolToCollectionIfMissing(toolCollection, tool);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Tool to an array that doesn't contain it", () => {
        const tool: ITool = { id: 123 };
        const toolCollection: ITool[] = [{ id: 456 }];
        expectedResult = service.addToolToCollectionIfMissing(toolCollection, tool);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(tool);
      });

      it('should add only unique Tool to an array', () => {
        const toolArray: ITool[] = [{ id: 123 }, { id: 456 }, { id: 18253 }];
        const toolCollection: ITool[] = [{ id: 123 }];
        expectedResult = service.addToolToCollectionIfMissing(toolCollection, ...toolArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const tool: ITool = { id: 123 };
        const tool2: ITool = { id: 456 };
        expectedResult = service.addToolToCollectionIfMissing([], tool, tool2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(tool);
        expect(expectedResult).toContain(tool2);
      });

      it('should accept null and undefined values', () => {
        const tool: ITool = { id: 123 };
        expectedResult = service.addToolToCollectionIfMissing([], null, tool, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(tool);
      });

      it('should return initial array if no Tool is added', () => {
        const toolCollection: ITool[] = [{ id: 123 }];
        expectedResult = service.addToolToCollectionIfMissing(toolCollection, undefined, null);
        expect(expectedResult).toEqual(toolCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
