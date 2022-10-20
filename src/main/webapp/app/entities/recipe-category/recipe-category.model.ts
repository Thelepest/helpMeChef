export interface IRecipeCategory {
  id?: number;
  name?: string;
  imageContentType?: string;
  image?: string;
  description?: string | null;
}

export class RecipeCategory implements IRecipeCategory {
  constructor(
    public id?: number,
    public name?: string,
    public imageContentType?: string,
    public image?: string,
    public description?: string | null
  ) {}
}

export function getRecipeCategoryIdentifier(recipeCategory: IRecipeCategory): number | undefined {
  return recipeCategory.id;
}
