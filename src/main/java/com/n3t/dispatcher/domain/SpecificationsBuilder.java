package com.n3t.dispatcher.domain;

import org.springframework.data.jpa.domain.Specification;

/**
 * A generic builder class to construct a {@link Specification}.
 *
 * @param <T> the type of the domain object the specification applies to
 * @since 0.0.1
 */
public class SpecificationsBuilder<T> {

    private Specification<T> specification;

    public SpecificationsBuilder<T> addSpecification(Specification<T> newSpecification) {
        if (newSpecification == null) {
            return this;
        }

        if (this.specification == null) {
            this.specification = Specification.where(newSpecification);
        } else {
            this.specification = this.specification.and(newSpecification);
        }

        return this;
    }

    public Specification<T> build() {
        return this.specification;
    }
}
