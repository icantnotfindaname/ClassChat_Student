package com.example.classchat.Object;

import android.support.annotation.NonNull;

import com.googlecode.mp4parser.srt.SrtParser;

public class Object_TodoList {

    private String userID, todoTitle, content, detailTime;
    private int dayChosen, weekChosen, timeSlot;
    private Boolean isClock;

    public Object_TodoList(){
        // TODO Auto-generated constructor stub
    }

    public Object_TodoList(String userID, String todoTitle, String content, int dayChosen, int weekChosen, int timeSlot, String detailTime, boolean isClock){
        this.userID = userID;
        this.todoTitle = todoTitle;
        this.detailTime = detailTime;
        this.content = content;
        this.detailTime = detailTime;
        this.dayChosen = dayChosen;
        this.weekChosen = weekChosen;
        this.timeSlot = timeSlot;
        this.isClock = isClock;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getTodoTitle() {
        return todoTitle;
    }

    public void setTodoTitle(String todoTitle) {
        this.todoTitle = todoTitle;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDetailTime() {
        return detailTime;
    }

    public void setDetailTime(String detailTime) {
        this.detailTime = detailTime;
    }

    public int getDayChosen() {
        return dayChosen;
    }

    public void setDayChosen(int dayChosen) {
        this.dayChosen = dayChosen;
    }

    public int getWeekChosen() {
        return weekChosen;
    }

    public void setWeekChosen(int weekChosen) {
        this.weekChosen = weekChosen;
    }

    public int getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(int timeSlot) {
        this.timeSlot = timeSlot;
    }

    public Boolean isClock() {
        return isClock;
    }

    public void setClock(Boolean isClock) {
        this.isClock = isClock;
    }

    @NonNull
    @Override
    public String toString() {
        return todoTitle + content+isClock+detailTime+dayChosen+weekChosen+timeSlot;

    }
}
