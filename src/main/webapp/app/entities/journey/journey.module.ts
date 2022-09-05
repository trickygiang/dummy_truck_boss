import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { JourneyComponent } from './list/journey.component';
import { JourneyDetailComponent } from './detail/journey-detail.component';
import { JourneyUpdateComponent } from './update/journey-update.component';
import { JourneyDeleteDialogComponent } from './delete/journey-delete-dialog.component';
import { JourneyRoutingModule } from './route/journey-routing.module';

@NgModule({
  imports: [SharedModule, JourneyRoutingModule],
  declarations: [JourneyComponent, JourneyDetailComponent, JourneyUpdateComponent, JourneyDeleteDialogComponent],
  entryComponents: [JourneyDeleteDialogComponent],
})
export class JourneyModule {}
