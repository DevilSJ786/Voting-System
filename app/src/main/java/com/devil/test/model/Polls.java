package com.devil.test.model;

import com.google.firebase.Timestamp;

public class Polls {
    private String userId;
    private String title;
    private String choice1;
    private String choice2;
    private String choice3;
    private int vote1;
    private int vote2;
    private int vote3;
    private Timestamp timeAdded;

    public Polls() {
    }

    public Polls(String userId, String title, String choice1, String choice2, String choice3, int vote1, int vote2, int vote3, Timestamp timeAdded) {
        this.userId = userId;
        this.title = title;
        this.choice1 = choice1;
        this.choice2 = choice2;
        this.choice3 = choice3;
        this.vote1 = vote1;
        this.vote2 = vote2;
        this.vote3 = vote3;
        this.timeAdded = timeAdded;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getChoice1() {
        return choice1;
    }

    public void setChoice1(String choice1) {
        this.choice1 = choice1;
    }

    public String getChoice2() {
        return choice2;
    }

    public void setChoice2(String choice2) {
        this.choice2 = choice2;
    }

    public String getChoice3() {
        return choice3;
    }

    public void setChoice3(String choice3) {
        this.choice3 = choice3;
    }

    public int getVote1() {
        return vote1;
    }

    public void setVote1(int vote1) {
        this.vote1 = vote1;
    }

    public int getVote2() {
        return vote2;
    }

    public void setVote2(int vote2) {
        this.vote2 = vote2;
    }

    public int getVote3() {
        return vote3;
    }

    public void setVote3(int vote3) {
        this.vote3 = vote3;
    }

    public Timestamp getTimeAdded() {
        return timeAdded;
    }

    public void setTimeAdded(Timestamp timeAdded) {
        this.timeAdded = timeAdded;
    }
}
