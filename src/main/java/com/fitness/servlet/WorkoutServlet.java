package com.fitness.servlet;

import java.io.IOException;
import java.time.LocalDate;
import com.fitness.dao.DAOFactory;
import com.fitness.enums.Intensity;
import com.fitness.enums.WorkoutType;
import com.fitness.model.User;
import com.fitness.model.Workout;
import com.fitness.service.ActivityService;
import com.fitness.service.WorkoutService;
import com.fitness.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/workouts")
public class WorkoutServlet extends BaseServlet {
    @Override protected void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException{
        int page;try{page=Math.max(1,Integer.parseInt(request.getParameter("page")));}catch(RuntimeException e){page=1;}
        request.setAttribute("pageTitle","WORKOUT LOG");request.setAttribute("today",java.time.LocalDate.now());
        request.setAttribute("workouts",DAOFactory.getInstance().workouts().findByUser(currentUser(request).getId(),(page-1)*12,12));
        request.setAttribute("page",page);request.setAttribute("types",WorkoutType.values());request.setAttribute("intensities",Intensity.values());forward(request,response,"user/workouts");
    }
    @Override protected void doPost(HttpServletRequest request,HttpServletResponse response)throws IOException{
        if(!requireCsrf(request,response))return;User user=currentUser(request);DAOFactory dao=DAOFactory.getInstance();WorkoutService service=(WorkoutService)getServletContext().getAttribute("workoutService");
        try{
            String action=request.getParameter("action");long id=request.getParameter("id")==null||request.getParameter("id").isBlank()?0:longParam(request,"id");
            if("delete".equals(action)){var found=dao.workouts().findById(id);if(found.isEmpty()||found.get().getUserId()!=user.getId())throw new IllegalArgumentException("Workout not found.");service.delete(id,user);flash(request,"success","Workout removed.");}
            else {Workout workout=new Workout.Builder().id(id).userId(user.getId()).type(WorkoutType.valueOf(request.getParameter("type")))
                    .durationMinutes(ValidationUtil.positiveInt(request.getParameter("duration"),"Duration",1440)).intensity(Intensity.valueOf(request.getParameter("intensity")))
                    .workoutDate(LocalDate.parse(request.getParameter("workoutDate"))).notes(request.getParameter("notes")==null?"":request.getParameter("notes").trim()).build();
                if(id>0){if(dao.workouts().findById(id).filter(w->w.getUserId()==user.getId()).isEmpty())throw new IllegalArgumentException("Workout not found.");service.update(workout,user);flash(request,"success","Workout updated.");}
                else{service.create(workout,user);flash(request,"success","Workout logged. Keep that momentum.");}
                ((ActivityService)getServletContext().getAttribute("activityService")).log(id>0?"WORKOUT_UPDATED":"WORKOUT_LOGGED",workout.getType()+" · "+workout.getDurationMinutes()+" minutes",user.getId());
            }
        }catch(RuntimeException e){flash(request,"error",e.getMessage()==null?"Could not save workout.":e.getMessage());}
        redirect(request,response,"/workouts");
    }
}