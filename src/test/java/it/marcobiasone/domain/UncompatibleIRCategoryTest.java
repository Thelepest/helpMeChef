package it.marcobiasone.domain;

import static org.assertj.core.api.Assertions.assertThat;

import it.marcobiasone.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UncompatibleIRCategoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UncompatibleIRCategory.class);
        UncompatibleIRCategory uncompatibleIRCategory1 = new UncompatibleIRCategory();
        uncompatibleIRCategory1.setId(1L);
        UncompatibleIRCategory uncompatibleIRCategory2 = new UncompatibleIRCategory();
        uncompatibleIRCategory2.setId(uncompatibleIRCategory1.getId());
        assertThat(uncompatibleIRCategory1).isEqualTo(uncompatibleIRCategory2);
        uncompatibleIRCategory2.setId(2L);
        assertThat(uncompatibleIRCategory1).isNotEqualTo(uncompatibleIRCategory2);
        uncompatibleIRCategory1.setId(null);
        assertThat(uncompatibleIRCategory1).isNotEqualTo(uncompatibleIRCategory2);
    }
}
