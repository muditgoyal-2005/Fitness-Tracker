package com.fitness.model;

import java.time.LocalDateTime;

public class Activity {
    private long id;
    private Long userId;
    private String action, details, userName;
    private LocalDateTime createdAt;
    public long getId(){return id;} public void setId(long v){id=v;}
    public Long getUserId(){return userId;} public void setUserId(Long v){userId=v;}
    public String getAction(){return action;} public void setAction(String v){action=v;}
    public String getDetails(){return details;} public void setDetails(String v){details=v;}
    public String getUserName(){return userName;} public void setUserName(String v){userName=v;}
    public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime v){createdAt=v;}
}