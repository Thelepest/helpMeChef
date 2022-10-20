package it.marcobiasone.domain;

import static org.assertj.core.api.Assertions.assertThat;

import it.marcobiasone.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RecipeCategoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RecipeCategory.class);
        RecipeCategory recipeCategory1 = new RecipeCategory();
        recipeCategory1.setId(1L);
        RecipeCategory recipeCategory2 = new RecipeCategory();
        recipeCategory2.setId(recipeCategory1.getId());
        assertThat(recipeCategory1).isEqualTo(recipeCategory2);
        recipeCategory2.setId(2L);
        assertThat(recipeCategory1).isNotEqualTo(recipeCategory2);
        recipeCategory1.setId(null);
        assertThat(recipeCategory1).isNotEqualTo(recipeCategory2);
    }
}
