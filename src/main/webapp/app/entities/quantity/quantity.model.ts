export interface IQuantity {
  id?: number;
  name?: string;
  amount?: number;
  description?: string | null;
}

export class Quantity implements IQuantity {
  constructor(public id?: number, public name?: string, public amount?: number, public description?: string | null) {}
}

export function getQuantityIdentifier(quantity: IQuantity): number | undefined {
  return quantity.id;
}
