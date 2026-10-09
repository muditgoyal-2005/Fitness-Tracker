package com.fitness.servlet;

import java.io.IOException;
import java.util.*;
import com.google.gson.Gson;
import com.fitness.dao.DAOFactory;
import com.fitness.enums.Role;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/api/activity")
public class ActivityApiServlet extends BaseServlet {
    private final Gson gson=new Gson();
    @Override protected void doGet(HttpServletRequest request,HttpServletResponse response)throws IOException{
        if(currentUser(request).getRole()!=Role.ADMIN){response.sendError(HttpServletResponse.SC_FORBIDDEN);return;}
        response.setContentType("application/json;charset=UTF-8");List<Map<String,Object>> items=new ArrayList<>();
        for(var event:DAOFactory.getInstance().activity().latest(20)){Map<String,Object> row=new LinkedHashMap<>();row.put("id",event.getId());row.put("user",event.getUserName()==null?"System":event.getUserName());row.put("action",event.getAction());row.put("details",event.getDetails());row.put("createdAt",event.getCreatedAt().toString());items.add(row);}
        response.getWriter().write(gson.toJson(items));
    }
}