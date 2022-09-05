import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { TruckComponent } from '../list/truck.component';
import { TruckDetailComponent } from '../detail/truck-detail.component';
import { TruckUpdateComponent } from '../update/truck-update.component';
import { TruckRoutingResolveService } from './truck-routing-resolve.service';

const truckRoute: Routes = [
  {
    path: '',
    component: TruckComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TruckDetailComponent,
    resolve: {
      truck: TruckRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TruckUpdateComponent,
    resolve: {
      truck: TruckRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TruckUpdateComponent,
    resolve: {
      truck: TruckRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(truckRoute)],
  exports: [RouterModule],
})
export class TruckRoutingModule {}
