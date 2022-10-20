import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ToolComponent } from './list/tool.component';
import { ToolDetailComponent } from './detail/tool-detail.component';
import { ToolUpdateComponent } from './update/tool-update.component';
import { ToolDeleteDialogComponent } from './delete/tool-delete-dialog.component';
import { ToolRoutingModule } from './route/tool-routing.module';

@NgModule({
  imports: [SharedModule, ToolRoutingModule],
  declarations: [ToolComponent, ToolDetailComponent, ToolUpdateComponent, ToolDeleteDialogComponent],
  entryComponents: [ToolDeleteDialogComponent],
})
export class ToolModule {}
