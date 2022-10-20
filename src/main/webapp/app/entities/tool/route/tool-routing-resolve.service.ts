import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITool, Tool } from '../tool.model';
import { ToolService } from '../service/tool.service';

@Injectable({ providedIn: 'root' })
export class ToolRoutingResolveService implements Resolve<ITool> {
  constructor(protected service: ToolService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITool> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((tool: HttpResponse<Tool>) => {
          if (tool.body) {
            return of(tool.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Tool());
  }
}
