import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IQuantity, Quantity } from '../quantity.model';
import { QuantityService } from '../service/quantity.service';

@Injectable({ providedIn: 'root' })
export class QuantityRoutingResolveService implements Resolve<IQuantity> {
  constructor(protected service: QuantityService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IQuantity> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((quantity: HttpResponse<Quantity>) => {
          if (quantity.body) {
            return of(quantity.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Quantity());
  }
}
