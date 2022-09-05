import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ITruck, Truck } from '../truck.model';
import { TruckService } from '../service/truck.service';

@Component({
  selector: 'jhi-truck-update',
  templateUrl: './truck-update.component.html',
})
export class TruckUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    code: [],
    model: [],
    registrationDate: [],
    monthDue: [],
  });

  constructor(protected truckService: TruckService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ truck }) => {
      this.updateForm(truck);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const truck = this.createFromForm();
    if (truck.id !== undefined) {
      this.subscribeToSaveResponse(this.truckService.update(truck));
    } else {
      this.subscribeToSaveResponse(this.truckService.create(truck));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITruck>>): void {
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

  protected updateForm(truck: ITruck): void {
    this.editForm.patchValue({
      id: truck.id,
      code: truck.code,
      model: truck.model,
      registrationDate: truck.registrationDate,
      monthDue: truck.monthDue,
    });
  }

  protected createFromForm(): ITruck {
    return {
      ...new Truck(),
      id: this.editForm.get(['id'])!.value,
      code: this.editForm.get(['code'])!.value,
      model: this.editForm.get(['model'])!.value,
      registrationDate: this.editForm.get(['registrationDate'])!.value,
      monthDue: this.editForm.get(['monthDue'])!.value,
    };
  }
}
