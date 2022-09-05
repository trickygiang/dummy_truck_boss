import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPlan, Plan } from '../plan.model';
import { PlanService } from '../service/plan.service';

@Injectable({ providedIn: 'root' })
export class PlanRoutingResolveService implements Resolve<IPlan> {
  constructor(protected service: PlanService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IPlan> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((plan: HttpResponse<Plan>) => {
          if (plan.body) {
            return of(plan.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Plan());
  }
}
