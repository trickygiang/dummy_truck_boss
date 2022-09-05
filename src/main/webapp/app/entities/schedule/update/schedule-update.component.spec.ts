import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ScheduleService } from '../service/schedule.service';
import { ISchedule, Schedule } from '../schedule.model';
import { ITruck } from 'app/entities/truck/truck.model';
import { TruckService } from 'app/entities/truck/service/truck.service';
import { IPlan } from 'app/entities/plan/plan.model';
import { PlanService } from 'app/entities/plan/service/plan.service';

import { ScheduleUpdateComponent } from './schedule-update.component';

describe('Schedule Management Update Component', () => {
  let comp: ScheduleUpdateComponent;
  let fixture: ComponentFixture<ScheduleUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let scheduleService: ScheduleService;
  let truckService: TruckService;
  let planService: PlanService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ScheduleUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(ScheduleUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ScheduleUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    scheduleService = TestBed.inject(ScheduleService);
    truckService = TestBed.inject(TruckService);
    planService = TestBed.inject(PlanService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Truck query and add missing value', () => {
      const schedule: ISchedule = { id: 456 };
      const truck: ITruck = { id: 95998 };
      schedule.truck = truck;

      const truckCollection: ITruck[] = [{ id: 15525 }];
      jest.spyOn(truckService, 'query').mockReturnValue(of(new HttpResponse({ body: truckCollection })));
      const additionalTrucks = [truck];
      const expectedCollection: ITruck[] = [...additionalTrucks, ...truckCollection];
      jest.spyOn(truckService, 'addTruckToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ schedule });
      comp.ngOnInit();

      expect(truckService.query).toHaveBeenCalled();
      expect(truckService.addTruckToCollectionIfMissing).toHaveBeenCalledWith(truckCollection, ...additionalTrucks);
      expect(comp.trucksSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Plan query and add missing value', () => {
      const schedule: ISchedule = { id: 456 };
      const plan: IPlan = { id: 34253 };
      schedule.plan = plan;

      const planCollection: IPlan[] = [{ id: 74899 }];
      jest.spyOn(planService, 'query').mockReturnValue(of(new HttpResponse({ body: planCollection })));
      const additionalPlans = [plan];
      const expectedCollection: IPlan[] = [...additionalPlans, ...planCollection];
      jest.spyOn(planService, 'addPlanToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ schedule });
      comp.ngOnInit();

      expect(planService.query).toHaveBeenCalled();
      expect(planService.addPlanToCollectionIfMissing).toHaveBeenCalledWith(planCollection, ...additionalPlans);
      expect(comp.plansSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const schedule: ISchedule = { id: 456 };
      const truck: ITruck = { id: 95830 };
      schedule.truck = truck;
      const plan: IPlan = { id: 27714 };
      schedule.plan = plan;

      activatedRoute.data = of({ schedule });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(schedule));
      expect(comp.trucksSharedCollection).toContain(truck);
      expect(comp.plansSharedCollection).toContain(plan);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Schedule>>();
      const schedule = { id: 123 };
      jest.spyOn(scheduleService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ schedule });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: schedule }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(scheduleService.update).toHaveBeenCalledWith(schedule);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Schedule>>();
      const schedule = new Schedule();
      jest.spyOn(scheduleService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ schedule });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: schedule }));
      saveSubject.complete();

      // THEN
      expect(scheduleService.create).toHaveBeenCalledWith(schedule);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Schedule>>();
      const schedule = { id: 123 };
      jest.spyOn(scheduleService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ schedule });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(scheduleService.update).toHaveBeenCalledWith(schedule);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackTruckById', () => {
      it('Should return tracked Truck primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackTruckById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackPlanById', () => {
      it('Should return tracked Plan primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackPlanById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
