import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { IIngredientCategory, IngredientCategory } from '../ingredient-category.model';
import { IngredientCategoryService } from '../service/ingredient-category.service';

import { IngredientCategoryRoutingResolveService } from './ingredient-category-routing-resolve.service';

describe('IngredientCategory routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: IngredientCategoryRoutingResolveService;
  let service: IngredientCategoryService;
  let resultIngredientCategory: IIngredientCategory | undefined;

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
    routingResolveService = TestBed.inject(IngredientCategoryRoutingResolveService);
    service = TestBed.inject(IngredientCategoryService);
    resultIngredientCategory = undefined;
  });

  describe('resolve', () => {
    it('should return IIngredientCategory returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultIngredientCategory = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultIngredientCategory).toEqual({ id: 123 });
    });

    it('should return new IIngredientCategory if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultIngredientCategory = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultIngredientCategory).toEqual(new IngredientCategory());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as IngredientCategory })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultIngredientCategory = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultIngredientCategory).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
