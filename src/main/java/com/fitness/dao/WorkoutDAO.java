package com.fitness.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import com.fitness.model.Workout;

public interface WorkoutDAO extends GenericDAO<Workout, Long> {
    List<Workout> findByUser(long userId, int offset, int limit);
    List<Workout> findByDateRange(long userId, LocalDate from, LocalDate to);
    Map<String, Double> calorieTotalsByType(long userId);
    double sumCalories(long userId, LocalDate from, LocalDate to);
    long countAll();
}