import dayjs from 'dayjs/esm';

export interface ITruck {
  id?: number;
  code?: string | null;
  model?: string | null;
  registrationDate?: dayjs.Dayjs | null;
  monthDue?: number | null;
}

export class Truck implements ITruck {
  constructor(
    public id?: number,
    public code?: string | null,
    public model?: string | null,
    public registrationDate?: dayjs.Dayjs | null,
    public monthDue?: number | null
  ) {}
}

export function getTruckIdentifier(truck: ITruck): number | undefined {
  return truck.id;
}
