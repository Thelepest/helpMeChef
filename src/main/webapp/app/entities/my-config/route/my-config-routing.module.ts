import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { MyConfigComponent } from '../list/my-config.component';
import { MyConfigDetailComponent } from '../detail/my-config-detail.component';
import { MyConfigUpdateComponent } from '../update/my-config-update.component';
import { MyConfigRoutingResolveService } from './my-config-routing-resolve.service';

const myConfigRoute: Routes = [
  {
    path: '',
    component: MyConfigComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: MyConfigDetailComponent,
    resolve: {
      myConfig: MyConfigRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: MyConfigUpdateComponent,
    resolve: {
      myConfig: MyConfigRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: MyConfigUpdateComponent,
    resolve: {
      myConfig: MyConfigRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(myConfigRoute)],
  exports: [RouterModule],
})
export class MyConfigRoutingModule {}
