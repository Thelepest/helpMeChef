import { IIngredientCategory } from 'app/entities/ingredient-category/ingredient-category.model';
import { IRecipeCategory } from 'app/entities/recipe-category/recipe-category.model';

export interface IUncompatibleIRCategory {
  id?: number;
  ingredientcategory?: IIngredientCategory | null;
  recipecategory?: IRecipeCategory | null;
}

export class UncompatibleIRCategory implements IUncompatibleIRCategory {
  constructor(public id?: number, public ingredientcategory?: IIngredientCategory | null, public recipecategory?: IRecipeCategory | null) {}
}

export function getUncompatibleIRCategoryIdentifier(uncompatibleIRCategory: IUncompatibleIRCategory): number | undefined {
  return uncompatibleIRCategory.id;
}
