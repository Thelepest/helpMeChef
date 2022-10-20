package it.marcobiasone.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A MyConfig.
 */
@Entity
@Table(name = "my_config")
public class MyConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "mc_key", nullable = false)
    private String mcKey;

    @NotNull
    @Column(name = "mc_value", nullable = false)
    private String mcValue;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public MyConfig id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMcKey() {
        return this.mcKey;
    }

    public MyConfig mcKey(String mcKey) {
        this.setMcKey(mcKey);
        return this;
    }

    public void setMcKey(String mcKey) {
        this.mcKey = mcKey;
    }

    public String getMcValue() {
        return this.mcValue;
    }

    public MyConfig mcValue(String mcValue) {
        this.setMcValue(mcValue);
        return this;
    }

    public void setMcValue(String mcValue) {
        this.mcValue = mcValue;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MyConfig)) {
            return false;
        }
        return id != null && id.equals(((MyConfig) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MyConfig{" +
            "id=" + getId() +
            ", mcKey='" + getMcKey() + "'" +
            ", mcValue='" + getMcValue() + "'" +
            "}";
    }
}
