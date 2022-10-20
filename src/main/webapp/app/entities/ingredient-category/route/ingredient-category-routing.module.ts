import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { IngredientCategoryComponent } from '../list/ingredient-category.component';
import { IngredientCategoryDetailComponent } from '../detail/ingredient-category-detail.component';
import { IngredientCategoryUpdateComponent } from '../update/ingredient-category-update.component';
import { IngredientCategoryRoutingResolveService } from './ingredient-category-routing-resolve.service';

const ingredientCategoryRoute: Routes = [
  {
    path: '',
    component: IngredientCategoryComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: IngredientCategoryDetailComponent,
    resolve: {
      ingredientCategory: IngredientCategoryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: IngredientCategoryUpdateComponent,
    resolve: {
      ingredientCategory: IngredientCategoryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: IngredientCategoryUpdateComponent,
    resolve: {
      ingredientCategory: IngredientCategoryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(ingredientCategoryRoute)],
  exports: [RouterModule],
})
export class IngredientCategoryRoutingModule {}
