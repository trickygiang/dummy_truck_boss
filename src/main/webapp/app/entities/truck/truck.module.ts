import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { TruckComponent } from './list/truck.component';
import { TruckDetailComponent } from './detail/truck-detail.component';
import { TruckUpdateComponent } from './update/truck-update.component';
import { TruckDeleteDialogComponent } from './delete/truck-delete-dialog.component';
import { TruckRoutingModule } from './route/truck-routing.module';

@NgModule({
  imports: [SharedModule, TruckRoutingModule],
  declarations: [TruckComponent, TruckDetailComponent, TruckUpdateComponent, TruckDeleteDialogComponent],
  entryComponents: [TruckDeleteDialogComponent],
})
export class TruckModule {}
