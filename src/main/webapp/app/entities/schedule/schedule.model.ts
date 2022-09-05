import { ITruck } from 'app/entities/truck/truck.model';
import { IPlan } from 'app/entities/plan/plan.model';

export interface ISchedule {
  id?: number;
  status?: number | null;
  truck?: ITruck | null;
  plan?: IPlan | null;
}

export class Schedule implements ISchedule {
  constructor(public id?: number, public status?: number | null, public truck?: ITruck | null, public plan?: IPlan | null) {}
}

export function getScheduleIdentifier(schedule: ISchedule): number | undefined {
  return schedule.id;
}
