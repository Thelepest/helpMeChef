import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITool, getToolIdentifier } from '../tool.model';

export type EntityResponseType = HttpResponse<ITool>;
export type EntityArrayResponseType = HttpResponse<ITool[]>;

@Injectable({ providedIn: 'root' })
export class ToolService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/tools');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(tool: ITool): Observable<EntityResponseType> {
    return this.http.post<ITool>(this.resourceUrl, tool, { observe: 'response' });
  }

  update(tool: ITool): Observable<EntityResponseType> {
    return this.http.put<ITool>(`${this.resourceUrl}/${getToolIdentifier(tool) as number}`, tool, { observe: 'response' });
  }

  partialUpdate(tool: ITool): Observable<EntityResponseType> {
    return this.http.patch<ITool>(`${this.resourceUrl}/${getToolIdentifier(tool) as number}`, tool, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITool>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITool[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addToolToCollectionIfMissing(toolCollection: ITool[], ...toolsToCheck: (ITool | null | undefined)[]): ITool[] {
    const tools: ITool[] = toolsToCheck.filter(isPresent);
    if (tools.length > 0) {
      const toolCollectionIdentifiers = toolCollection.map(toolItem => getToolIdentifier(toolItem)!);
      const toolsToAdd = tools.filter(toolItem => {
        const toolIdentifier = getToolIdentifier(toolItem);
        if (toolIdentifier == null || toolCollectionIdentifiers.includes(toolIdentifier)) {
          return false;
        }
        toolCollectionIdentifiers.push(toolIdentifier);
        return true;
      });
      return [...toolsToAdd, ...toolCollection];
    }
    return toolCollection;
  }
}
