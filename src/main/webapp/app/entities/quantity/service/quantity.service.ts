import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IQuantity, getQuantityIdentifier } from '../quantity.model';

export type EntityResponseType = HttpResponse<IQuantity>;
export type EntityArrayResponseType = HttpResponse<IQuantity[]>;

@Injectable({ providedIn: 'root' })
export class QuantityService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/quantities');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(quantity: IQuantity): Observable<EntityResponseType> {
    return this.http.post<IQuantity>(this.resourceUrl, quantity, { observe: 'response' });
  }

  update(quantity: IQuantity): Observable<EntityResponseType> {
    return this.http.put<IQuantity>(`${this.resourceUrl}/${getQuantityIdentifier(quantity) as number}`, quantity, { observe: 'response' });
  }

  partialUpdate(quantity: IQuantity): Observable<EntityResponseType> {
    return this.http.patch<IQuantity>(`${this.resourceUrl}/${getQuantityIdentifier(quantity) as number}`, quantity, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IQuantity>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IQuantity[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addQuantityToCollectionIfMissing(quantityCollection: IQuantity[], ...quantitiesToCheck: (IQuantity | null | undefined)[]): IQuantity[] {
    const quantities: IQuantity[] = quantitiesToCheck.filter(isPresent);
    if (quantities.length > 0) {
      const quantityCollectionIdentifiers = quantityCollection.map(quantityItem => getQuantityIdentifier(quantityItem)!);
      const quantitiesToAdd = quantities.filter(quantityItem => {
        const quantityIdentifier = getQuantityIdentifier(quantityItem);
        if (quantityIdentifier == null || quantityCollectionIdentifiers.includes(quantityIdentifier)) {
          return false;
        }
        quantityCollectionIdentifiers.push(quantityIdentifier);
        return true;
      });
      return [...quantitiesToAdd, ...quantityCollection];
    }
    return quantityCollection;
  }
}
