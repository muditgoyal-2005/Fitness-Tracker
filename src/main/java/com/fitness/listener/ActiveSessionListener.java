package com.fitness.listener;

import java.util.concurrent.atomic.AtomicInteger;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.*;

@WebListener
public class ActiveSessionListener implements HttpSessionListener {
    private static final AtomicInteger ACTIVE=new AtomicInteger();
    @Override public void sessionCreated(HttpSessionEvent event){event.getSession().getServletContext().setAttribute("activeUsers",ACTIVE.incrementAndGet());}
    @Override public void sessionDestroyed(HttpSessionEvent event){event.getSession().getServletContext().setAttribute("activeUsers",ACTIVE.updateAndGet(value->Math.max(0,value-1)));}
}