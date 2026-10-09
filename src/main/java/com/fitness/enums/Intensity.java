package com.fitness.enums;

public enum Intensity {
    LOW(0.75), MEDIUM(1.0), HIGH(1.25);
    private final double metMultiplier;
    Intensity(double metMultiplier) { this.metMultiplier = metMultiplier; }
    public double getMetMultiplier() { return metMultiplier; }
}