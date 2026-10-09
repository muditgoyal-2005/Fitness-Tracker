package com.fitness.servlet;

import java.io.IOException;
import com.fitness.dao.DAOFactory;
import com.fitness.model.FitnessContent;
import com.fitness.model.User;
import com.fitness.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/content")
public class ContentServlet extends BaseServlet {
    @Override protected void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException{request.setAttribute("pageTitle","COMMUNITY NOTEBOOK");request.setAttribute("contentItems",DAOFactory.getInstance().content().findByUser(currentUser(request).getId()));forward(request,response,"user/content");}
    @Override protected void doPost(HttpServletRequest request,HttpServletResponse response)throws IOException{
        if(!requireCsrf(request,response))return;
        try{FitnessContent item=new FitnessContent();item.setUserId(currentUser(request).getId());item.setTitle(ValidationUtil.required(request.getParameter("title"),"Title",160));item.setBody(ValidationUtil.required(request.getParameter("body"),"Content",5000));DAOFactory.getInstance().content().save(item);flash(request,"success","Submitted for admin review.");}
        catch(RuntimeException e){flash(request,"error",e.getMessage()==null?"Submission failed.":e.getMessage());}redirect(request,response,"/content");
    }
}