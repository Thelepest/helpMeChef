import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IMyConfig, getMyConfigIdentifier } from '../my-config.model';

export type EntityResponseType = HttpResponse<IMyConfig>;
export type EntityArrayResponseType = HttpResponse<IMyConfig[]>;

@Injectable({ providedIn: 'root' })
export class MyConfigService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/my-configs');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(myConfig: IMyConfig): Observable<EntityResponseType> {
    return this.http.post<IMyConfig>(this.resourceUrl, myConfig, { observe: 'response' });
  }

  update(myConfig: IMyConfig): Observable<EntityResponseType> {
    return this.http.put<IMyConfig>(`${this.resourceUrl}/${getMyConfigIdentifier(myConfig) as number}`, myConfig, { observe: 'response' });
  }

  partialUpdate(myConfig: IMyConfig): Observable<EntityResponseType> {
    return this.http.patch<IMyConfig>(`${this.resourceUrl}/${getMyConfigIdentifier(myConfig) as number}`, myConfig, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IMyConfig>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IMyConfig[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addMyConfigToCollectionIfMissing(myConfigCollection: IMyConfig[], ...myConfigsToCheck: (IMyConfig | null | undefined)[]): IMyConfig[] {
    const myConfigs: IMyConfig[] = myConfigsToCheck.filter(isPresent);
    if (myConfigs.length > 0) {
      const myConfigCollectionIdentifiers = myConfigCollection.map(myConfigItem => getMyConfigIdentifier(myConfigItem)!);
      const myConfigsToAdd = myConfigs.filter(myConfigItem => {
        const myConfigIdentifier = getMyConfigIdentifier(myConfigItem);
        if (myConfigIdentifier == null || myConfigCollectionIdentifiers.includes(myConfigIdentifier)) {
          return false;
        }
        myConfigCollectionIdentifiers.push(myConfigIdentifier);
        return true;
      });
      return [...myConfigsToAdd, ...myConfigCollection];
    }
    return myConfigCollection;
  }
}
