import { Route } from '@angular/router';

import { MapJourneyComponent } from './map-journey.component';

export const MAP_JOURNEY_ROUTE: Route = {
  path: 'map-journey',
  component: MapJourneyComponent,
  data: {
    pageTitle: 'mapJourney.title',
  },
};
