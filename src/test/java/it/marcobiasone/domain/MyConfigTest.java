package it.marcobiasone.domain;

import static org.assertj.core.api.Assertions.assertThat;

import it.marcobiasone.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MyConfigTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MyConfig.class);
        MyConfig myConfig1 = new MyConfig();
        myConfig1.setId(1L);
        MyConfig myConfig2 = new MyConfig();
        myConfig2.setId(myConfig1.getId());
        assertThat(myConfig1).isEqualTo(myConfig2);
        myConfig2.setId(2L);
        assertThat(myConfig1).isNotEqualTo(myConfig2);
        myConfig1.setId(null);
        assertThat(myConfig1).isNotEqualTo(myConfig2);
    }
}
