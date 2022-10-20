import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IRecipeCategory, RecipeCategory } from '../recipe-category.model';
import { RecipeCategoryService } from '../service/recipe-category.service';

@Injectable({ providedIn: 'root' })
export class RecipeCategoryRoutingResolveService implements Resolve<IRecipeCategory> {
  constructor(protected service: RecipeCategoryService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IRecipeCategory> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((recipeCategory: HttpResponse<RecipeCategory>) => {
          if (recipeCategory.body) {
            return of(recipeCategory.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new RecipeCategory());
  }
}
