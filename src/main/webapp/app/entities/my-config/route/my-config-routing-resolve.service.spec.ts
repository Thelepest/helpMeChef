import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { IMyConfig, MyConfig } from '../my-config.model';
import { MyConfigService } from '../service/my-config.service';

import { MyConfigRoutingResolveService } from './my-config-routing-resolve.service';

describe('MyConfig routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: MyConfigRoutingResolveService;
  let service: MyConfigService;
  let resultMyConfig: IMyConfig | undefined;

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
    routingResolveService = TestBed.inject(MyConfigRoutingResolveService);
    service = TestBed.inject(MyConfigService);
    resultMyConfig = undefined;
  });

  describe('resolve', () => {
    it('should return IMyConfig returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultMyConfig = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultMyConfig).toEqual({ id: 123 });
    });

    it('should return new IMyConfig if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultMyConfig = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultMyConfig).toEqual(new MyConfig());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as MyConfig })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultMyConfig = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultMyConfig).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
