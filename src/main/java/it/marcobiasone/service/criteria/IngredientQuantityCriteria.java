package it.marcobiasone.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link it.marcobiasone.domain.IngredientQuantity} entity. This class is used
 * in {@link it.marcobiasone.web.rest.IngredientQuantityResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /ingredient-quantities?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class IngredientQuantityCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter ingredientId;

    private LongFilter quantityId;

    private LongFilter pantryId;

    private LongFilter recipeId;

    private Boolean distinct;

    public IngredientQuantityCriteria() {}

    public IngredientQuantityCriteria(IngredientQuantityCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.ingredientId = other.ingredientId == null ? null : other.ingredientId.copy();
        this.quantityId = other.quantityId == null ? null : other.quantityId.copy();
        this.pantryId = other.pantryId == null ? null : other.pantryId.copy();
        this.recipeId = other.recipeId == null ? null : other.recipeId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public IngredientQuantityCriteria copy() {
        return new IngredientQuantityCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public LongFilter getIngredientId() {
        return ingredientId;
    }

    public LongFilter ingredientId() {
        if (ingredientId == null) {
            ingredientId = new LongFilter();
        }
        return ingredientId;
    }

    public void setIngredientId(LongFilter ingredientId) {
        this.ingredientId = ingredientId;
    }

    public LongFilter getQuantityId() {
        return quantityId;
    }

    public LongFilter quantityId() {
        if (quantityId == null) {
            quantityId = new LongFilter();
        }
        return quantityId;
    }

    public void setQuantityId(LongFilter quantityId) {
        this.quantityId = quantityId;
    }

    public LongFilter getPantryId() {
        return pantryId;
    }

    public LongFilter pantryId() {
        if (pantryId == null) {
            pantryId = new LongFilter();
        }
        return pantryId;
    }

    public void setPantryId(LongFilter pantryId) {
        this.pantryId = pantryId;
    }

    public LongFilter getRecipeId() {
        return recipeId;
    }

    public LongFilter recipeId() {
        if (recipeId == null) {
            recipeId = new LongFilter();
        }
        return recipeId;
    }

    public void setRecipeId(LongFilter recipeId) {
        this.recipeId = recipeId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final IngredientQuantityCriteria that = (IngredientQuantityCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(ingredientId, that.ingredientId) &&
            Objects.equals(quantityId, that.quantityId) &&
            Objects.equals(pantryId, that.pantryId) &&
            Objects.equals(recipeId, that.recipeId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ingredientId, quantityId, pantryId, recipeId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "IngredientQuantityCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (ingredientId != null ? "ingredientId=" + ingredientId + ", " : "") +
            (quantityId != null ? "quantityId=" + quantityId + ", " : "") +
            (pantryId != null ? "pantryId=" + pantryId + ", " : "") +
            (recipeId != null ? "recipeId=" + recipeId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
