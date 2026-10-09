package com.fitness.service;

import java.util.List;
import com.fitness.model.User;

public interface Recommendable { List<String> recommendationsFor(User user); }