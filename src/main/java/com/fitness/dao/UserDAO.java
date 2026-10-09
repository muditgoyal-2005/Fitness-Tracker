package com.fitness.dao;

import java.util.List;
import java.util.Optional;
import com.fitness.model.User;

public interface UserDAO extends GenericDAO<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> search(String query, int offset, int limit);
    long countUsers();
    boolean updateProfile(User user, boolean replacePassword);
}