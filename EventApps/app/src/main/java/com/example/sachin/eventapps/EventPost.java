package com.example.sachin.eventapps;

/**
 * Created by Sachin on 2018-04-03.
 */

public class EventPost {
    String interested;
    String university;
    String email;


    public EventPost(){

    }
    public EventPost(String interested, String university, String email) {
        this.interested = interested;
        this.university = university;
        this.email = email;
    }

    public String getInterested() {
        return interested;
    }

    public void setInterested(String interested) {
        this.interested = interested;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
