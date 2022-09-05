import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TruckService } from '../service/truck.service';
import { ITruck, Truck } from '../truck.model';

import { TruckUpdateComponent } from './truck-update.component';

describe('Truck Management Update Component', () => {
  let comp: TruckUpdateComponent;
  let fixture: ComponentFixture<TruckUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let truckService: TruckService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [TruckUpdateComponent],
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
      .overrideTemplate(TruckUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TruckUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    truckService = TestBed.inject(TruckService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const truck: ITruck = { id: 456 };

      activatedRoute.data = of({ truck });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(truck));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Truck>>();
      const truck = { id: 123 };
      jest.spyOn(truckService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ truck });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: truck }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(truckService.update).toHaveBeenCalledWith(truck);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Truck>>();
      const truck = new Truck();
      jest.spyOn(truckService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ truck });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: truck }));
      saveSubject.complete();

      // THEN
      expect(truckService.create).toHaveBeenCalledWith(truck);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Truck>>();
      const truck = { id: 123 };
      jest.spyOn(truckService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ truck });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(truckService.update).toHaveBeenCalledWith(truck);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
