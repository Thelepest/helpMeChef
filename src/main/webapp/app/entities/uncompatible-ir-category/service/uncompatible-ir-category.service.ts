import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IUncompatibleIRCategory, getUncompatibleIRCategoryIdentifier } from '../uncompatible-ir-category.model';

export type EntityResponseType = HttpResponse<IUncompatibleIRCategory>;
export type EntityArrayResponseType = HttpResponse<IUncompatibleIRCategory[]>;

@Injectable({ providedIn: 'root' })
export class UncompatibleIRCategoryService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/uncompatible-ir-categories');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(uncompatibleIRCategory: IUncompatibleIRCategory): Observable<EntityResponseType> {
    return this.http.post<IUncompatibleIRCategory>(this.resourceUrl, uncompatibleIRCategory, { observe: 'response' });
  }

  update(uncompatibleIRCategory: IUncompatibleIRCategory): Observable<EntityResponseType> {
    return this.http.put<IUncompatibleIRCategory>(
      `${this.resourceUrl}/${getUncompatibleIRCategoryIdentifier(uncompatibleIRCategory) as number}`,
      uncompatibleIRCategory,
      { observe: 'response' }
    );
  }

  partialUpdate(uncompatibleIRCategory: IUncompatibleIRCategory): Observable<EntityResponseType> {
    return this.http.patch<IUncompatibleIRCategory>(
      `${this.resourceUrl}/${getUncompatibleIRCategoryIdentifier(uncompatibleIRCategory) as number}`,
      uncompatibleIRCategory,
      { observe: 'response' }
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IUncompatibleIRCategory>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IUncompatibleIRCategory[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addUncompatibleIRCategoryToCollectionIfMissing(
    uncompatibleIRCategoryCollection: IUncompatibleIRCategory[],
    ...uncompatibleIRCategoriesToCheck: (IUncompatibleIRCategory | null | undefined)[]
  ): IUncompatibleIRCategory[] {
    const uncompatibleIRCategories: IUncompatibleIRCategory[] = uncompatibleIRCategoriesToCheck.filter(isPresent);
    if (uncompatibleIRCategories.length > 0) {
      const uncompatibleIRCategoryCollectionIdentifiers = uncompatibleIRCategoryCollection.map(
        uncompatibleIRCategoryItem => getUncompatibleIRCategoryIdentifier(uncompatibleIRCategoryItem)!
      );
      const uncompatibleIRCategoriesToAdd = uncompatibleIRCategories.filter(uncompatibleIRCategoryItem => {
        const uncompatibleIRCategoryIdentifier = getUncompatibleIRCategoryIdentifier(uncompatibleIRCategoryItem);
        if (
          uncompatibleIRCategoryIdentifier == null ||
          uncompatibleIRCategoryCollectionIdentifiers.includes(uncompatibleIRCategoryIdentifier)
        ) {
          return false;
        }
        uncompatibleIRCategoryCollectionIdentifiers.push(uncompatibleIRCategoryIdentifier);
        return true;
      });
      return [...uncompatibleIRCategoriesToAdd, ...uncompatibleIRCategoryCollection];
    }
    return uncompatibleIRCategoryCollection;
  }
}
