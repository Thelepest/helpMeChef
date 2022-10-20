import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { QuantityComponent } from '../list/quantity.component';
import { QuantityDetailComponent } from '../detail/quantity-detail.component';
import { QuantityUpdateComponent } from '../update/quantity-update.component';
import { QuantityRoutingResolveService } from './quantity-routing-resolve.service';

const quantityRoute: Routes = [
  {
    path: '',
    component: QuantityComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: QuantityDetailComponent,
    resolve: {
      quantity: QuantityRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: QuantityUpdateComponent,
    resolve: {
      quantity: QuantityRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: QuantityUpdateComponent,
    resolve: {
      quantity: QuantityRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(quantityRoute)],
  exports: [RouterModule],
})
export class QuantityRoutingModule {}
