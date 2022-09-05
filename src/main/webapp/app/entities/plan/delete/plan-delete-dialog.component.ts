import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IPlan } from '../plan.model';
import { PlanService } from '../service/plan.service';

@Component({
  templateUrl: './plan-delete-dialog.component.html',
})
export class PlanDeleteDialogComponent {
  plan?: IPlan;

  constructor(protected planService: PlanService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.planService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
