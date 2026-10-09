package com.fitness.dao;

import java.util.List;
import com.fitness.model.Goal;

public interface GoalDAO extends GenericDAO<Goal, Long> { List<Goal> findByUser(long userId); }