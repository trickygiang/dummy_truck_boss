import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IJourney } from '../journey.model';
import { JourneyService } from '../service/journey.service';

@Component({
  templateUrl: './journey-delete-dialog.component.html',
})
export class JourneyDeleteDialogComponent {
  journey?: IJourney;

  constructor(protected journeyService: JourneyService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.journeyService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
