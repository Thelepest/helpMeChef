import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { IngredientCategoryComponent } from './list/ingredient-category.component';
import { IngredientCategoryDetailComponent } from './detail/ingredient-category-detail.component';
import { IngredientCategoryUpdateComponent } from './update/ingredient-category-update.component';
import { IngredientCategoryDeleteDialogComponent } from './delete/ingredient-category-delete-dialog.component';
import { IngredientCategoryRoutingModule } from './route/ingredient-category-routing.module';

@NgModule({
  imports: [SharedModule, IngredientCategoryRoutingModule],
  declarations: [
    IngredientCategoryComponent,
    IngredientCategoryDetailComponent,
    IngredientCategoryUpdateComponent,
    IngredientCategoryDeleteDialogComponent,
  ],
  entryComponents: [IngredientCategoryDeleteDialogComponent],
})
export class IngredientCategoryModule {}
