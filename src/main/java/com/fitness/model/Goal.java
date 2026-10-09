package com.fitness.model;

import java.time.LocalDate;

public class Goal {
    private long id, userId;
    private String goalType, status;
    private double targetValue, currentValue;
    private LocalDate startDate, endDate;
    public long getId(){return id;} public void setId(long v){id=v;}
    public long getUserId(){return userId;} public void setUserId(long v){userId=v;}
    public String getGoalType(){return goalType;} public void setGoalType(String v){goalType=v;}
    public double getTargetValue(){return targetValue;} public void setTargetValue(double v){targetValue=v;}
    public double getCurrentValue(){return currentValue;} public void setCurrentValue(double v){currentValue=v;}
    public int getProgressPercent(){return targetValue<=0?0:(int)Math.min(100,currentValue*100/targetValue);}
    public LocalDate getStartDate(){return startDate;} public void setStartDate(LocalDate v){startDate=v;}
    public LocalDate getEndDate(){return endDate;} public void setEndDate(LocalDate v){endDate=v;}
    public String getStatus(){return status;} public void setStatus(String v){status=v;}
}