import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { UncompatibleIRCategoryComponent } from '../list/uncompatible-ir-category.component';
import { UncompatibleIRCategoryDetailComponent } from '../detail/uncompatible-ir-category-detail.component';
import { UncompatibleIRCategoryUpdateComponent } from '../update/uncompatible-ir-category-update.component';
import { UncompatibleIRCategoryRoutingResolveService } from './uncompatible-ir-category-routing-resolve.service';

const uncompatibleIRCategoryRoute: Routes = [
  {
    path: '',
    component: UncompatibleIRCategoryComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: UncompatibleIRCategoryDetailComponent,
    resolve: {
      uncompatibleIRCategory: UncompatibleIRCategoryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: UncompatibleIRCategoryUpdateComponent,
    resolve: {
      uncompatibleIRCategory: UncompatibleIRCategoryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: UncompatibleIRCategoryUpdateComponent,
    resolve: {
      uncompatibleIRCategory: UncompatibleIRCategoryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(uncompatibleIRCategoryRoute)],
  exports: [RouterModule],
})
export class UncompatibleIRCategoryRoutingModule {}
