import { IIngredient } from 'app/entities/ingredient/ingredient.model';
import { IQuantity } from 'app/entities/quantity/quantity.model';
import { IPantry } from 'app/entities/pantry/pantry.model';
import { IRecipe } from 'app/entities/recipe/recipe.model';

export interface IIngredientQuantity {
  id?: number;
  ingredient?: IIngredient | null;
  quantity?: IQuantity | null;
  pantries?: IPantry[] | null;
  recipes?: IRecipe[] | null;
}

export class IngredientQuantity implements IIngredientQuantity {
  constructor(
    public id?: number,
    public ingredient?: IIngredient | null,
    public quantity?: IQuantity | null,
    public pantries?: IPantry[] | null,
    public recipes?: IRecipe[] | null
  ) {}
}

export function getIngredientQuantityIdentifier(ingredientQuantity: IIngredientQuantity): number | undefined {
  return ingredientQuantity.id;
}
