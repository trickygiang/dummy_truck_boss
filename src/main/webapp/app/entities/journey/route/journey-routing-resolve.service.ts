import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IJourney, Journey } from '../journey.model';
import { JourneyService } from '../service/journey.service';

@Injectable({ providedIn: 'root' })
export class JourneyRoutingResolveService implements Resolve<IJourney> {
  constructor(protected service: JourneyService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IJourney> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((journey: HttpResponse<Journey>) => {
          if (journey.body) {
            return of(journey.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Journey());
  }
}
