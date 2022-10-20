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
 * Criteria class for the {@link it.marcobiasone.domain.UncompatibleIRCategory} entity. This class is used
 * in {@link it.marcobiasone.web.rest.UncompatibleIRCategoryResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /uncompatible-ir-categories?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class UncompatibleIRCategoryCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter ingredientcategoryId;

    private LongFilter recipecategoryId;

    private Boolean distinct;

    public UncompatibleIRCategoryCriteria() {}

    public UncompatibleIRCategoryCriteria(UncompatibleIRCategoryCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.ingredientcategoryId = other.ingredientcategoryId == null ? null : other.ingredientcategoryId.copy();
        this.recipecategoryId = other.recipecategoryId == null ? null : other.recipecategoryId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public UncompatibleIRCategoryCriteria copy() {
        return new UncompatibleIRCategoryCriteria(this);
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

    public LongFilter getIngredientcategoryId() {
        return ingredientcategoryId;
    }

    public LongFilter ingredientcategoryId() {
        if (ingredientcategoryId == null) {
            ingredientcategoryId = new LongFilter();
        }
        return ingredientcategoryId;
    }

    public void setIngredientcategoryId(LongFilter ingredientcategoryId) {
        this.ingredientcategoryId = ingredientcategoryId;
    }

    public LongFilter getRecipecategoryId() {
        return recipecategoryId;
    }

    public LongFilter recipecategoryId() {
        if (recipecategoryId == null) {
            recipecategoryId = new LongFilter();
        }
        return recipecategoryId;
    }

    public void setRecipecategoryId(LongFilter recipecategoryId) {
        this.recipecategoryId = recipecategoryId;
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
        final UncompatibleIRCategoryCriteria that = (UncompatibleIRCategoryCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(ingredientcategoryId, that.ingredientcategoryId) &&
            Objects.equals(recipecategoryId, that.recipecategoryId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ingredientcategoryId, recipecategoryId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UncompatibleIRCategoryCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (ingredientcategoryId != null ? "ingredientcategoryId=" + ingredientcategoryId + ", " : "") +
            (recipecategoryId != null ? "recipecategoryId=" + recipecategoryId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
