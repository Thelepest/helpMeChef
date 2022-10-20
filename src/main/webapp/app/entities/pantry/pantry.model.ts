import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { IIngredientQuantity } from 'app/entities/ingredient-quantity/ingredient-quantity.model';

export interface IPantry {
  id?: number;
  name?: string;
  active?: boolean;
  description?: string | null;
  createdAt?: dayjs.Dayjs;
  user?: IUser | null;
  ingredientquantities?: IIngredientQuantity[] | null;
}

export class Pantry implements IPantry {
  constructor(
    public id?: number,
    public name?: string,
    public active?: boolean,
    public description?: string | null,
    public createdAt?: dayjs.Dayjs,
    public user?: IUser | null,
    public ingredientquantities?: IIngredientQuantity[] | null
  ) {
    this.active = this.active ?? false;
  }
}

export function getPantryIdentifier(pantry: IPantry): number | undefined {
  return pantry.id;
}
