import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ToolComponent } from '../list/tool.component';
import { ToolDetailComponent } from '../detail/tool-detail.component';
import { ToolUpdateComponent } from '../update/tool-update.component';
import { ToolRoutingResolveService } from './tool-routing-resolve.service';

const toolRoute: Routes = [
  {
    path: '',
    component: ToolComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ToolDetailComponent,
    resolve: {
      tool: ToolRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ToolUpdateComponent,
    resolve: {
      tool: ToolRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ToolUpdateComponent,
    resolve: {
      tool: ToolRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(toolRoute)],
  exports: [RouterModule],
})
export class ToolRoutingModule {}
