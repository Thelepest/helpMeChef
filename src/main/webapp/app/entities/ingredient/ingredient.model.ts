import { IIngredientCategory } from 'app/entities/ingredient-category/ingredient-category.model';

export interface IIngredient {
  id?: number;
  name?: string;
  imageContentType?: string | null;
  image?: string | null;
  ingredientcategory?: IIngredientCategory | null;
  parent?: IIngredient | null;
}

export class Ingredient implements IIngredient {
  constructor(
    public id?: number,
    public name?: string,
    public imageContentType?: string | null,
    public image?: string | null,
    public ingredientcategory?: IIngredientCategory | null,
    public parent?: IIngredient | null
  ) {}
}

export function getIngredientIdentifier(ingredient: IIngredient): number | undefined {
  return ingredient.id;
}
