import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { PlanComponent } from '../list/plan.component';
import { PlanDetailComponent } from '../detail/plan-detail.component';
import { PlanUpdateComponent } from '../update/plan-update.component';
import { PlanRoutingResolveService } from './plan-routing-resolve.service';

const planRoute: Routes = [
  {
    path: '',
    component: PlanComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: PlanDetailComponent,
    resolve: {
      plan: PlanRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: PlanUpdateComponent,
    resolve: {
      plan: PlanRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: PlanUpdateComponent,
    resolve: {
      plan: PlanRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(planRoute)],
  exports: [RouterModule],
})
export class PlanRoutingModule {}
