package it.marcobiasone.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Type;

/**
 * A Recipe.
 */
@Entity
@Table(name = "recipe")
public class Recipe implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "time")
    private Double time;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "description", nullable = false)
    private String description;

    @ManyToOne
    private RecipeCategory recipecategory;

    @ManyToMany
    @JoinTable(
        name = "rel_recipe__ingredientquantity",
        joinColumns = @JoinColumn(name = "recipe_id"),
        inverseJoinColumns = @JoinColumn(name = "ingredientquantity_id")
    )
    @JsonIgnoreProperties(value = { "ingredient", "quantity", "pantries", "recipes" }, allowSetters = true)
    private Set<IngredientQuantity> ingredientquantities = new HashSet<>();

    @ManyToMany(mappedBy = "recipes")
    @JsonIgnoreProperties(value = { "recipes" }, allowSetters = true)
    private Set<Tool> tools = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Recipe id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Recipe name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getTime() {
        return this.time;
    }

    public Recipe time(Double time) {
        this.setTime(time);
        return this;
    }

    public void setTime(Double time) {
        this.time = time;
    }

    public String getDescription() {
        return this.description;
    }

    public Recipe description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RecipeCategory getRecipecategory() {
        return this.recipecategory;
    }

    public void setRecipecategory(RecipeCategory recipeCategory) {
        this.recipecategory = recipeCategory;
    }

    public Recipe recipecategory(RecipeCategory recipeCategory) {
        this.setRecipecategory(recipeCategory);
        return this;
    }

    public Set<IngredientQuantity> getIngredientquantities() {
        return this.ingredientquantities;
    }

    public void setIngredientquantities(Set<IngredientQuantity> ingredientQuantities) {
        this.ingredientquantities = ingredientQuantities;
    }

    public Recipe ingredientquantities(Set<IngredientQuantity> ingredientQuantities) {
        this.setIngredientquantities(ingredientQuantities);
        return this;
    }

    public Recipe addIngredientquantity(IngredientQuantity ingredientQuantity) {
        this.ingredientquantities.add(ingredientQuantity);
        ingredientQuantity.getRecipes().add(this);
        return this;
    }

    public Recipe removeIngredientquantity(IngredientQuantity ingredientQuantity) {
        this.ingredientquantities.remove(ingredientQuantity);
        ingredientQuantity.getRecipes().remove(this);
        return this;
    }

    public Set<Tool> getTools() {
        return this.tools;
    }

    public void setTools(Set<Tool> tools) {
        if (this.tools != null) {
            this.tools.forEach(i -> i.removeRecipe(this));
        }
        if (tools != null) {
            tools.forEach(i -> i.addRecipe(this));
        }
        this.tools = tools;
    }

    public Recipe tools(Set<Tool> tools) {
        this.setTools(tools);
        return this;
    }

    public Recipe addTool(Tool tool) {
        this.tools.add(tool);
        tool.getRecipes().add(this);
        return this;
    }

    public Recipe removeTool(Tool tool) {
        this.tools.remove(tool);
        tool.getRecipes().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Recipe)) {
            return false;
        }
        return id != null && id.equals(((Recipe) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Recipe{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", time=" + getTime() +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
