import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { IngredientQuantityComponent } from '../list/ingredient-quantity.component';
import { IngredientQuantityDetailComponent } from '../detail/ingredient-quantity-detail.component';
import { IngredientQuantityUpdateComponent } from '../update/ingredient-quantity-update.component';
import { IngredientQuantityRoutingResolveService } from './ingredient-quantity-routing-resolve.service';

const ingredientQuantityRoute: Routes = [
  {
    path: '',
    component: IngredientQuantityComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: IngredientQuantityDetailComponent,
    resolve: {
      ingredientQuantity: IngredientQuantityRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: IngredientQuantityUpdateComponent,
    resolve: {
      ingredientQuantity: IngredientQuantityRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: IngredientQuantityUpdateComponent,
    resolve: {
      ingredientQuantity: IngredientQuantityRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(ingredientQuantityRoute)],
  exports: [RouterModule],
})
export class IngredientQuantityRoutingModule {}
