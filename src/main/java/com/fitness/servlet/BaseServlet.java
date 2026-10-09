package com.fitness.servlet;

import java.io.IOException;
import com.fitness.model.User;
import com.fitness.util.WebUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public abstract class BaseServlet extends HttpServlet {
    protected User currentUser(HttpServletRequest request){return (User)request.getSession().getAttribute("user");}
    protected void flash(HttpServletRequest request,String kind,String message){request.getSession().setAttribute("flashKind",kind);request.getSession().setAttribute("flashMessage",message);}
    protected void redirect(HttpServletRequest request,HttpServletResponse response,String path)throws IOException{response.sendRedirect(request.getContextPath()+path);}
    protected void forward(HttpServletRequest request,HttpServletResponse response,String view)throws ServletException,IOException{
        HttpSession session=request.getSession();request.setAttribute("csrfToken",WebUtil.csrfToken(session));
        request.setAttribute("Math",new com.fitness.util.JspMath());
        Object message=session.getAttribute("flashMessage");if(message!=null){request.setAttribute("flashMessage",message);request.setAttribute("flashKind",session.getAttribute("flashKind"));session.removeAttribute("flashMessage");session.removeAttribute("flashKind");}
        request.getRequestDispatcher("/WEB-INF/views/"+view+".jsp").forward(request,response);
    }
    protected boolean requireCsrf(HttpServletRequest request,HttpServletResponse response)throws IOException{
        if(WebUtil.validCsrf(request))return true;flash(request,"error","Your form expired. Please try again.");response.sendRedirect(request.getContextPath()+request.getRequestURI().substring(request.getContextPath().length()));return false;
    }
    protected long longParam(HttpServletRequest request,String name){try{return Long.parseLong(request.getParameter(name));}catch(RuntimeException e){throw new IllegalArgumentException("Invalid "+name+".");}}
}