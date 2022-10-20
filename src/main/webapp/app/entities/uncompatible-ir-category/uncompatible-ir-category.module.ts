import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { UncompatibleIRCategoryComponent } from './list/uncompatible-ir-category.component';
import { UncompatibleIRCategoryDetailComponent } from './detail/uncompatible-ir-category-detail.component';
import { UncompatibleIRCategoryUpdateComponent } from './update/uncompatible-ir-category-update.component';
import { UncompatibleIRCategoryDeleteDialogComponent } from './delete/uncompatible-ir-category-delete-dialog.component';
import { UncompatibleIRCategoryRoutingModule } from './route/uncompatible-ir-category-routing.module';

@NgModule({
  imports: [SharedModule, UncompatibleIRCategoryRoutingModule],
  declarations: [
    UncompatibleIRCategoryComponent,
    UncompatibleIRCategoryDetailComponent,
    UncompatibleIRCategoryUpdateComponent,
    UncompatibleIRCategoryDeleteDialogComponent,
  ],
  entryComponents: [UncompatibleIRCategoryDeleteDialogComponent],
})
export class UncompatibleIRCategoryModule {}
