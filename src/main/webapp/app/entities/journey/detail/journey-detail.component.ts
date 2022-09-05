import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IJourney } from '../journey.model';

@Component({
  selector: 'jhi-journey-detail',
  templateUrl: './journey-detail.component.html',
})
export class JourneyDetailComponent implements OnInit {
  journey: IJourney | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ journey }) => {
      this.journey = journey;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
