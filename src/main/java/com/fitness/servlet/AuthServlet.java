package com.fitness.servlet;

import java.io.IOException;
import com.fitness.exception.DuplicateEmailException;
import com.fitness.exception.InvalidCredentialsException;
import com.fitness.model.User;
import com.fitness.service.AuthService;
import com.fitness.util.WebUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/auth/*")
public class AuthServlet extends BaseServlet {
    @Override protected void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException{
        String action=request.getPathInfo();
        if("/logout".equals(action)){request.getSession().invalidate();redirect(request,response,"/auth/login");return;}
        if(request.getSession(false)!=null&&request.getSession(false).getAttribute("user") instanceof User user){redirect(request,response,user.getDashboardPath());return;}
        forward(request,response,"common/"+("/register".equals(action)?"register":"login"));
    }
    @Override protected void doPost(HttpServletRequest request,HttpServletResponse response)throws IOException{
        if(!WebUtil.validCsrf(request)){flash(request,"error","Your form expired. Please try again.");redirect(request,response,"/auth/login");return;}
        String action=request.getPathInfo();
        if("/logout".equals(action)){request.getSession().invalidate();redirect(request,response,"/auth/login");return;}
        AuthService auth=(AuthService)getServletContext().getAttribute("authService");
        try{
            if("/register".equals(action)){User user=auth.register(request.getParameter("name"),request.getParameter("email"),request.getParameter("password"));request.getSession().setAttribute("user",user);flash(request,"success","Your training profile is ready.");redirect(request,response,user.getDashboardPath());return;}
            User user=auth.login(request.getParameter("email"),request.getParameter("password"));request.getSession().invalidate();HttpSession session=request.getSession(true);session.setAttribute("user",user);flash(request,"success","Welcome back, "+user.getName()+".");redirect(request,response,user.getDashboardPath());
        }catch(InvalidCredentialsException|DuplicateEmailException|IllegalArgumentException e){flash(request,"error",e.getMessage());redirect(request,response,"/auth"+("/register".equals(action)?"/register":"/login"));}
    }
}