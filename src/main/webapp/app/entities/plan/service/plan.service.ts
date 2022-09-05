import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPlan, getPlanIdentifier } from '../plan.model';

export type EntityResponseType = HttpResponse<IPlan>;
export type EntityArrayResponseType = HttpResponse<IPlan[]>;

@Injectable({ providedIn: 'root' })
export class PlanService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/plans');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(plan: IPlan): Observable<EntityResponseType> {
    return this.http.post<IPlan>(this.resourceUrl, plan, { observe: 'response' });
  }

  update(plan: IPlan): Observable<EntityResponseType> {
    return this.http.put<IPlan>(`${this.resourceUrl}/${getPlanIdentifier(plan) as number}`, plan, { observe: 'response' });
  }

  partialUpdate(plan: IPlan): Observable<EntityResponseType> {
    return this.http.patch<IPlan>(`${this.resourceUrl}/${getPlanIdentifier(plan) as number}`, plan, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IPlan>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPlan[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addPlanToCollectionIfMissing(planCollection: IPlan[], ...plansToCheck: (IPlan | null | undefined)[]): IPlan[] {
    const plans: IPlan[] = plansToCheck.filter(isPresent);
    if (plans.length > 0) {
      const planCollectionIdentifiers = planCollection.map(planItem => getPlanIdentifier(planItem)!);
      const plansToAdd = plans.filter(planItem => {
        const planIdentifier = getPlanIdentifier(planItem);
        if (planIdentifier == null || planCollectionIdentifiers.includes(planIdentifier)) {
          return false;
        }
        planCollectionIdentifiers.push(planIdentifier);
        return true;
      });
      return [...plansToAdd, ...planCollection];
    }
    return planCollection;
  }
}
