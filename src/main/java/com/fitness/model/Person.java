package com.fitness.model;

import com.fitness.enums.Role;

// OOP abstraction and encapsulation: role-specific people share protected construction and private state.
public abstract class Person {
    private long id;
    private String name;
    private String email;
    private Role role;

    protected Person() { }
    protected Person(long id, String name, String email, Role role) {
        this.id = id; this.name = name; this.email = email; this.role = role;
    }
    public abstract String getDashboardPath();
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}