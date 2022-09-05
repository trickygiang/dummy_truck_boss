import dayjs from 'dayjs/esm';
import { ISchedule } from 'app/entities/schedule/schedule.model';

export interface IJourney {
  id?: number;
  startTime?: dayjs.Dayjs | null;
  endTime?: dayjs.Dayjs | null;
  status?: number | null;
  schedule?: ISchedule | null;
}

export class Journey implements IJourney {
  constructor(
    public id?: number,
    public startTime?: dayjs.Dayjs | null,
    public endTime?: dayjs.Dayjs | null,
    public status?: number | null,
    public schedule?: ISchedule | null
  ) {}
}

export function getJourneyIdentifier(journey: IJourney): number | undefined {
  return journey.id;
}
