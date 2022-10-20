import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IIngredientCategory, getIngredientCategoryIdentifier } from '../ingredient-category.model';

export type EntityResponseType = HttpResponse<IIngredientCategory>;
export type EntityArrayResponseType = HttpResponse<IIngredientCategory[]>;

@Injectable({ providedIn: 'root' })
export class IngredientCategoryService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/ingredient-categories');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(ingredientCategory: IIngredientCategory): Observable<EntityResponseType> {
    return this.http.post<IIngredientCategory>(this.resourceUrl, ingredientCategory, { observe: 'response' });
  }

  update(ingredientCategory: IIngredientCategory): Observable<EntityResponseType> {
    return this.http.put<IIngredientCategory>(
      `${this.resourceUrl}/${getIngredientCategoryIdentifier(ingredientCategory) as number}`,
      ingredientCategory,
      { observe: 'response' }
    );
  }

  partialUpdate(ingredientCategory: IIngredientCategory): Observable<EntityResponseType> {
    return this.http.patch<IIngredientCategory>(
      `${this.resourceUrl}/${getIngredientCategoryIdentifier(ingredientCategory) as number}`,
      ingredientCategory,
      { observe: 'response' }
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IIngredientCategory>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IIngredientCategory[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addIngredientCategoryToCollectionIfMissing(
    ingredientCategoryCollection: IIngredientCategory[],
    ...ingredientCategoriesToCheck: (IIngredientCategory | null | undefined)[]
  ): IIngredientCategory[] {
    const ingredientCategories: IIngredientCategory[] = ingredientCategoriesToCheck.filter(isPresent);
    if (ingredientCategories.length > 0) {
      const ingredientCategoryCollectionIdentifiers = ingredientCategoryCollection.map(
        ingredientCategoryItem => getIngredientCategoryIdentifier(ingredientCategoryItem)!
      );
      const ingredientCategoriesToAdd = ingredientCategories.filter(ingredientCategoryItem => {
        const ingredientCategoryIdentifier = getIngredientCategoryIdentifier(ingredientCategoryItem);
        if (ingredientCategoryIdentifier == null || ingredientCategoryCollectionIdentifiers.includes(ingredientCategoryIdentifier)) {
          return false;
        }
        ingredientCategoryCollectionIdentifiers.push(ingredientCategoryIdentifier);
        return true;
      });
      return [...ingredientCategoriesToAdd, ...ingredientCategoryCollection];
    }
    return ingredientCategoryCollection;
  }
}
