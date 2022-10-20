import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { IRecipeCategory, RecipeCategory } from '../recipe-category.model';
import { RecipeCategoryService } from '../service/recipe-category.service';

import { RecipeCategoryRoutingResolveService } from './recipe-category-routing-resolve.service';

describe('RecipeCategory routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: RecipeCategoryRoutingResolveService;
  let service: RecipeCategoryService;
  let resultRecipeCategory: IRecipeCategory | undefined;

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
    routingResolveService = TestBed.inject(RecipeCategoryRoutingResolveService);
    service = TestBed.inject(RecipeCategoryService);
    resultRecipeCategory = undefined;
  });

  describe('resolve', () => {
    it('should return IRecipeCategory returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultRecipeCategory = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultRecipeCategory).toEqual({ id: 123 });
    });

    it('should return new IRecipeCategory if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultRecipeCategory = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultRecipeCategory).toEqual(new RecipeCategory());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as RecipeCategory })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultRecipeCategory = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultRecipeCategory).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
