package com.fitness.util;

import java.util.regex.Pattern;

public final class ValidationUtil {
    private static final Pattern EMAIL=Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PASSWORD=Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,72}$");
    private ValidationUtil(){ }
    public static boolean validEmail(String value){return value!=null&&EMAIL.matcher(value.trim()).matches();}
    public static boolean validPassword(String value){return value!=null&&PASSWORD.matcher(value).matches();}
    public static String required(String value,String label,int max){String clean=value==null?"":value.trim();if(clean.isEmpty()||clean.length()>max)throw new IllegalArgumentException(label+" is required and must be under "+max+" characters.");return clean;}
    public static int positiveInt(String value,String label,int max){try{int number=Integer.parseInt(value);if(number<1||number>max)throw new NumberFormatException();return number;}catch(NumberFormatException e){throw new IllegalArgumentException(label+" must be between 1 and "+max+".");}}
}