import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { MyConfigComponent } from './list/my-config.component';
import { MyConfigDetailComponent } from './detail/my-config-detail.component';
import { MyConfigUpdateComponent } from './update/my-config-update.component';
import { MyConfigDeleteDialogComponent } from './delete/my-config-delete-dialog.component';
import { MyConfigRoutingModule } from './route/my-config-routing.module';

@NgModule({
  imports: [SharedModule, MyConfigRoutingModule],
  declarations: [MyConfigComponent, MyConfigDetailComponent, MyConfigUpdateComponent, MyConfigDeleteDialogComponent],
  entryComponents: [MyConfigDeleteDialogComponent],
})
export class MyConfigModule {}
