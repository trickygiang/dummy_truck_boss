import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IJourney, Journey } from '../journey.model';
import { JourneyService } from '../service/journey.service';
import { ISchedule } from 'app/entities/schedule/schedule.model';
import { ScheduleService } from 'app/entities/schedule/service/schedule.service';

@Component({
  selector: 'jhi-journey-update',
  templateUrl: './journey-update.component.html',
})
export class JourneyUpdateComponent implements OnInit {
  isSaving = false;

  schedulesSharedCollection: ISchedule[] = [];

  editForm = this.fb.group({
    id: [],
    startTime: [],
    endTime: [],
    status: [],
    schedule: [],
  });

  constructor(
    protected journeyService: JourneyService,
    protected scheduleService: ScheduleService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ journey }) => {
      if (journey.id === undefined) {
        const today = dayjs().startOf('day');
        journey.startTime = today;
        journey.endTime = today;
      }

      this.updateForm(journey);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const journey = this.createFromForm();
    if (journey.id !== undefined) {
      this.subscribeToSaveResponse(this.journeyService.update(journey));
    } else {
      this.subscribeToSaveResponse(this.journeyService.create(journey));
    }
  }

  trackScheduleById(index: number, item: ISchedule): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IJourney>>): void {
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

  protected updateForm(journey: IJourney): void {
    this.editForm.patchValue({
      id: journey.id,
      startTime: journey.startTime ? journey.startTime.format(DATE_TIME_FORMAT) : null,
      endTime: journey.endTime ? journey.endTime.format(DATE_TIME_FORMAT) : null,
      status: journey.status,
      schedule: journey.schedule,
    });

    this.schedulesSharedCollection = this.scheduleService.addScheduleToCollectionIfMissing(
      this.schedulesSharedCollection,
      journey.schedule
    );
  }

  protected loadRelationshipsOptions(): void {
    this.scheduleService
      .query()
      .pipe(map((res: HttpResponse<ISchedule[]>) => res.body ?? []))
      .pipe(
        map((schedules: ISchedule[]) =>
          this.scheduleService.addScheduleToCollectionIfMissing(schedules, this.editForm.get('schedule')!.value)
        )
      )
      .subscribe((schedules: ISchedule[]) => (this.schedulesSharedCollection = schedules));
  }

  protected createFromForm(): IJourney {
    return {
      ...new Journey(),
      id: this.editForm.get(['id'])!.value,
      startTime: this.editForm.get(['startTime'])!.value ? dayjs(this.editForm.get(['startTime'])!.value, DATE_TIME_FORMAT) : undefined,
      endTime: this.editForm.get(['endTime'])!.value ? dayjs(this.editForm.get(['endTime'])!.value, DATE_TIME_FORMAT) : undefined,
      status: this.editForm.get(['status'])!.value,
      schedule: this.editForm.get(['schedule'])!.value,
    };
  }
}
