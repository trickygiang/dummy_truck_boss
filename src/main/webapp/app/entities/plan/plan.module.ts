import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { PlanComponent } from './list/plan.component';
import { PlanDetailComponent } from './detail/plan-detail.component';
import { PlanUpdateComponent } from './update/plan-update.component';
import { PlanDeleteDialogComponent } from './delete/plan-delete-dialog.component';
import { PlanRoutingModule } from './route/plan-routing.module';

@NgModule({
  imports: [SharedModule, PlanRoutingModule],
  declarations: [PlanComponent, PlanDetailComponent, PlanUpdateComponent, PlanDeleteDialogComponent],
  entryComponents: [PlanDeleteDialogComponent],
})
export class PlanModule {}
