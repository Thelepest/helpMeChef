import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { IUncompatibleIRCategory, UncompatibleIRCategory } from '../uncompatible-ir-category.model';
import { UncompatibleIRCategoryService } from '../service/uncompatible-ir-category.service';

import { UncompatibleIRCategoryRoutingResolveService } from './uncompatible-ir-category-routing-resolve.service';

describe('UncompatibleIRCategory routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: UncompatibleIRCategoryRoutingResolveService;
  let service: UncompatibleIRCategoryService;
  let resultUncompatibleIRCategory: IUncompatibleIRCategory | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({}),
            },
          },
        },
      ],
    });
    mockRouter = TestBed.inject(Router);
    jest.spyOn(mockRouter, 'navigate').mockImplementation(() => Promise.resolve(true));
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRoute).snapshot;
    routingResolveService = TestBed.inject(UncompatibleIRCategoryRoutingResolveService);
    service = TestBed.inject(UncompatibleIRCategoryService);
    resultUncompatibleIRCategory = undefined;
  });

  describe('resolve', () => {
    it('should return IUncompatibleIRCategory returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultUncompatibleIRCategory = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultUncompatibleIRCategory).toEqual({ id: 123 });
    });

    it('should return new IUncompatibleIRCategory if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultUncompatibleIRCategory = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultUncompatibleIRCategory).toEqual(new UncompatibleIRCategory());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as UncompatibleIRCategory })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultUncompatibleIRCategory = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultUncompatibleIRCategory).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
