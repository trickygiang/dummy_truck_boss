import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IPlan, Plan } from '../plan.model';

import { PlanService } from './plan.service';

describe('Plan Service', () => {
  let service: PlanService;
  let httpMock: HttpTestingController;
  let elemDefault: IPlan;
  let expectedResult: IPlan | IPlan[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(PlanService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      startLat: 0,
      startLong: 0,
      endLat: 0,
      endLong: 0,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Plan', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Plan()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Plan', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          startLat: 1,
          startLong: 1,
          endLat: 1,
          endLong: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Plan', () => {
      const patchObject = Object.assign(
        {
          startLat: 1,
          startLong: 1,
          endLat: 1,
          endLong: 1,
        },
        new Plan()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Plan', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          startLat: 1,
          startLong: 1,
          endLat: 1,
          endLong: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Plan', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addPlanToCollectionIfMissing', () => {
      it('should add a Plan to an empty array', () => {
        const plan: IPlan = { id: 123 };
        expectedResult = service.addPlanToCollectionIfMissing([], plan);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(plan);
      });

      it('should not add a Plan to an array that contains it', () => {
        const plan: IPlan = { id: 123 };
        const planCollection: IPlan[] = [
          {
            ...plan,
          },
          { id: 456 },
        ];
        expectedResult = service.addPlanToCollectionIfMissing(planCollection, plan);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Plan to an array that doesn't contain it", () => {
        const plan: IPlan = { id: 123 };
        const planCollection: IPlan[] = [{ id: 456 }];
        expectedResult = service.addPlanToCollectionIfMissing(planCollection, plan);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(plan);
      });

      it('should add only unique Plan to an array', () => {
        const planArray: IPlan[] = [{ id: 123 }, { id: 456 }, { id: 39003 }];
        const planCollection: IPlan[] = [{ id: 123 }];
        expectedResult = service.addPlanToCollectionIfMissing(planCollection, ...planArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const plan: IPlan = { id: 123 };
        const plan2: IPlan = { id: 456 };
        expectedResult = service.addPlanToCollectionIfMissing([], plan, plan2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(plan);
        expect(expectedResult).toContain(plan2);
      });

      it('should accept null and undefined values', () => {
        const plan: IPlan = { id: 123 };
        expectedResult = service.addPlanToCollectionIfMissing([], null, plan, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(plan);
      });

      it('should return initial array if no Plan is added', () => {
        const planCollection: IPlan[] = [{ id: 123 }];
        expectedResult = service.addPlanToCollectionIfMissing(planCollection, undefined, null);
        expect(expectedResult).toEqual(planCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
