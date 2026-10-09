package com.fitness.service;

import java.util.*;
import java.util.stream.Collectors;
import com.fitness.dao.WorkoutDAO;
import com.fitness.enums.WorkoutType;
import com.fitness.model.User;
import com.fitness.model.Workout;

public class RecommendationService implements Recommendable {
    private final WorkoutDAO workouts;
    public RecommendationService(WorkoutDAO workouts){this.workouts=workouts;}
    @Override public List<String> recommendationsFor(User user){
        List<Workout> history=workouts.findByUser(user.getId(),0,30);
        // Streams group and rank the training history before selecting contextual advice.
        Optional<Map.Entry<String,Long>> mostFrequent=history.stream().collect(Collectors.groupingBy(w->w.getType().name(),Collectors.counting())).entrySet().stream().max(Map.Entry.comparingByValue());
        List<String> tips=new ArrayList<>();
        mostFrequent.ifPresent(entry->tips.add("Balance your "+entry.getKey().toLowerCase(Locale.ROOT)+" routine with a recovery or mobility session."));
        double average=history.stream().mapToDouble(Workout::getDurationMinutes).average().orElse(0);
        if(average==0)tips.add("Log your first session to unlock recommendations shaped around your training.");
        else if(average<35)tips.add("Try adding five easy minutes to one weekly session to build aerobic capacity gradually.");
        else tips.add("Protect recovery between longer sessions with a lighter movement day.");
        WorkoutType next=history.stream().map(Workout::getType).distinct().findFirst().map(t->t==WorkoutType.RUNNING?WorkoutType.YOGA:WorkoutType.RUNNING).orElse(WorkoutType.WALKING);
        tips.add("Next up: a measured "+next.name().toLowerCase(Locale.ROOT)+" session can round out your weekly mix.");
        return tips;
    }
}