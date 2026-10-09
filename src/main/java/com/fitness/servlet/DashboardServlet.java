package com.fitness.servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.fitness.dao.DAOFactory;
import com.fitness.enums.Role;
import com.fitness.model.User;
import com.fitness.service.Recommendable;
import com.fitness.service.WorkoutService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet({"/dashboard","/admin/dashboard"})
public class DashboardServlet extends BaseServlet {
    @Override protected void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException{
        User user=currentUser(request);DAOFactory dao=DAOFactory.getInstance();
        if(user.getRole()==Role.ADMIN){
            request.setAttribute("pageTitle","ADMIN / OVERVIEW");
            request.setAttribute("userCount",dao.users().countUsers());request.setAttribute("workoutCount",dao.workouts().countAll());
            request.setAttribute("participantCount",dao.challenges().participantCount());request.setAttribute("pendingCount",dao.content().countPending());
            request.setAttribute("activity",dao.activity().latest(12));request.setAttribute("activeUsers",getServletContext().getAttribute("activeUsers"));
            forward(request,response,"admin/dashboard");return;
        }
        WorkoutService workout=(WorkoutService)getServletContext().getAttribute("workoutService");
        LocalDate today=LocalDate.now();
        var todayWorkouts=dao.workouts().findByDateRange(user.getId(),today,today);
        var monthWorkouts=dao.workouts().findByDateRange(user.getId(),today.withDayOfMonth(1),today);
        double todayCalories=todayWorkouts.stream().mapToDouble(w->w.getCalories()).sum();
        int todayMinutes=todayWorkouts.stream().mapToInt(w->w.getDurationMinutes()).sum();
        request.setAttribute("pageTitle","DAILY READOUT");request.setAttribute("pageDate",today);
        request.setAttribute("todayCalories",Math.round(todayCalories));request.setAttribute("todayMinutes",todayMinutes);
        request.setAttribute("todayPercent",Math.min(100,(int)(todayCalories*100/600)));
        request.setAttribute("minutePercent",Math.min(100,todayMinutes*100/45));request.setAttribute("monthWorkoutCount",monthWorkouts.size());
        request.setAttribute("recentWorkouts",workout.recent(user.getId()));request.setAttribute("weeklyCalories",workout.weekCalories(user.getId()));
        double monthlyCalories=workout.monthCalories(user.getId());request.setAttribute("monthlyCalories",monthlyCalories);request.setAttribute("workoutTypes",dao.workouts().calorieTotalsByType(user.getId()));
        var goals=dao.goals().findByUser(user.getId());var activeGoal=goals.stream().filter(g->"ACTIVE".equals(g.getStatus())).findFirst();
        request.setAttribute("activeGoalTarget",activeGoal.map(g->g.getTargetValue()).orElse(3000.0));
        request.setAttribute("goalPercent",activeGoal.map(g->(int)Math.min(100,monthlyCalories*100/g.getTargetValue())).orElse(0));
        request.setAttribute("goals",goals);request.setAttribute("challenges",dao.challenges().findForUser(user.getId()));
        request.setAttribute("recommendations",((Recommendable)getServletContext().getAttribute("recommendationService")).recommendationsFor(user));
        request.setAttribute("monthStart",today.withDayOfMonth(1));request.setAttribute("monthEnd",today.withDayOfMonth(today.lengthOfMonth()));
        LocalDate first=today.minusDays(27);
        Map<LocalDate,Integer> minutesByDate=new HashMap<>(dao.workouts().findByDateRange(user.getId(),first,today).stream().collect(Collectors.groupingBy(w->w.getWorkoutDate(),Collectors.summingInt(w->w.getDurationMinutes()))));
        List<Map<String,Object>> heatmap=new ArrayList<>();
        for(int offset=27;offset>=0;offset--){LocalDate day=today.minusDays(offset);int minutes=minutesByDate.getOrDefault(day,0);Map<String,Object> cell=new LinkedHashMap<>();cell.put("date",day);cell.put("minutes",minutes);cell.put("level",minutes==0?0:minutes<20?1:minutes<45?2:3);heatmap.add(cell);}
        int streak=0;for(int offset=0;offset<28;offset++){int minutes=minutesByDate.getOrDefault(today.minusDays(offset),0);if(minutes>0)streak++;else if(offset>0)break;}
        request.setAttribute("heatmapDays",heatmap);request.setAttribute("streakDays",streak);
        forward(request,response,"user/dashboard");
    }
}