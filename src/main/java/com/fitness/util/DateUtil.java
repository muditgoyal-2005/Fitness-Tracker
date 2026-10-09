package com.fitness.util;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

public final class DateUtil {
    private DateUtil(){ }
    public static LocalDate startOfWeek(LocalDate date){return date.with(java.time.DayOfWeek.MONDAY);}
    public static LocalDate endOfWeek(LocalDate date){return date.with(java.time.DayOfWeek.SUNDAY);}
    public static LocalDate startOfMonth(LocalDate date){return date.with(TemporalAdjusters.firstDayOfMonth());}
    public static LocalDate endOfMonth(LocalDate date){return date.with(TemporalAdjusters.lastDayOfMonth());}
}