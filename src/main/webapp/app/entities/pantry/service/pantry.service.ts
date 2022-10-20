import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPantry, getPantryIdentifier } from '../pantry.model';

export type EntityResponseType = HttpResponse<IPantry>;
export type EntityArrayResponseType = HttpResponse<IPantry[]>;

@Injectable({ providedIn: 'root' })
export class PantryService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/pantries');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(pantry: IPantry): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(pantry);
    return this.http
      .post<IPantry>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(pantry: IPantry): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(pantry);
    return this.http
      .put<IPantry>(`${this.resourceUrl}/${getPantryIdentifier(pantry) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(pantry: IPantry): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(pantry);
    return this.http
      .patch<IPantry>(`${this.resourceUrl}/${getPantryIdentifier(pantry) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IPantry>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IPantry[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addPantryToCollectionIfMissing(pantryCollection: IPantry[], ...pantriesToCheck: (IPantry | null | undefined)[]): IPantry[] {
    const pantries: IPantry[] = pantriesToCheck.filter(isPresent);
    if (pantries.length > 0) {
      const pantryCollectionIdentifiers = pantryCollection.map(pantryItem => getPantryIdentifier(pantryItem)!);
      const pantriesToAdd = pantries.filter(pantryItem => {
        const pantryIdentifier = getPantryIdentifier(pantryItem);
        if (pantryIdentifier == null || pantryCollectionIdentifiers.includes(pantryIdentifier)) {
          return false;
        }
        pantryCollectionIdentifiers.push(pantryIdentifier);
        return true;
      });
      return [...pantriesToAdd, ...pantryCollection];
    }
    return pantryCollection;
  }

  protected convertDateFromClient(pantry: IPantry): IPantry {
    return Object.assign({}, pantry, {
      createdAt: pantry.createdAt?.isValid() ? pantry.createdAt.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.createdAt = res.body.createdAt ? dayjs(res.body.createdAt) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((pantry: IPantry) => {
        pantry.createdAt = pantry.createdAt ? dayjs(pantry.createdAt) : undefined;
      });
    }
    return res;
  }
}
