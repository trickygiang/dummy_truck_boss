import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_FORMAT } from 'app/config/input.constants';
import { ITruck, Truck } from '../truck.model';

import { TruckService } from './truck.service';

describe('Truck Service', () => {
  let service: TruckService;
  let httpMock: HttpTestingController;
  let elemDefault: ITruck;
  let expectedResult: ITruck | ITruck[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TruckService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      code: 'AAAAAAA',
      model: 'AAAAAAA',
      registrationDate: currentDate,
      monthDue: 0,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          registrationDate: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Truck', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
          registrationDate: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          registrationDate: currentDate,
        },
        returnedFromService
      );

      service.create(new Truck()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Truck', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          code: 'BBBBBB',
          model: 'BBBBBB',
          registrationDate: currentDate.format(DATE_FORMAT),
          monthDue: 1,
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          registrationDate: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Truck', () => {
      const patchObject = Object.assign(
        {
          code: 'BBBBBB',
          registrationDate: currentDate.format(DATE_FORMAT),
          monthDue: 1,
        },
        new Truck()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          registrationDate: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Truck', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          code: 'BBBBBB',
          model: 'BBBBBB',
          registrationDate: currentDate.format(DATE_FORMAT),
          monthDue: 1,
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          registrationDate: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Truck', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addTruckToCollectionIfMissing', () => {
      it('should add a Truck to an empty array', () => {
        const truck: ITruck = { id: 123 };
        expectedResult = service.addTruckToCollectionIfMissing([], truck);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(truck);
      });

      it('should not add a Truck to an array that contains it', () => {
        const truck: ITruck = { id: 123 };
        const truckCollection: ITruck[] = [
          {
            ...truck,
          },
          { id: 456 },
        ];
        expectedResult = service.addTruckToCollectionIfMissing(truckCollection, truck);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Truck to an array that doesn't contain it", () => {
        const truck: ITruck = { id: 123 };
        const truckCollection: ITruck[] = [{ id: 456 }];
        expectedResult = service.addTruckToCollectionIfMissing(truckCollection, truck);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(truck);
      });

      it('should add only unique Truck to an array', () => {
        const truckArray: ITruck[] = [{ id: 123 }, { id: 456 }, { id: 30202 }];
        const truckCollection: ITruck[] = [{ id: 123 }];
        expectedResult = service.addTruckToCollectionIfMissing(truckCollection, ...truckArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const truck: ITruck = { id: 123 };
        const truck2: ITruck = { id: 456 };
        expectedResult = service.addTruckToCollectionIfMissing([], truck, truck2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(truck);
        expect(expectedResult).toContain(truck2);
      });

      it('should accept null and undefined values', () => {
        const truck: ITruck = { id: 123 };
        expectedResult = service.addTruckToCollectionIfMissing([], null, truck, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(truck);
      });

      it('should return initial array if no Truck is added', () => {
        const truckCollection: ITruck[] = [{ id: 123 }];
        expectedResult = service.addTruckToCollectionIfMissing(truckCollection, undefined, null);
        expect(expectedResult).toEqual(truckCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
