import { IUser } from 'app/entities/user/user.model';
import { IRecipe } from 'app/entities/recipe/recipe.model';

export interface IComment {
  id?: number;
  title?: string;
  body?: string;
  user?: IUser | null;
  recipe?: IRecipe | null;
}

export class Comment implements IComment {
  constructor(
    public id?: number,
    public title?: string,
    public body?: string,
    public user?: IUser | null,
    public recipe?: IRecipe | null
  ) {}
}

export function getCommentIdentifier(comment: IComment): number | undefined {
  return comment.id;
}
