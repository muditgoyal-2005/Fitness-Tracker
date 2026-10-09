package com.fitness.servlet;

import java.io.IOException;
import com.fitness.dao.DAOFactory;
import com.fitness.model.User;
import com.fitness.service.ActivityService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/admin/content")
public class AdminContentServlet extends BaseServlet {
    @Override protected void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException{request.setAttribute("pageTitle","CONTENT REVIEW");request.setAttribute("contentItems",DAOFactory.getInstance().content().findAll());forward(request,response,"admin/content");}
    @Override protected void doPost(HttpServletRequest request,HttpServletResponse response)throws IOException{
        if(!requireCsrf(request,response))return;try{long id=longParam(request,"id");String status=request.getParameter("status");if(!DAOFactory.getInstance().content().review(id,currentUser(request).getId(),status))throw new IllegalArgumentException("Content is no longer pending.");flash(request,"success","Submission "+status.toLowerCase()+".");((ActivityService)getServletContext().getAttribute("activityService")).log("CONTENT_"+status,"Reviewed content #"+id,currentUser(request).getId());}
        catch(RuntimeException e){flash(request,"error",e.getMessage()==null?"Review failed.":e.getMessage());}redirect(request,response,"/admin/content");
    }
}