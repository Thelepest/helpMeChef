package it.marcobiasone.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A IngredientQuantity.
 */
@Entity
@Table(name = "ingredient_quantity")
public class IngredientQuantity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JsonIgnoreProperties(value = { "ingredientcategory", "parent" }, allowSetters = true)
    private Ingredient ingredient;

    @ManyToOne
    private Quantity quantity;

    @ManyToMany(mappedBy = "ingredientquantities")
    @JsonIgnoreProperties(value = { "user", "ingredientquantities" }, allowSetters = true)
    private Set<Pantry> pantries = new HashSet<>();

    @ManyToMany(mappedBy = "ingredientquantities")
    @JsonIgnoreProperties(value = { "recipecategory", "ingredientquantities", "tools" }, allowSetters = true)
    private Set<Recipe> recipes = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public IngredientQuantity id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Ingredient getIngredient() {
        return this.ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public IngredientQuantity ingredient(Ingredient ingredient) {
        this.setIngredient(ingredient);
        return this;
    }

    public Quantity getQuantity() {
        return this.quantity;
    }

    public void setQuantity(Quantity quantity) {
        this.quantity = quantity;
    }

    public IngredientQuantity quantity(Quantity quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public Set<Pantry> getPantries() {
        return this.pantries;
    }

    public void setPantries(Set<Pantry> pantries) {
        if (this.pantries != null) {
            this.pantries.forEach(i -> i.removeIngredientquantity(this));
        }
        if (pantries != null) {
            pantries.forEach(i -> i.addIngredientquantity(this));
        }
        this.pantries = pantries;
    }

    public IngredientQuantity pantries(Set<Pantry> pantries) {
        this.setPantries(pantries);
        return this;
    }

    public IngredientQuantity addPantry(Pantry pantry) {
        this.pantries.add(pantry);
        pantry.getIngredientquantities().add(this);
        return this;
    }

    public IngredientQuantity removePantry(Pantry pantry) {
        this.pantries.remove(pantry);
        pantry.getIngredientquantities().remove(this);
        return this;
    }

    public Set<Recipe> getRecipes() {
        return this.recipes;
    }

    public void setRecipes(Set<Recipe> recipes) {
        if (this.recipes != null) {
            this.recipes.forEach(i -> i.removeIngredientquantity(this));
        }
        if (recipes != null) {
            recipes.forEach(i -> i.addIngredientquantity(this));
        }
        this.recipes = recipes;
    }

    public IngredientQuantity recipes(Set<Recipe> recipes) {
        this.setRecipes(recipes);
        return this;
    }

    public IngredientQuantity addRecipe(Recipe recipe) {
        this.recipes.add(recipe);
        recipe.getIngredientquantities().add(this);
        return this;
    }

    public IngredientQuantity removeRecipe(Recipe recipe) {
        this.recipes.remove(recipe);
        recipe.getIngredientquantities().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IngredientQuantity)) {
            return false;
        }
        return id != null && id.equals(((IngredientQuantity) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "IngredientQuantity{" +
            "id=" + getId() +
            "}";
    }
}
