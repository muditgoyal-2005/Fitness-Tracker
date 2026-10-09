package com.fitness.service;

import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.fitness.dao.ActivityDAO;
import com.fitness.model.Activity;
import com.fitness.util.Loggable;

// Background activity persistence keeps request handling independent of audit writes.
public class ActivityService implements Loggable {
    private static final Logger LOGGER=Logger.getLogger(ActivityService.class.getName());
    private final ActivityDAO activities;
    private final ExecutorService executor;
    public ActivityService(ActivityDAO activities,ExecutorService executor){this.activities=activities;this.executor=executor;}
    @Override public void log(String action,String details,Long userId){executor.submit(()->{try{Activity event=new Activity();event.setAction(action);event.setDetails(details);event.setUserId(userId);activities.save(event);}catch(RuntimeException e){LOGGER.log(Level.WARNING,"Background activity log failed",e);}});}
    public ActivityDAO getActivities(){return activities;}
}