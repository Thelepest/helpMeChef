package it.marcobiasone.domain;

import static org.assertj.core.api.Assertions.assertThat;

import it.marcobiasone.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class IngredientCategoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(IngredientCategory.class);
        IngredientCategory ingredientCategory1 = new IngredientCategory();
        ingredientCategory1.setId(1L);
        IngredientCategory ingredientCategory2 = new IngredientCategory();
        ingredientCategory2.setId(ingredientCategory1.getId());
        assertThat(ingredientCategory1).isEqualTo(ingredientCategory2);
        ingredientCategory2.setId(2L);
        assertThat(ingredientCategory1).isNotEqualTo(ingredientCategory2);
        ingredientCategory1.setId(null);
        assertThat(ingredientCategory1).isNotEqualTo(ingredientCategory2);
    }
}
