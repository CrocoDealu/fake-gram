package org.example.sharedInfrastructure;

import org.example.exceptions.DomainValidationException;


/**
 * Interface for defining a validation strategy for different entity types.
 * Classes implementing this interface provide specific validation rules
 * for various entities and throw a DomainValidationException when validation fails.
 *
 * @param <T> the type of the entity being validated
 */
public interface Validator<T> {
    void validate(T entity) throws DomainValidationException;
}
