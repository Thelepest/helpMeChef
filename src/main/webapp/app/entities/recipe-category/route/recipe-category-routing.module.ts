import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { RecipeCategoryComponent } from '../list/recipe-category.component';
import { RecipeCategoryDetailComponent } from '../detail/recipe-category-detail.component';
import { RecipeCategoryUpdateComponent } from '../update/recipe-category-update.component';
import { RecipeCategoryRoutingResolveService } from './recipe-category-routing-resolve.service';

const recipeCategoryRoute: Routes = [
  {
    path: '',
    component: RecipeCategoryComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: RecipeCategoryDetailComponent,
    resolve: {
      recipeCategory: RecipeCategoryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: RecipeCategoryUpdateComponent,
    resolve: {
      recipeCategory: RecipeCategoryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: RecipeCategoryUpdateComponent,
    resolve: {
      recipeCategory: RecipeCategoryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(recipeCategoryRoute)],
  exports: [RouterModule],
})
export class RecipeCategoryRoutingModule {}
