package it.marcobiasone.domain;

import static org.assertj.core.api.Assertions.assertThat;

import it.marcobiasone.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class IngredientQuantityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(IngredientQuantity.class);
        IngredientQuantity ingredientQuantity1 = new IngredientQuantity();
        ingredientQuantity1.setId(1L);
        IngredientQuantity ingredientQuantity2 = new IngredientQuantity();
        ingredientQuantity2.setId(ingredientQuantity1.getId());
        assertThat(ingredientQuantity1).isEqualTo(ingredientQuantity2);
        ingredientQuantity2.setId(2L);
        assertThat(ingredientQuantity1).isNotEqualTo(ingredientQuantity2);
        ingredientQuantity1.setId(null);
        assertThat(ingredientQuantity1).isNotEqualTo(ingredientQuantity2);
    }
}
