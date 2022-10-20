import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IIngredientQuantity, IngredientQuantity } from '../ingredient-quantity.model';
import { IngredientQuantityService } from '../service/ingredient-quantity.service';

@Injectable({ providedIn: 'root' })
export class IngredientQuantityRoutingResolveService implements Resolve<IIngredientQuantity> {
  constructor(protected service: IngredientQuantityService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IIngredientQuantity> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((ingredientQuantity: HttpResponse<IngredientQuantity>) => {
          if (ingredientQuantity.body) {
            return of(ingredientQuantity.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new IngredientQuantity());
  }
}
