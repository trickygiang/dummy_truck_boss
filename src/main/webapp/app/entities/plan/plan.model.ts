export interface IPlan {
  id?: number;
  startLat?: number | null;
  startLong?: number | null;
  endLat?: number | null;
  endLong?: number | null;
}

export class Plan implements IPlan {
  constructor(
    public id?: number,
    public startLat?: number | null,
    public startLong?: number | null,
    public endLat?: number | null,
    public endLong?: number | null
  ) {}
}

export function getPlanIdentifier(plan: IPlan): number | undefined {
  return plan.id;
}
