import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { PantryComponent } from '../list/pantry.component';
import { PantryDetailComponent } from '../detail/pantry-detail.component';
import { PantryUpdateComponent } from '../update/pantry-update.component';
import { PantryRoutingResolveService } from './pantry-routing-resolve.service';

const pantryRoute: Routes = [
  {
    path: '',
    component: PantryComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: PantryDetailComponent,
    resolve: {
      pantry: PantryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: PantryUpdateComponent,
    resolve: {
      pantry: PantryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: PantryUpdateComponent,
    resolve: {
      pantry: PantryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(pantryRoute)],
  exports: [RouterModule],
})
export class PantryRoutingModule {}
