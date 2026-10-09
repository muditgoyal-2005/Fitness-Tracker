package com.fitness.filter;

import java.io.IOException;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;

@WebFilter("/*")
public class AuthenticationFilter implements Filter {
    @Override public void doFilter(ServletRequest req,ServletResponse res,FilterChain chain)throws IOException,ServletException{
        HttpServletRequest request=(HttpServletRequest)req;HttpServletResponse response=(HttpServletResponse)res;String path=request.getRequestURI().substring(request.getContextPath().length());
        boolean publicPath=path.equals("/")||path.equals("/index.jsp")||path.startsWith("/auth/")||path.startsWith("/css/")||path.startsWith("/js/")||path.startsWith("/images/")||path.equals("/favicon.ico");
        if(publicPath||request.getSession(false)!=null&&request.getSession(false).getAttribute("user")!=null){chain.doFilter(req,res);return;}
        request.getSession(true).setAttribute("flashMessage","Sign in to continue.");request.getSession().setAttribute("flashKind","error");response.sendRedirect(request.getContextPath()+"/auth/login");
    }
}