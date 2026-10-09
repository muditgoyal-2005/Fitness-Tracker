package com.fitness.filter;

import java.io.IOException;
import com.fitness.enums.Role;
import com.fitness.model.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;

@WebFilter("/admin/*")
public class RoleAuthorizationFilter implements Filter {
    @Override public void doFilter(ServletRequest req,ServletResponse res,FilterChain chain)throws IOException,ServletException{
        HttpServletRequest request=(HttpServletRequest)req;HttpServletResponse response=(HttpServletResponse)res;Object value=request.getSession(false)==null?null:request.getSession(false).getAttribute("user");
        if(value instanceof User user&&user.getRole()==Role.ADMIN){chain.doFilter(req,res);return;}
        response.sendError(HttpServletResponse.SC_FORBIDDEN,"Administrator access required.");
    }
}