package com.fitness.servlet;

import java.io.IOException;
import com.fitness.dao.DAOFactory;
import com.fitness.model.User;
import com.fitness.util.PasswordUtil;
import com.fitness.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/profile")
public class ProfileServlet extends BaseServlet {
    @Override protected void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException{request.setAttribute("pageTitle","PROFILE SETTINGS");request.setAttribute("memberSince",currentUser(request).getCreatedAt()==null?"New profile":currentUser(request).getCreatedAt().toLocalDate());forward(request,response,"user/profile");}
    @Override protected void doPost(HttpServletRequest request,HttpServletResponse response)throws IOException{
        if(!requireCsrf(request,response))return;User user=currentUser(request);var users=DAOFactory.getInstance().users();
        try{String name=ValidationUtil.required(request.getParameter("name"),"Name",100);String email=ValidationUtil.required(request.getParameter("email"),"Email",190).toLowerCase();if(!ValidationUtil.validEmail(email))throw new IllegalArgumentException("Enter a valid email address.");
            var existing=users.findByEmail(email);if(existing.isPresent()&&existing.get().getId()!=user.getId())throw new IllegalArgumentException("That email is already in use.");
            String newPassword=request.getParameter("newPassword");boolean changePassword=newPassword!=null&&!newPassword.isBlank();
            if(changePassword){if(!PasswordUtil.verify(request.getParameter("currentPassword"),user.getPasswordHash()))throw new IllegalArgumentException("Current password is incorrect.");if(!ValidationUtil.validPassword(newPassword))throw new IllegalArgumentException("New password must be 8-72 characters with uppercase, lowercase, and a number.");user.setPasswordHash(PasswordUtil.hash(newPassword));}
            user.setName(name);user.setEmail(email);if(!users.updateProfile(user,changePassword))throw new IllegalArgumentException("Profile could not be updated.");request.getSession().setAttribute("user",user);flash(request,"success","Profile updated successfully.");
        }catch(RuntimeException e){flash(request,"error",e.getMessage()==null?"Profile update failed.":e.getMessage());}redirect(request,response,"/profile");
    }
}