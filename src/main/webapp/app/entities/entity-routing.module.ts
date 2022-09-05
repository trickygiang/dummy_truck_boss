import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'truck',
        data: { pageTitle: 'truckBossApp.truck.home.title' },
        loadChildren: () => import('./truck/truck.module').then(m => m.TruckModule),
      },
      {
        path: 'plan',
        data: { pageTitle: 'truckBossApp.plan.home.title' },
        loadChildren: () => import('./plan/plan.module').then(m => m.PlanModule),
      },
      {
        path: 'schedule',
        data: { pageTitle: 'truckBossApp.schedule.home.title' },
        loadChildren: () => import('./schedule/schedule.module').then(m => m.ScheduleModule),
      },
      {
        path: 'journey',
        data: { pageTitle: 'truckBossApp.journey.home.title' },
        loadChildren: () => import('./journey/journey.module').then(m => m.JourneyModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
