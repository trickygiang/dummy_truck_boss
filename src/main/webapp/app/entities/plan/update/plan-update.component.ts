import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IPlan, Plan } from '../plan.model';
import { PlanService } from '../service/plan.service';

@Component({
  selector: 'jhi-plan-update',
  templateUrl: './plan-update.component.html',
})
export class PlanUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    startLat: [],
    startLong: [],
    endLat: [],
    endLong: [],
  });

  constructor(protected planService: PlanService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ plan }) => {
      this.updateForm(plan);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const plan = this.createFromForm();
    if (plan.id !== undefined) {
      this.subscribeToSaveResponse(this.planService.update(plan));
    } else {
      this.subscribeToSaveResponse(this.planService.create(plan));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPlan>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(plan: IPlan): void {
    this.editForm.patchValue({
      id: plan.id,
      startLat: plan.startLat,
      startLong: plan.startLong,
      endLat: plan.endLat,
      endLong: plan.endLong,
    });
  }

  protected createFromForm(): IPlan {
    return {
      ...new Plan(),
      id: this.editForm.get(['id'])!.value,
      startLat: this.editForm.get(['startLat'])!.value,
      startLong: this.editForm.get(['startLong'])!.value,
      endLat: this.editForm.get(['endLat'])!.value,
      endLong: this.editForm.get(['endLong'])!.value,
    };
  }
}
