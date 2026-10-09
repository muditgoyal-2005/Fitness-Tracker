package com.fitness.servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import com.google.gson.Gson;
import com.fitness.dao.DAOFactory;
import com.fitness.enums.Role;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/api/stats")
public class StatsApiServlet extends BaseServlet {
    private final Gson gson=new Gson();
    @Override protected void doGet(HttpServletRequest request,HttpServletResponse response)throws IOException{
        response.setContentType("application/json;charset=UTF-8");var dao=DAOFactory.getInstance();Map<String,Object> data=new LinkedHashMap<>();
        if(currentUser(request).getRole()==Role.ADMIN){data.put("users",dao.users().countUsers());data.put("workouts",dao.workouts().countAll());data.put("participants",dao.challenges().participantCount());data.put("pending",dao.content().countPending());}
        else{long userId=currentUser(request).getId();LocalDate today=LocalDate.now();List<String> labels=new ArrayList<>();List<Double> weekly=new ArrayList<>();
            for(int offset=6;offset>=0;offset--){LocalDate day=today.minusDays(offset);labels.add(day.getDayOfWeek().name().substring(0,3));weekly.add(dao.workouts().sumCalories(userId,day,day));}
            data.put("labels",labels);data.put("weeklyCalories",weekly);data.put("types",dao.workouts().calorieTotalsByType(userId));data.put("monthCalories",dao.workouts().sumCalories(userId,today.withDayOfMonth(1),today));}
        response.getWriter().write(gson.toJson(data));
    }
}