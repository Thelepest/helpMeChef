import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { PantryComponent } from './list/pantry.component';
import { PantryDetailComponent } from './detail/pantry-detail.component';
import { PantryUpdateComponent } from './update/pantry-update.component';
import { PantryDeleteDialogComponent } from './delete/pantry-delete-dialog.component';
import { PantryRoutingModule } from './route/pantry-routing.module';

@NgModule({
    imports: [SharedModule, PantryRoutingModule],
    declarations: [PantryComponent, PantryDetailComponent, PantryUpdateComponent, PantryDeleteDialogComponent]
})
export class PantryModule {}
