package com.fitness.enums;

public enum WorkoutType {
    RUNNING(9.8), CYCLING(7.5), STRENGTH(5.0), SWIMMING(8.0), WALKING(3.8), YOGA(2.5);
    private final double met;
    WorkoutType(double met) { this.met = met; }
    public double getMet() { return met; }
    public double calories(double weightKg, int durationMinutes, Intensity intensity) {
        return met * intensity.getMetMultiplier() * weightKg * (durationMinutes / 60.0);
    }
}