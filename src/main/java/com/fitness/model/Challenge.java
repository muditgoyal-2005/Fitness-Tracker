package com.fitness.model;

import java.time.LocalDate;

public class Challenge {
    private long id;
    private String title, description, status;
    private LocalDate startDate, endDate;
    private double targetValue, progress;
    private int participantCount;
    private boolean joined;
    public long getId(){return id;} public void setId(long v){id=v;}
    public String getTitle(){return title;} public void setTitle(String v){title=v;}
    public String getDescription(){return description;} public void setDescription(String v){description=v;}
    public String getStatus(){return status;} public void setStatus(String v){status=v;}
    public LocalDate getStartDate(){return startDate;} public void setStartDate(LocalDate v){startDate=v;}
    public LocalDate getEndDate(){return endDate;} public void setEndDate(LocalDate v){endDate=v;}
    public double getTargetValue(){return targetValue;} public void setTargetValue(double v){targetValue=v;}
    public double getProgress(){return progress;} public void setProgress(double v){progress=v;}
    public int getParticipantCount(){return participantCount;} public void setParticipantCount(int v){participantCount=v;}
    public boolean isJoined(){return joined;} public void setJoined(boolean v){joined=v;}
    public int getProgressPercent(){return targetValue <= 0 ? 0 : (int)Math.min(100, progress * 100 / targetValue);}
}