import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { RecipeCategoryComponent } from './list/recipe-category.component';
import { RecipeCategoryDetailComponent } from './detail/recipe-category-detail.component';
import { RecipeCategoryUpdateComponent } from './update/recipe-category-update.component';
import { RecipeCategoryDeleteDialogComponent } from './delete/recipe-category-delete-dialog.component';
import { RecipeCategoryRoutingModule } from './route/recipe-category-routing.module';

@NgModule({
    imports: [SharedModule, RecipeCategoryRoutingModule],
    declarations: [
        RecipeCategoryComponent,
        RecipeCategoryDetailComponent,
        RecipeCategoryUpdateComponent,
        RecipeCategoryDeleteDialogComponent,
    ]
})
export class RecipeCategoryModule {}
