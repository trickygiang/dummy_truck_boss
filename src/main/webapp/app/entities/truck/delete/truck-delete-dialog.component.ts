import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ITruck } from '../truck.model';
import { TruckService } from '../service/truck.service';

@Component({
  templateUrl: './truck-delete-dialog.component.html',
})
export class TruckDeleteDialogComponent {
  truck?: ITruck;

  constructor(protected truckService: TruckService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.truckService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
