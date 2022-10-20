export interface IIngredientCategory {
  id?: number;
  name?: string;
  imageContentType?: string;
  image?: string;
  description?: string | null;
}

export class IngredientCategory implements IIngredientCategory {
  constructor(
    public id?: number,
    public name?: string,
    public imageContentType?: string,
    public image?: string,
    public description?: string | null
  ) {}
}

export function getIngredientCategoryIdentifier(ingredientCategory: IIngredientCategory): number | undefined {
  return ingredientCategory.id;
}
