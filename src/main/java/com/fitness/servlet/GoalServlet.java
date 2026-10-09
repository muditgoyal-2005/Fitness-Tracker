package com.fitness.servlet;

import java.io.IOException;
import java.time.LocalDate;
import com.fitness.dao.DAOFactory;
import com.fitness.model.Goal;
import com.fitness.model.User;
import com.fitness.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/goals")
public class GoalServlet extends BaseServlet {
    @Override protected void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException{var user=currentUser(request);var goals=DAOFactory.getInstance().goals().findByUser(user.getId());var workouts=DAOFactory.getInstance().workouts();for(Goal goal:goals)goal.setCurrentValue(workouts.sumCalories(user.getId(),goal.getStartDate(),goal.getEndDate()));request.setAttribute("pageTitle","GOAL TRACKER");request.setAttribute("today",java.time.LocalDate.now());request.setAttribute("monthEnd",java.time.LocalDate.now().withDayOfMonth(java.time.LocalDate.now().lengthOfMonth()));request.setAttribute("goals",goals);forward(request,response,"user/goals");}
    @Override protected void doPost(HttpServletRequest request,HttpServletResponse response)throws IOException{
        if(!requireCsrf(request,response))return;User user=currentUser(request);var dao=DAOFactory.getInstance().goals();
        try{String action=request.getParameter("action");long id=request.getParameter("id")==null||request.getParameter("id").isBlank()?0:longParam(request,"id");
            if("delete".equals(action)){Goal goal=dao.findById(id).filter(g->g.getUserId()==user.getId()).orElseThrow(()->new IllegalArgumentException("Goal not found."));dao.delete(goal.getId());flash(request,"success","Goal removed.");}
            else{Goal goal=new Goal();goal.setId(id);goal.setUserId(user.getId());goal.setGoalType(ValidationUtil.required(request.getParameter("goalType"),"Goal name",80));goal.setTargetValue(Double.parseDouble(request.getParameter("target")));goal.setStartDate(LocalDate.parse(request.getParameter("startDate")));goal.setEndDate(LocalDate.parse(request.getParameter("endDate")));goal.setStatus("ACTIVE");
                if(goal.getTargetValue()<=0||goal.getEndDate().isBefore(goal.getStartDate()))throw new IllegalArgumentException("Target must be positive and the end date must follow the start date.");
                if(id>0){if(dao.findById(id).filter(g->g.getUserId()==user.getId()).isEmpty())throw new IllegalArgumentException("Goal not found.");dao.update(goal);flash(request,"success","Goal updated.");}else{dao.save(goal);flash(request,"success","Goal set. Progress starts now.");}}
        }catch(RuntimeException e){flash(request,"error",e.getMessage()==null?"Could not save goal.":e.getMessage());}redirect(request,response,"/goals");
    }
}