package com.fitness.servlet;

import java.io.IOException;
import com.fitness.dao.DAOFactory;
import com.fitness.enums.Role;
import com.fitness.model.User;
import com.fitness.util.PasswordUtil;
import com.fitness.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/admin/users")
public class AdminUserServlet extends BaseServlet {
    @Override protected void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException{
        request.setAttribute("pageTitle","MEMBER DIRECTORY");
        int page;try{page=Math.max(1,Integer.parseInt(request.getParameter("page")));}catch(RuntimeException e){page=1;}String query=request.getParameter("q");
        request.setAttribute("users",DAOFactory.getInstance().users().search(query,(page-1)*20,20));request.setAttribute("query",query==null?"":query);request.setAttribute("page",page);request.setAttribute("roles",Role.values());forward(request,response,"admin/users");
    }
    @Override protected void doPost(HttpServletRequest request,HttpServletResponse response)throws IOException{
        if(!requireCsrf(request,response))return;var dao=DAOFactory.getInstance().users();
        try{String action=request.getParameter("action");long id=request.getParameter("id")==null||request.getParameter("id").isBlank()?0:longParam(request,"id");
            if("delete".equals(action)){if(id==currentUser(request).getId())throw new IllegalArgumentException("You cannot delete the signed-in administrator.");if(!dao.delete(id))throw new IllegalArgumentException("User not found.");flash(request,"success","User deleted.");}
            else{String email=ValidationUtil.required(request.getParameter("email"),"Email",190).toLowerCase();if(!ValidationUtil.validEmail(email))throw new IllegalArgumentException("Enter a valid email address.");var duplicate=dao.findByEmail(email);if(duplicate.isPresent()&&duplicate.get().getId()!=id)throw new IllegalArgumentException("Email is already in use.");
                User user=id>0?dao.findById(id).orElseThrow(()->new IllegalArgumentException("User not found.")):new User();user.setName(ValidationUtil.required(request.getParameter("name"),"Name",100));user.setEmail(email);user.setRole(Role.valueOf(request.getParameter("role")));
                if(id==0){String password=request.getParameter("password");if(!ValidationUtil.validPassword(password))throw new IllegalArgumentException("Password must be 8-72 characters with uppercase, lowercase, and a number.");user.setPasswordHash(PasswordUtil.hash(password));user.setWeightKg(70);user.setHeightCm(170);dao.save(user);flash(request,"success","User created.");}
                else{String password=request.getParameter("password");if(password!=null&&!password.isBlank()){if(!ValidationUtil.validPassword(password))throw new IllegalArgumentException("Password must be 8-72 characters with uppercase, lowercase, and a number.");user.setPasswordHash(PasswordUtil.hash(password));}dao.update(user);flash(request,"success","User updated.");}}
        }catch(RuntimeException e){flash(request,"error",e.getMessage()==null?"User update failed.":e.getMessage());}redirect(request,response,"/admin/users");
    }
}