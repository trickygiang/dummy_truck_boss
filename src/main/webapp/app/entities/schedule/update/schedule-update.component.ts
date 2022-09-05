import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ISchedule, Schedule } from '../schedule.model';
import { ScheduleService } from '../service/schedule.service';
import { ITruck } from 'app/entities/truck/truck.model';
import { TruckService } from 'app/entities/truck/service/truck.service';
import { IPlan } from 'app/entities/plan/plan.model';
import { PlanService } from 'app/entities/plan/service/plan.service';

@Component({
  selector: 'jhi-schedule-update',
  templateUrl: './schedule-update.component.html',
})
export class ScheduleUpdateComponent implements OnInit {
  isSaving = false;

  trucksSharedCollection: ITruck[] = [];
  plansSharedCollection: IPlan[] = [];

  editForm = this.fb.group({
    id: [],
    status: [],
    truck: [],
    plan: [],
  });

  constructor(
    protected scheduleService: ScheduleService,
    protected truckService: TruckService,
    protected planService: PlanService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ schedule }) => {
      this.updateForm(schedule);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const schedule = this.createFromForm();
    if (schedule.id !== undefined) {
      this.subscribeToSaveResponse(this.scheduleService.update(schedule));
    } else {
      this.subscribeToSaveResponse(this.scheduleService.create(schedule));
    }
  }

  trackTruckById(index: number, item: ITruck): number {
    return item.id!;
  }

  trackPlanById(index: number, item: IPlan): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISchedule>>): void {
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

  protected updateForm(schedule: ISchedule): void {
    this.editForm.patchValue({
      id: schedule.id,
      status: schedule.status,
      truck: schedule.truck,
      plan: schedule.plan,
    });

    this.trucksSharedCollection = this.truckService.addTruckToCollectionIfMissing(this.trucksSharedCollection, schedule.truck);
    this.plansSharedCollection = this.planService.addPlanToCollectionIfMissing(this.plansSharedCollection, schedule.plan);
  }

  protected loadRelationshipsOptions(): void {
    this.truckService
      .query()
      .pipe(map((res: HttpResponse<ITruck[]>) => res.body ?? []))
      .pipe(map((trucks: ITruck[]) => this.truckService.addTruckToCollectionIfMissing(trucks, this.editForm.get('truck')!.value)))
      .subscribe((trucks: ITruck[]) => (this.trucksSharedCollection = trucks));

    this.planService
      .query()
      .pipe(map((res: HttpResponse<IPlan[]>) => res.body ?? []))
      .pipe(map((plans: IPlan[]) => this.planService.addPlanToCollectionIfMissing(plans, this.editForm.get('plan')!.value)))
      .subscribe((plans: IPlan[]) => (this.plansSharedCollection = plans));
  }

  protected createFromForm(): ISchedule {
    return {
      ...new Schedule(),
      id: this.editForm.get(['id'])!.value,
      status: this.editForm.get(['status'])!.value,
      truck: this.editForm.get(['truck'])!.value,
      plan: this.editForm.get(['plan'])!.value,
    };
  }
}
