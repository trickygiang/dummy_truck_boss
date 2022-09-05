import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { JourneyComponent } from '../list/journey.component';
import { JourneyDetailComponent } from '../detail/journey-detail.component';
import { JourneyUpdateComponent } from '../update/journey-update.component';
import { JourneyRoutingResolveService } from './journey-routing-resolve.service';

const journeyRoute: Routes = [
  {
    path: '',
    component: JourneyComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: JourneyDetailComponent,
    resolve: {
      journey: JourneyRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: JourneyUpdateComponent,
    resolve: {
      journey: JourneyRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: JourneyUpdateComponent,
    resolve: {
      journey: JourneyRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(journeyRoute)],
  exports: [RouterModule],
})
export class JourneyRoutingModule {}
