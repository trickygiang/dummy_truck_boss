import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from 'app/shared/shared.module';
import { MAP_JOURNEY_ROUTE } from './map-journey.route';
import { MapJourneyComponent } from './map-journey.component';

@NgModule({
  imports: [SharedModule, RouterModule.forChild([MAP_JOURNEY_ROUTE])],
  declarations: [MapJourneyComponent],
})
export class MapJourneyModule {}
