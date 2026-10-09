package com.fitness.model;

import com.fitness.enums.Role;

// OOP polymorphism: administrators override the dashboard destination.
public final class Admin extends User {
    public Admin() { setRole(Role.ADMIN); }
    public Admin(long id, String name, String email, String passwordHash, double weightKg, double heightCm) {
        super(id, name, email, passwordHash, weightKg, heightCm, Role.ADMIN);
    }
    @Override public String getDashboardPath() { return "/admin/dashboard"; }
}