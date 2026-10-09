package com.fitness.servlet;

import java.io.IOException;
import com.fitness.dao.DAOFactory;
import com.fitness.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/admin/settings")
public class AdminSettingsServlet extends BaseServlet {
    @Override protected void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException{request.setAttribute("pageTitle","SYSTEM CONTROLS");request.setAttribute("settings",DAOFactory.getInstance().settings().findAll());forward(request,response,"admin/settings");}
    @Override protected void doPost(HttpServletRequest request,HttpServletResponse response)throws IOException{
        if(!requireCsrf(request,response))return;try{String key=ValidationUtil.required(request.getParameter("key"),"Setting key",100);String value=ValidationUtil.required(request.getParameter("value"),"Setting value",500);if(!key.matches("[a-zA-Z0-9_.-]+"))throw new IllegalArgumentException("Setting key contains unsupported characters.");if("max_challenges".equals(key))ValidationUtil.positiveInt(value,"Maximum challenges",1000);DAOFactory.getInstance().settings().put(key,value);flash(request,"success","System setting saved.");}
        catch(RuntimeException e){flash(request,"error",e.getMessage()==null?"Setting update failed.":e.getMessage());}redirect(request,response,"/admin/settings");
    }
}