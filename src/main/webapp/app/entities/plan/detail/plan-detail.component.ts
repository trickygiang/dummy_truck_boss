import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IPlan } from '../plan.model';

@Component({
  selector: 'jhi-plan-detail',
  templateUrl: './plan-detail.component.html',
})
export class PlanDetailComponent implements OnInit {
  plan: IPlan | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ plan }) => {
      this.plan = plan;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
