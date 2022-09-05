import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IJourney, Journey } from '../journey.model';

import { JourneyService } from './journey.service';

describe('Journey Service', () => {
  let service: JourneyService;
  let httpMock: HttpTestingController;
  let elemDefault: IJourney;
  let expectedResult: IJourney | IJourney[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(JourneyService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      startTime: currentDate,
      endTime: currentDate,
      status: 0,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          startTime: currentDate.format(DATE_TIME_FORMAT),
          endTime: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Journey', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
          startTime: currentDate.format(DATE_TIME_FORMAT),
          endTime: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          startTime: currentDate,
          endTime: currentDate,
        },
        returnedFromService
      );

      service.create(new Journey()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Journey', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          startTime: currentDate.format(DATE_TIME_FORMAT),
          endTime: currentDate.format(DATE_TIME_FORMAT),
          status: 1,
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          startTime: currentDate,
          endTime: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Journey', () => {
      const patchObject = Object.assign(
        {
          startTime: currentDate.format(DATE_TIME_FORMAT),
          endTime: currentDate.format(DATE_TIME_FORMAT),
          status: 1,
        },
        new Journey()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          startTime: currentDate,
          endTime: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Journey', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          startTime: currentDate.format(DATE_TIME_FORMAT),
          endTime: currentDate.format(DATE_TIME_FORMAT),
          status: 1,
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          startTime: currentDate,
          endTime: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Journey', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addJourneyToCollectionIfMissing', () => {
      it('should add a Journey to an empty array', () => {
        const journey: IJourney = { id: 123 };
        expectedResult = service.addJourneyToCollectionIfMissing([], journey);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(journey);
      });

      it('should not add a Journey to an array that contains it', () => {
        const journey: IJourney = { id: 123 };
        const journeyCollection: IJourney[] = [
          {
            ...journey,
          },
          { id: 456 },
        ];
        expectedResult = service.addJourneyToCollectionIfMissing(journeyCollection, journey);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Journey to an array that doesn't contain it", () => {
        const journey: IJourney = { id: 123 };
        const journeyCollection: IJourney[] = [{ id: 456 }];
        expectedResult = service.addJourneyToCollectionIfMissing(journeyCollection, journey);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(journey);
      });

      it('should add only unique Journey to an array', () => {
        const journeyArray: IJourney[] = [{ id: 123 }, { id: 456 }, { id: 81844 }];
        const journeyCollection: IJourney[] = [{ id: 123 }];
        expectedResult = service.addJourneyToCollectionIfMissing(journeyCollection, ...journeyArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const journey: IJourney = { id: 123 };
        const journey2: IJourney = { id: 456 };
        expectedResult = service.addJourneyToCollectionIfMissing([], journey, journey2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(journey);
        expect(expectedResult).toContain(journey2);
      });

      it('should accept null and undefined values', () => {
        const journey: IJourney = { id: 123 };
        expectedResult = service.addJourneyToCollectionIfMissing([], null, journey, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(journey);
      });

      it('should return initial array if no Journey is added', () => {
        const journeyCollection: IJourney[] = [{ id: 123 }];
        expectedResult = service.addJourneyToCollectionIfMissing(journeyCollection, undefined, null);
        expect(expectedResult).toEqual(journeyCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
