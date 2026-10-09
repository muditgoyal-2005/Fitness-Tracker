package com.fitness.dao;

import java.util.List;
import java.util.Optional;

// Generics keep common persistence operations type-safe across model and identifier types.
public interface GenericDAO<T, ID> {
    Optional<T> findById(ID id);
    List<T> findAll();
    T save(T entity);
    boolean update(T entity);
    boolean delete(ID id);
}