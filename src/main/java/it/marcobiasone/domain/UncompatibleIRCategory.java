package it.marcobiasone.domain;

import java.io.Serializable;
import javax.persistence.*;

/**
 * A UncompatibleIRCategory.
 */
@Entity
@Table(name = "uncompatible_ir_category")
public class UncompatibleIRCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @ManyToOne
    private IngredientCategory ingredientcategory;

    @ManyToOne
    private RecipeCategory recipecategory;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public UncompatibleIRCategory id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public IngredientCategory getIngredientcategory() {
        return this.ingredientcategory;
    }

    public void setIngredientcategory(IngredientCategory ingredientCategory) {
        this.ingredientcategory = ingredientCategory;
    }

    public UncompatibleIRCategory ingredientcategory(IngredientCategory ingredientCategory) {
        this.setIngredientcategory(ingredientCategory);
        return this;
    }

    public RecipeCategory getRecipecategory() {
        return this.recipecategory;
    }

    public void setRecipecategory(RecipeCategory recipeCategory) {
        this.recipecategory = recipeCategory;
    }

    public UncompatibleIRCategory recipecategory(RecipeCategory recipeCategory) {
        this.setRecipecategory(recipeCategory);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UncompatibleIRCategory)) {
            return false;
        }
        return id != null && id.equals(((UncompatibleIRCategory) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UncompatibleIRCategory{" +
            "id=" + getId() +
            "}";
    }
}
