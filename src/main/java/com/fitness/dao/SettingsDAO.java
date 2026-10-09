package com.fitness.dao;

import java.util.Map;

public interface SettingsDAO { Map<String, String> findAll(); void put(String key, String value); }