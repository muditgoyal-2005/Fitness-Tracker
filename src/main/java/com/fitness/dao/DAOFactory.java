package com.fitness.dao;

import com.fitness.dao.impl.*;

// Factory pattern centralizes the concrete JDBC DAO implementations.
public final class DAOFactory {
    private static final DAOFactory INSTANCE=new DAOFactory();
    private final UserDAO users=new UserDAOImpl();
    private final WorkoutDAO workouts=new WorkoutDAOImpl();
    private final GoalDAO goals=new GoalDAOImpl();
    private final ChallengeDAO challenges=new ChallengeDAOImpl();
    private final ContentDAO content=new ContentDAOImpl();
    private final SettingsDAO settings=new SettingsDAOImpl();
    private final ActivityDAO activity=new ActivityDAOImpl();
    private DAOFactory(){ }
    public static DAOFactory getInstance(){return INSTANCE;}
    public UserDAO users(){return users;} public WorkoutDAO workouts(){return workouts;} public GoalDAO goals(){return goals;}
    public ChallengeDAO challenges(){return challenges;} public ContentDAO content(){return content;} public SettingsDAO settings(){return settings;} public ActivityDAO activity(){return activity;}
}