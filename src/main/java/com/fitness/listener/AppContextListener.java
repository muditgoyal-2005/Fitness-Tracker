package com.fitness.listener;

import java.sql.Connection;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.fitness.dao.DAOFactory;
import com.fitness.service.*;
import com.fitness.util.DBConnectionManager;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {
    private static final Logger LOGGER=Logger.getLogger(AppContextListener.class.getName());
    @Override public void contextInitialized(ServletContextEvent event){
        DAOFactory dao=DAOFactory.getInstance();ExecutorService activityPool=Executors.newFixedThreadPool(2);ScheduledExecutorService scheduler=Executors.newSingleThreadScheduledExecutor();
        event.getServletContext().setAttribute("daoFactory",dao);
        event.getServletContext().setAttribute("authService",new AuthService(dao.users()));
        event.getServletContext().setAttribute("workoutService",new WorkoutService(dao.workouts(),dao.challenges()));
        event.getServletContext().setAttribute("recommendationService",new RecommendationService(dao.workouts()));
        event.getServletContext().setAttribute("activityService",new ActivityService(dao.activity(),activityPool));
        event.getServletContext().setAttribute("activityPool",activityPool);event.getServletContext().setAttribute("scheduler",scheduler);
        // A scheduled worker retains audit history and completes challenges after their end date.
        scheduler.scheduleAtFixedRate(()->{try{dao.activity().deleteOlderThanDays(90);dao.challenges().archiveExpired();}catch(RuntimeException e){LOGGER.log(Level.WARNING,"Scheduled fitness data cleanup failed",e);}},1,24,TimeUnit.HOURS);
        try(Connection ignored=DBConnectionManager.getInstance().getConnection()){LOGGER.info("Fitness tracker database connection is ready");}
        catch(Exception e){LOGGER.log(Level.WARNING,"Database is not reachable yet; requests will retry when used",e);}
    }
    @Override public void contextDestroyed(ServletContextEvent event){
        shutdown((ExecutorService)event.getServletContext().getAttribute("activityPool"));
        shutdown((ExecutorService)event.getServletContext().getAttribute("scheduler"));
    }
    private void shutdown(ExecutorService executor){if(executor==null)return;executor.shutdown();try{if(!executor.awaitTermination(5,TimeUnit.SECONDS))executor.shutdownNow();}catch(InterruptedException e){executor.shutdownNow();Thread.currentThread().interrupt();}}
}