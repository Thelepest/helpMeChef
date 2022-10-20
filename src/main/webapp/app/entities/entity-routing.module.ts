import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'ingredient',
        data: { pageTitle: 'helpMeChefApp.ingredient.home.title' },
        loadChildren: () => import('./ingredient/ingredient.module').then(m => m.IngredientModule),
      },
      {
        path: 'quantity',
        data: { pageTitle: 'helpMeChefApp.quantity.home.title' },
        loadChildren: () => import('./quantity/quantity.module').then(m => m.QuantityModule),
      },
      {
        path: 'ingredient-quantity',
        data: { pageTitle: 'helpMeChefApp.ingredientQuantity.home.title' },
        loadChildren: () => import('./ingredient-quantity/ingredient-quantity.module').then(m => m.IngredientQuantityModule),
      },
      {
        path: 'recipe',
        data: { pageTitle: 'helpMeChefApp.recipe.home.title' },
        loadChildren: () => import('./recipe/recipe.module').then(m => m.RecipeModule),
      },
      {
        path: 'pantry',
        data: { pageTitle: 'helpMeChefApp.pantry.home.title' },
        loadChildren: () => import('./pantry/pantry.module').then(m => m.PantryModule),
      },
      {
        path: 'ingredient-category',
        data: { pageTitle: 'helpMeChefApp.ingredientCategory.home.title' },
        loadChildren: () => import('./ingredient-category/ingredient-category.module').then(m => m.IngredientCategoryModule),
      },
      {
        path: 'recipe-category',
        data: { pageTitle: 'helpMeChefApp.recipeCategory.home.title' },
        loadChildren: () => import('./recipe-category/recipe-category.module').then(m => m.RecipeCategoryModule),
      },
      {
        path: 'uncompatible-ir-category',
        data: { pageTitle: 'helpMeChefApp.uncompatibleIRCategory.home.title' },
        loadChildren: () => import('./uncompatible-ir-category/uncompatible-ir-category.module').then(m => m.UncompatibleIRCategoryModule),
      },
      {
        path: 'tool',
        data: { pageTitle: 'helpMeChefApp.tool.home.title' },
        loadChildren: () => import('./tool/tool.module').then(m => m.ToolModule),
      },
      {
        path: 'my-config',
        data: { pageTitle: 'helpMeChefApp.myConfig.home.title' },
        loadChildren: () => import('./my-config/my-config.module').then(m => m.MyConfigModule),
      },
      {
        path: 'comment',
        data: { pageTitle: 'helpMeChefApp.comment.home.title' },
        loadChildren: () => import('./comment/comment.module').then(m => m.CommentModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
