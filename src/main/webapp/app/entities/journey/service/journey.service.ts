import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IJourney, getJourneyIdentifier } from '../journey.model';

export type EntityResponseType = HttpResponse<IJourney>;
export type EntityArrayResponseType = HttpResponse<IJourney[]>;

@Injectable({ providedIn: 'root' })
export class JourneyService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/journeys');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(journey: IJourney): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(journey);
    return this.http
      .post<IJourney>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(journey: IJourney): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(journey);
    return this.http
      .put<IJourney>(`${this.resourceUrl}/${getJourneyIdentifier(journey) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(journey: IJourney): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(journey);
    return this.http
      .patch<IJourney>(`${this.resourceUrl}/${getJourneyIdentifier(journey) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IJourney>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IJourney[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addJourneyToCollectionIfMissing(journeyCollection: IJourney[], ...journeysToCheck: (IJourney | null | undefined)[]): IJourney[] {
    const journeys: IJourney[] = journeysToCheck.filter(isPresent);
    if (journeys.length > 0) {
      const journeyCollectionIdentifiers = journeyCollection.map(journeyItem => getJourneyIdentifier(journeyItem)!);
      const journeysToAdd = journeys.filter(journeyItem => {
        const journeyIdentifier = getJourneyIdentifier(journeyItem);
        if (journeyIdentifier == null || journeyCollectionIdentifiers.includes(journeyIdentifier)) {
          return false;
        }
        journeyCollectionIdentifiers.push(journeyIdentifier);
        return true;
      });
      return [...journeysToAdd, ...journeyCollection];
    }
    return journeyCollection;
  }

  protected convertDateFromClient(journey: IJourney): IJourney {
    return Object.assign({}, journey, {
      startTime: journey.startTime?.isValid() ? journey.startTime.toJSON() : undefined,
      endTime: journey.endTime?.isValid() ? journey.endTime.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.startTime = res.body.startTime ? dayjs(res.body.startTime) : undefined;
      res.body.endTime = res.body.endTime ? dayjs(res.body.endTime) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((journey: IJourney) => {
        journey.startTime = journey.startTime ? dayjs(journey.startTime) : undefined;
        journey.endTime = journey.endTime ? dayjs(journey.endTime) : undefined;
      });
    }
    return res;
  }
}
