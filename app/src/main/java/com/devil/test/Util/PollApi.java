package com.devil.test.Util;

import android.app.Application;

public class PollApi extends Application {
    private String userId;

    private static PollApi instance;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public PollApi(){}
    public static PollApi getInstance(){
        if (instance==null){
            instance= new PollApi();
        }
        return instance;
    }
}
