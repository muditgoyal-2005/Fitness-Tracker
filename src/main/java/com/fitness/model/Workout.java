package com.fitness.model;

import java.time.LocalDate;
import com.fitness.enums.Intensity;
import com.fitness.enums.WorkoutType;

public class Workout {
    private long id;
    private long userId;
    private WorkoutType type;
    private int durationMinutes;
    private Intensity intensity;
    private double calories;
    private LocalDate workoutDate;
    private String notes;
    public Workout() { }
    private Workout(Builder builder) {
        id = builder.id; userId = builder.userId; type = builder.type; durationMinutes = builder.durationMinutes;
        intensity = builder.intensity; workoutDate = builder.workoutDate; notes = builder.notes; calories = builder.calories;
    }
    public double calculateCalories(double weightKg) { return type.calories(weightKg, durationMinutes, intensity); }
    // Builder pattern keeps validated workout construction readable at servlet boundaries.
    public static class Builder {
        private long id, userId;
        private WorkoutType type;
        private int durationMinutes;
        private Intensity intensity = Intensity.MEDIUM;
        private double calories;
        private LocalDate workoutDate = LocalDate.now();
        private String notes = "";
        public Builder id(long value) { id=value; return this; }
        public Builder userId(long value) { userId=value; return this; }
        public Builder type(WorkoutType value) { type=value; return this; }
        public Builder durationMinutes(int value) { durationMinutes=value; return this; }
        public Builder intensity(Intensity value) { intensity=value; return this; }
        public Builder calories(double value) { calories=value; return this; }
        public Builder workoutDate(LocalDate value) { workoutDate=value; return this; }
        public Builder notes(String value) { notes=value; return this; }
        public Workout build() { if (type == null || durationMinutes < 1) throw new IllegalArgumentException("Workout type and positive duration are required"); return new Workout(this); }
    }
    public long getId() { return id; }
    public void setId(long value) { id=value; }
    public long getUserId() { return userId; }
    public void setUserId(long value) { userId=value; }
    public WorkoutType getType() { return type; }
    public void setType(WorkoutType value) { type=value; }
    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int value) { durationMinutes=value; }
    public Intensity getIntensity() { return intensity; }
    public void setIntensity(Intensity value) { intensity=value; }
    public double getCalories() { return calories; }
    public void setCalories(double value) { calories=value; }
    public LocalDate getWorkoutDate() { return workoutDate; }
    public void setWorkoutDate(LocalDate value) { workoutDate=value; }
    public String getNotes() { return notes; }
    public void setNotes(String value) { notes=value; }
}