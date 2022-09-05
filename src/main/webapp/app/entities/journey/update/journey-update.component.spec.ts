import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { JourneyService } from '../service/journey.service';
import { IJourney, Journey } from '../journey.model';
import { ISchedule } from 'app/entities/schedule/schedule.model';
import { ScheduleService } from 'app/entities/schedule/service/schedule.service';

import { JourneyUpdateComponent } from './journey-update.component';

describe('Journey Management Update Component', () => {
  let comp: JourneyUpdateComponent;
  let fixture: ComponentFixture<JourneyUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let journeyService: JourneyService;
  let scheduleService: ScheduleService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [JourneyUpdateComponent],
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
      .overrideTemplate(JourneyUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(JourneyUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    journeyService = TestBed.inject(JourneyService);
    scheduleService = TestBed.inject(ScheduleService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Schedule query and add missing value', () => {
      const journey: IJourney = { id: 456 };
      const schedule: ISchedule = { id: 30982 };
      journey.schedule = schedule;

      const scheduleCollection: ISchedule[] = [{ id: 23430 }];
      jest.spyOn(scheduleService, 'query').mockReturnValue(of(new HttpResponse({ body: scheduleCollection })));
      const additionalSchedules = [schedule];
      const expectedCollection: ISchedule[] = [...additionalSchedules, ...scheduleCollection];
      jest.spyOn(scheduleService, 'addScheduleToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ journey });
      comp.ngOnInit();

      expect(scheduleService.query).toHaveBeenCalled();
      expect(scheduleService.addScheduleToCollectionIfMissing).toHaveBeenCalledWith(scheduleCollection, ...additionalSchedules);
      expect(comp.schedulesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const journey: IJourney = { id: 456 };
      const schedule: ISchedule = { id: 85885 };
      journey.schedule = schedule;

      activatedRoute.data = of({ journey });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(journey));
      expect(comp.schedulesSharedCollection).toContain(schedule);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Journey>>();
      const journey = { id: 123 };
      jest.spyOn(journeyService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ journey });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: journey }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(journeyService.update).toHaveBeenCalledWith(journey);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Journey>>();
      const journey = new Journey();
      jest.spyOn(journeyService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ journey });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: journey }));
      saveSubject.complete();

      // THEN
      expect(journeyService.create).toHaveBeenCalledWith(journey);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Journey>>();
      const journey = { id: 123 };
      jest.spyOn(journeyService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ journey });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(journeyService.update).toHaveBeenCalledWith(journey);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackScheduleById', () => {
      it('Should return tracked Schedule primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackScheduleById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
