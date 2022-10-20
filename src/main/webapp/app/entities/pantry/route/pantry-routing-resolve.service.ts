import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPantry, Pantry } from '../pantry.model';
import { PantryService } from '../service/pantry.service';

@Injectable({ providedIn: 'root' })
export class PantryRoutingResolveService implements Resolve<IPantry> {
  constructor(protected service: PantryService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IPantry> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((pantry: HttpResponse<Pantry>) => {
          if (pantry.body) {
            return of(pantry.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Pantry());
  }
}
