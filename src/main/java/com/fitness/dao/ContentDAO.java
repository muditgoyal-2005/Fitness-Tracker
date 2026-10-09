package com.fitness.dao;

import java.util.List;
import com.fitness.model.FitnessContent;

public interface ContentDAO extends GenericDAO<FitnessContent, Long> {
    List<FitnessContent> findByStatus(String status);
    List<FitnessContent> findByUser(long userId);
    boolean review(long contentId, long adminId, String status);
    long countPending();
}