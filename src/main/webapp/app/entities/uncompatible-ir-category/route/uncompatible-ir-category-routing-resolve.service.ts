import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IUncompatibleIRCategory, UncompatibleIRCategory } from '../uncompatible-ir-category.model';
import { UncompatibleIRCategoryService } from '../service/uncompatible-ir-category.service';

@Injectable({ providedIn: 'root' })
export class UncompatibleIRCategoryRoutingResolveService implements Resolve<IUncompatibleIRCategory> {
  constructor(protected service: UncompatibleIRCategoryService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IUncompatibleIRCategory> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((uncompatibleIRCategory: HttpResponse<UncompatibleIRCategory>) => {
          if (uncompatibleIRCategory.body) {
            return of(uncompatibleIRCategory.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new UncompatibleIRCategory());
  }
}
