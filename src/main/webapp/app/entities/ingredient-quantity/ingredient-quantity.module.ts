import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { IngredientQuantityComponent } from './list/ingredient-quantity.component';
import { IngredientQuantityDetailComponent } from './detail/ingredient-quantity-detail.component';
import { IngredientQuantityUpdateComponent } from './update/ingredient-quantity-update.component';
import { IngredientQuantityDeleteDialogComponent } from './delete/ingredient-quantity-delete-dialog.component';
import { IngredientQuantityRoutingModule } from './route/ingredient-quantity-routing.module';

@NgModule({
  imports: [SharedModule, IngredientQuantityRoutingModule],
  declarations: [
    IngredientQuantityComponent,
    IngredientQuantityDetailComponent,
    IngredientQuantityUpdateComponent,
    IngredientQuantityDeleteDialogComponent,
  ],
  entryComponents: [IngredientQuantityDeleteDialogComponent],
})
export class IngredientQuantityModule {}
