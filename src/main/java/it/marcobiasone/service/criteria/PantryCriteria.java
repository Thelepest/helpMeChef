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
import tech.jhipster.service.filter.ZonedDateTimeFilter;

/**
 * Criteria class for the {@link it.marcobiasone.domain.Pantry} entity. This class is used
 * in {@link it.marcobiasone.web.rest.PantryResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /pantries?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class PantryCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private BooleanFilter active;

    private StringFilter description;

    private ZonedDateTimeFilter createdAt;

    private StringFilter userId;

    private LongFilter ingredientquantityId;

    private Boolean distinct;

    public PantryCriteria() {}

    public PantryCriteria(PantryCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.active = other.active == null ? null : other.active.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.createdAt = other.createdAt == null ? null : other.createdAt.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
        this.ingredientquantityId = other.ingredientquantityId == null ? null : other.ingredientquantityId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public PantryCriteria copy() {
        return new PantryCriteria(this);
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

    public BooleanFilter getActive() {
        return active;
    }

    public BooleanFilter active() {
        if (active == null) {
            active = new BooleanFilter();
        }
        return active;
    }

    public void setActive(BooleanFilter active) {
        this.active = active;
    }

    public StringFilter getDescription() {
        return description;
    }

    public StringFilter description() {
        if (description == null) {
            description = new StringFilter();
        }
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public ZonedDateTimeFilter getCreatedAt() {
        return createdAt;
    }

    public ZonedDateTimeFilter createdAt() {
        if (createdAt == null) {
            createdAt = new ZonedDateTimeFilter();
        }
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTimeFilter createdAt) {
        this.createdAt = createdAt;
    }

    public StringFilter getUserId() {
        return userId;
    }

    public StringFilter userId() {
        if (userId == null) {
            userId = new StringFilter();
        }
        return userId;
    }

    public void setUserId(StringFilter userId) {
        this.userId = userId;
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
        final PantryCriteria that = (PantryCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(active, that.active) &&
            Objects.equals(description, that.description) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(ingredientquantityId, that.ingredientquantityId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, active, description, createdAt, userId, ingredientquantityId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PantryCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (active != null ? "active=" + active + ", " : "") +
            (description != null ? "description=" + description + ", " : "") +
            (createdAt != null ? "createdAt=" + createdAt + ", " : "") +
            (userId != null ? "userId=" + userId + ", " : "") +
            (ingredientquantityId != null ? "ingredientquantityId=" + ingredientquantityId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
