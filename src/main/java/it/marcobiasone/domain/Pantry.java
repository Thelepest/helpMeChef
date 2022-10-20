package it.marcobiasone.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Pantry.
 */
@Entity
@Table(name = "pantry")
public class Pantry implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active;

    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    @ManyToOne
    private User user;

    @ManyToMany
    @JoinTable(
        name = "rel_pantry__ingredientquantity",
        joinColumns = @JoinColumn(name = "pantry_id"),
        inverseJoinColumns = @JoinColumn(name = "ingredientquantity_id")
    )
    @JsonIgnoreProperties(value = { "ingredient", "quantity", "pantries", "recipes" }, allowSetters = true)
    private Set<IngredientQuantity> ingredientquantities = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Pantry id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Pantry name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getActive() {
        return this.active;
    }

    public Pantry active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getDescription() {
        return this.description;
    }

    public Pantry description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }

    public Pantry createdAt(ZonedDateTime createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Pantry user(User user) {
        this.setUser(user);
        return this;
    }

    public Set<IngredientQuantity> getIngredientquantities() {
        return this.ingredientquantities;
    }

    public void setIngredientquantities(Set<IngredientQuantity> ingredientQuantities) {
        this.ingredientquantities = ingredientQuantities;
    }

    public Pantry ingredientquantities(Set<IngredientQuantity> ingredientQuantities) {
        this.setIngredientquantities(ingredientQuantities);
        return this;
    }

    public Pantry addIngredientquantity(IngredientQuantity ingredientQuantity) {
        this.ingredientquantities.add(ingredientQuantity);
        ingredientQuantity.getPantries().add(this);
        return this;
    }

    public Pantry removeIngredientquantity(IngredientQuantity ingredientQuantity) {
        this.ingredientquantities.remove(ingredientQuantity);
        ingredientQuantity.getPantries().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Pantry)) {
            return false;
        }
        return id != null && id.equals(((Pantry) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Pantry{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", active='" + getActive() + "'" +
            ", description='" + getDescription() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
