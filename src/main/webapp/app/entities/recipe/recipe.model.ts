import { IRecipeCategory } from 'app/entities/recipe-category/recipe-category.model';
import { IIngredientQuantity } from 'app/entities/ingredient-quantity/ingredient-quantity.model';
import { ITool } from 'app/entities/tool/tool.model';

export interface IRecipe {
  id?: number;
  name?: string;
  time?: number | null;
  description?: string;
  recipecategory?: IRecipeCategory | null;
  ingredientquantities?: IIngredientQuantity[] | null;
  tools?: ITool[] | null;
}

export class Recipe implements IRecipe {
  constructor(
    public id?: number,
    public name?: string,
    public time?: number | null,
    public description?: string,
    public recipecategory?: IRecipeCategory | null,
    public ingredientquantities?: IIngredientQuantity[] | null,
    public tools?: ITool[] | null
  ) {}
}

export function getRecipeIdentifier(recipe: IRecipe): number | undefined {
  return recipe.id;
}
