package com.fitness.util;

import java.security.SecureRandom;
import java.util.Base64;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public final class WebUtil {
    private static final SecureRandom RANDOM=new SecureRandom();
    private WebUtil(){ }
    public static String csrfToken(HttpSession session){String token=(String)session.getAttribute("csrfToken");if(token==null){byte[] bytes=new byte[32];RANDOM.nextBytes(bytes);token=Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);session.setAttribute("csrfToken",token);}return token;}
    public static boolean validCsrf(HttpServletRequest request){String expected=(String)request.getSession().getAttribute("csrfToken");String actual=request.getParameter("_csrf");return expected!=null&&expected.equals(actual);}
}