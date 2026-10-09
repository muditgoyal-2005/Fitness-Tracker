package com.fitness.model;

import com.fitness.enums.Role;
import java.time.LocalDateTime;

// OOP inheritance: a member is a Person and supplies the shared dashboard behavior.
public class User extends Person {
    private String passwordHash;
    private double weightKg;
    private double heightCm;
    private LocalDateTime createdAt;
    public User() { setRole(Role.USER); }
    public User(long id, String name, String email, String passwordHash, double weightKg, double heightCm, Role role) {
        super(id, name, email, role); this.passwordHash = passwordHash; this.weightKg = weightKg; this.heightCm = heightCm;
    }
    @Override public String getDashboardPath() { return getRole() == Role.ADMIN ? "/admin/dashboard" : "/dashboard"; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public double getWeightKg() { return weightKg; }
    public void setWeightKg(double weightKg) { this.weightKg = weightKg; }
    public double getHeightCm() { return heightCm; }
    public void setHeightCm(double heightCm) { this.heightCm = heightCm; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}