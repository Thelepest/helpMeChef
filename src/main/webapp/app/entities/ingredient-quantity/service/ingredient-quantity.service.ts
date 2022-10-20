import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IIngredientQuantity, getIngredientQuantityIdentifier } from '../ingredient-quantity.model';

export type EntityResponseType = HttpResponse<IIngredientQuantity>;
export type EntityArrayResponseType = HttpResponse<IIngredientQuantity[]>;

@Injectable({ providedIn: 'root' })
export class IngredientQuantityService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/ingredient-quantities');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(ingredientQuantity: IIngredientQuantity): Observable<EntityResponseType> {
    return this.http.post<IIngredientQuantity>(this.resourceUrl, ingredientQuantity, { observe: 'response' });
  }

  update(ingredientQuantity: IIngredientQuantity): Observable<EntityResponseType> {
    return this.http.put<IIngredientQuantity>(
      `${this.resourceUrl}/${getIngredientQuantityIdentifier(ingredientQuantity) as number}`,
      ingredientQuantity,
      { observe: 'response' }
    );
  }

  partialUpdate(ingredientQuantity: IIngredientQuantity): Observable<EntityResponseType> {
    return this.http.patch<IIngredientQuantity>(
      `${this.resourceUrl}/${getIngredientQuantityIdentifier(ingredientQuantity) as number}`,
      ingredientQuantity,
      { observe: 'response' }
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IIngredientQuantity>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IIngredientQuantity[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addIngredientQuantityToCollectionIfMissing(
    ingredientQuantityCollection: IIngredientQuantity[],
    ...ingredientQuantitiesToCheck: (IIngredientQuantity | null | undefined)[]
  ): IIngredientQuantity[] {
    const ingredientQuantities: IIngredientQuantity[] = ingredientQuantitiesToCheck.filter(isPresent);
    if (ingredientQuantities.length > 0) {
      const ingredientQuantityCollectionIdentifiers = ingredientQuantityCollection.map(
        ingredientQuantityItem => getIngredientQuantityIdentifier(ingredientQuantityItem)!
      );
      const ingredientQuantitiesToAdd = ingredientQuantities.filter(ingredientQuantityItem => {
        const ingredientQuantityIdentifier = getIngredientQuantityIdentifier(ingredientQuantityItem);
        if (ingredientQuantityIdentifier == null || ingredientQuantityCollectionIdentifiers.includes(ingredientQuantityIdentifier)) {
          return false;
        }
        ingredientQuantityCollectionIdentifiers.push(ingredientQuantityIdentifier);
        return true;
      });
      return [...ingredientQuantitiesToAdd, ...ingredientQuantityCollection];
    }
    return ingredientQuantityCollection;
  }
}
