package com.fitness.dao;

import java.util.List;
import java.util.Map;
import com.fitness.model.Challenge;

public interface ChallengeDAO extends GenericDAO<Challenge, Long> {
    List<Challenge> findForUser(long userId);
    void join(long challengeId, long userId);
    boolean leave(long challengeId, long userId);
    List<Map<String, Object>> leaderboard(long challengeId);
    long participantCount();
    void updateProgressForUser(long userId);
    void archiveExpired();
}