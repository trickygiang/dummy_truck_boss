import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { PlanDetailComponent } from './plan-detail.component';

describe('Plan Management Detail Component', () => {
  let comp: PlanDetailComponent;
  let fixture: ComponentFixture<PlanDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PlanDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ plan: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(PlanDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(PlanDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load plan on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.plan).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
