package com.bogdanenache.Code_Sharing_Platform.entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public class EmptyClass {
    private String code;
    private String date;
    private long time;
    private int views;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

}
