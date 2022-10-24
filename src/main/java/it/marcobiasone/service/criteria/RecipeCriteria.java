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
 * Criteria class for the {@link it.marcobiasone.domain.Recipe} entity. This class is used
 * in {@link it.marcobiasone.web.rest.RecipeResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /recipes?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class RecipeCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private DoubleFilter time;

    private IntegerFilter diners;

    private LongFilter recipecategoryId;

    private LongFilter ingredientquantityId;

    private LongFilter toolId;

    private Boolean distinct;

    public RecipeCriteria() {}

    public RecipeCriteria(RecipeCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.time = other.time == null ? null : other.time.copy();
        this.diners = other.diners == null ? null : other.diners.copy();
        this.recipecategoryId = other.recipecategoryId == null ? null : other.recipecategoryId.copy();
        this.ingredientquantityId = other.ingredientquantityId == null ? null : other.ingredientquantityId.copy();
        this.toolId = other.toolId == null ? null : other.toolId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public RecipeCriteria copy() {
        return new RecipeCriteria(this);
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

    public StringFilter getName() {
        return name;
    }

    public StringFilter name() {
        if (name == null) {
            name = new StringFilter();
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public DoubleFilter getTime() {
        return time;
    }

    public DoubleFilter time() {
        if (time == null) {
            time = new DoubleFilter();
        }
        return time;
    }

    public void setTime(DoubleFilter time) {
        this.time = time;
    }

    public IntegerFilter getDiners() {
        return diners;
    }

    public IntegerFilter diners() {
        if (diners == null) {
            diners = new IntegerFilter();
        }
        return diners;
    }

    public void setDiners(IntegerFilter diners) {
        this.diners = diners;
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

    public LongFilter getIngredientquantityId() {
        return ingredientquantityId;
    }

    public LongFilter ingredientquantityId() {
        if (ingredientquantityId == null) {
            ingredientquantityId = new LongFilter();
        }
        return ingredientquantityId;
    }

    public void setIngredientquantityId(LongFilter ingredientquantityId) {
        this.ingredientquantityId = ingredientquantityId;
    }

    public LongFilter getToolId() {
        return toolId;
    }

    public LongFilter toolId() {
        if (toolId == null) {
            toolId = new LongFilter();
        }
        return toolId;
    }

    public void setToolId(LongFilter toolId) {
        this.toolId = toolId;
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
        final RecipeCriteria that = (RecipeCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(time, that.time) &&
            Objects.equals(diners, that.diners) &&
            Objects.equals(recipecategoryId, that.recipecategoryId) &&
            Objects.equals(ingredientquantityId, that.ingredientquantityId) &&
            Objects.equals(toolId, that.toolId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, time, diners, recipecategoryId, ingredientquantityId, toolId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RecipeCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (time != null ? "time=" + time + ", " : "") +
            (diners != null ? "diners=" + diners + ", " : "") +
            (recipecategoryId != null ? "recipecategoryId=" + recipecategoryId + ", " : "") +
            (ingredientquantityId != null ? "ingredientquantityId=" + ingredientquantityId + ", " : "") +
            (toolId != null ? "toolId=" + toolId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
