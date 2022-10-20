import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IRecipeCategory, getRecipeCategoryIdentifier } from '../recipe-category.model';

export type EntityResponseType = HttpResponse<IRecipeCategory>;
export type EntityArrayResponseType = HttpResponse<IRecipeCategory[]>;

@Injectable({ providedIn: 'root' })
export class RecipeCategoryService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/recipe-categories');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(recipeCategory: IRecipeCategory): Observable<EntityResponseType> {
    return this.http.post<IRecipeCategory>(this.resourceUrl, recipeCategory, { observe: 'response' });
  }

  update(recipeCategory: IRecipeCategory): Observable<EntityResponseType> {
    return this.http.put<IRecipeCategory>(`${this.resourceUrl}/${getRecipeCategoryIdentifier(recipeCategory) as number}`, recipeCategory, {
      observe: 'response',
    });
  }

  partialUpdate(recipeCategory: IRecipeCategory): Observable<EntityResponseType> {
    return this.http.patch<IRecipeCategory>(
      `${this.resourceUrl}/${getRecipeCategoryIdentifier(recipeCategory) as number}`,
      recipeCategory,
      { observe: 'response' }
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IRecipeCategory>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IRecipeCategory[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addRecipeCategoryToCollectionIfMissing(
    recipeCategoryCollection: IRecipeCategory[],
    ...recipeCategoriesToCheck: (IRecipeCategory | null | undefined)[]
  ): IRecipeCategory[] {
    const recipeCategories: IRecipeCategory[] = recipeCategoriesToCheck.filter(isPresent);
    if (recipeCategories.length > 0) {
      const recipeCategoryCollectionIdentifiers = recipeCategoryCollection.map(
        recipeCategoryItem => getRecipeCategoryIdentifier(recipeCategoryItem)!
      );
      const recipeCategoriesToAdd = recipeCategories.filter(recipeCategoryItem => {
        const recipeCategoryIdentifier = getRecipeCategoryIdentifier(recipeCategoryItem);
        if (recipeCategoryIdentifier == null || recipeCategoryCollectionIdentifiers.includes(recipeCategoryIdentifier)) {
          return false;
        }
        recipeCategoryCollectionIdentifiers.push(recipeCategoryIdentifier);
        return true;
      });
      return [...recipeCategoriesToAdd, ...recipeCategoryCollection];
    }
    return recipeCategoryCollection;
  }
}
