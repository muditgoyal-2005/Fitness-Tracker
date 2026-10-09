package com.fitness.model;

import java.time.LocalDateTime;

public class FitnessContent {
    private long id, userId;
    private Long reviewedBy;
    private String title, body, status, authorName;
    private LocalDateTime submittedAt;
    public long getId(){return id;} public void setId(long v){id=v;}
    public long getUserId(){return userId;} public void setUserId(long v){userId=v;}
    public Long getReviewedBy(){return reviewedBy;} public void setReviewedBy(Long v){reviewedBy=v;}
    public String getTitle(){return title;} public void setTitle(String v){title=v;}
    public String getBody(){return body;} public void setBody(String v){body=v;}
    public String getStatus(){return status;} public void setStatus(String v){status=v;}
    public String getAuthorName(){return authorName;} public void setAuthorName(String v){authorName=v;}
    public LocalDateTime getSubmittedAt(){return submittedAt;} public void setSubmittedAt(LocalDateTime v){submittedAt=v;}
}