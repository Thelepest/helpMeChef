import { IRecipe } from 'app/entities/recipe/recipe.model';

export interface ITool {
  id?: number;
  name?: string;
  description?: string | null;
  recipes?: IRecipe[] | null;
}

export class Tool implements ITool {
  constructor(public id?: number, public name?: string, public description?: string | null, public recipes?: IRecipe[] | null) {}
}

export function getToolIdentifier(tool: ITool): number | undefined {
  return tool.id;
}
