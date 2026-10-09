package com.fitness.dao;

import java.util.List;
import com.fitness.model.Activity;

public interface ActivityDAO { void save(Activity activity); List<Activity> latest(int limit); void deleteOlderThanDays(int days); }