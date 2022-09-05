import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITruck, getTruckIdentifier } from '../truck.model';

export type EntityResponseType = HttpResponse<ITruck>;
export type EntityArrayResponseType = HttpResponse<ITruck[]>;

@Injectable({ providedIn: 'root' })
export class TruckService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/trucks');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(truck: ITruck): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(truck);
    return this.http
      .post<ITruck>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(truck: ITruck): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(truck);
    return this.http
      .put<ITruck>(`${this.resourceUrl}/${getTruckIdentifier(truck) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(truck: ITruck): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(truck);
    return this.http
      .patch<ITruck>(`${this.resourceUrl}/${getTruckIdentifier(truck) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<ITruck>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ITruck[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addTruckToCollectionIfMissing(truckCollection: ITruck[], ...trucksToCheck: (ITruck | null | undefined)[]): ITruck[] {
    const trucks: ITruck[] = trucksToCheck.filter(isPresent);
    if (trucks.length > 0) {
      const truckCollectionIdentifiers = truckCollection.map(truckItem => getTruckIdentifier(truckItem)!);
      const trucksToAdd = trucks.filter(truckItem => {
        const truckIdentifier = getTruckIdentifier(truckItem);
        if (truckIdentifier == null || truckCollectionIdentifiers.includes(truckIdentifier)) {
          return false;
        }
        truckCollectionIdentifiers.push(truckIdentifier);
        return true;
      });
      return [...trucksToAdd, ...truckCollection];
    }
    return truckCollection;
  }

  protected convertDateFromClient(truck: ITruck): ITruck {
    return Object.assign({}, truck, {
      registrationDate: truck.registrationDate?.isValid() ? truck.registrationDate.format(DATE_FORMAT) : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.registrationDate = res.body.registrationDate ? dayjs(res.body.registrationDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((truck: ITruck) => {
        truck.registrationDate = truck.registrationDate ? dayjs(truck.registrationDate) : undefined;
      });
    }
    return res;
  }
}
