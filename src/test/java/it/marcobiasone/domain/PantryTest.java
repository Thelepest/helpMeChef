package it.marcobiasone.domain;

import static org.assertj.core.api.Assertions.assertThat;

import it.marcobiasone.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PantryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Pantry.class);
        Pantry pantry1 = new Pantry();
        pantry1.setId(1L);
        Pantry pantry2 = new Pantry();
        pantry2.setId(pantry1.getId());
        assertThat(pantry1).isEqualTo(pantry2);
        pantry2.setId(2L);
        assertThat(pantry1).isNotEqualTo(pantry2);
        pantry1.setId(null);
        assertThat(pantry1).isNotEqualTo(pantry2);
    }
}
