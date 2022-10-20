import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IMyConfig, MyConfig } from '../my-config.model';
import { MyConfigService } from '../service/my-config.service';

@Injectable({ providedIn: 'root' })
export class MyConfigRoutingResolveService implements Resolve<IMyConfig> {
  constructor(protected service: MyConfigService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IMyConfig> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((myConfig: HttpResponse<MyConfig>) => {
          if (myConfig.body) {
            return of(myConfig.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new MyConfig());
  }
}
