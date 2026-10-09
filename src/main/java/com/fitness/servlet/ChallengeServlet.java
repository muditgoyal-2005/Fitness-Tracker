package com.fitness.servlet;

import java.io.IOException;
import com.fitness.dao.DAOFactory;
import com.fitness.model.User;
import com.fitness.service.ActivityService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/challenges")
public class ChallengeServlet extends BaseServlet {
    @Override protected void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException{
        request.setAttribute("pageTitle","CHALLENGE CIRCUIT");
        var dao=DAOFactory.getInstance().challenges();request.setAttribute("challenges",dao.findForUser(currentUser(request).getId()));
        String challenge=request.getParameter("leaderboard");if(challenge!=null)try{request.setAttribute("leaderboard",dao.leaderboard(Long.parseLong(challenge)).stream().limit(3).toList());request.setAttribute("leaderboardId",challenge);}catch(NumberFormatException e){request.setAttribute("leaderboard",java.util.List.of());}
        forward(request,response,"user/challenges");
    }
    @Override protected void doPost(HttpServletRequest request,HttpServletResponse response)throws IOException{
        if(!requireCsrf(request,response))return;User user=currentUser(request);var dao=DAOFactory.getInstance().challenges();long id;
        try{id=longParam(request,"challengeId");String action=request.getParameter("action");
            if("join".equals(action)){var challenge=dao.findById(id).orElseThrow(()->new IllegalArgumentException("Challenge not found."));if(!"ACTIVE".equals(challenge.getStatus()))throw new IllegalArgumentException("This challenge is not open for joining.");dao.join(id,user.getId());flash(request,"success","You joined "+challenge.getTitle()+".");}
            else if("leave".equals(action)){if(!dao.leave(id,user.getId()))throw new IllegalArgumentException("You are not enrolled in that challenge.");flash(request,"success","You left the challenge.");}
            else throw new IllegalArgumentException("Unknown challenge action.");
            if(!"join".equals(action))((ActivityService)getServletContext().getAttribute("activityService")).log("CHALLENGE_"+action.toUpperCase(),"Challenge #"+id,user.getId());
        }catch(RuntimeException e){flash(request,"error",e.getMessage()==null?"Challenge update failed.":e.getMessage());}
        redirect(request,response,"/challenges");
    }
}