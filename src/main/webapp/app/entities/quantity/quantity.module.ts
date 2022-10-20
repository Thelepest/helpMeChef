import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { QuantityComponent } from './list/quantity.component';
import { QuantityDetailComponent } from './detail/quantity-detail.component';
import { QuantityUpdateComponent } from './update/quantity-update.component';
import { QuantityDeleteDialogComponent } from './delete/quantity-delete-dialog.component';
import { QuantityRoutingModule } from './route/quantity-routing.module';

@NgModule({
  imports: [SharedModule, QuantityRoutingModule],
  declarations: [QuantityComponent, QuantityDetailComponent, QuantityUpdateComponent, QuantityDeleteDialogComponent],
  entryComponents: [QuantityDeleteDialogComponent],
})
export class QuantityModule {}
