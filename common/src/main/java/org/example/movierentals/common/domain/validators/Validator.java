package org.example.movierentals.common.domain.validators;

public interface Validator<T> {
    void validate(T entity);
}
