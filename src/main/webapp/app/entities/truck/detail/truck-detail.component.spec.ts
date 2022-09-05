import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { TruckDetailComponent } from './truck-detail.component';

describe('Truck Management Detail Component', () => {
  let comp: TruckDetailComponent;
  let fixture: ComponentFixture<TruckDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TruckDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ truck: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(TruckDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(TruckDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load truck on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.truck).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
