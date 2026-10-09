package com.fitness.service;

import java.time.LocalDate;
import java.util.List;
import com.fitness.dao.ChallengeDAO;
import com.fitness.dao.WorkoutDAO;
import com.fitness.model.User;
import com.fitness.model.Workout;

public class WorkoutService {
    private final WorkoutDAO workouts;
    private final ChallengeDAO challenges;
    public WorkoutService(WorkoutDAO workouts,ChallengeDAO challenges){this.workouts=workouts;this.challenges=challenges;}
    public Workout create(Workout workout,User user){workout.setUserId(user.getId());workout.setCalories(workout.calculateCalories(user.getWeightKg()));Workout saved=workouts.save(workout);challenges.updateProgressForUser(user.getId());return saved;}
    public boolean update(Workout workout,User user){workout.setUserId(user.getId());workout.setCalories(workout.calculateCalories(user.getWeightKg()));boolean changed=workouts.update(workout);if(changed)challenges.updateProgressForUser(user.getId());return changed;}
    public boolean delete(long workoutId,User user){boolean changed=workouts.delete(workoutId);if(changed)challenges.updateProgressForUser(user.getId());return changed;}
    public List<Workout> recent(long userId){return workouts.findByUser(userId,0,8);}
    public double weekCalories(long userId){LocalDate today=LocalDate.now();return workouts.sumCalories(userId,today.with(java.time.DayOfWeek.MONDAY),today.with(java.time.DayOfWeek.SUNDAY));}
    public double monthCalories(long userId){LocalDate today=LocalDate.now();return workouts.sumCalories(userId,today.withDayOfMonth(1),today.withDayOfMonth(today.lengthOfMonth()));}
}