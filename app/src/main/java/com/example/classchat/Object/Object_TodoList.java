package com.example.classchat.Object;

import android.support.annotation.NonNull;

import com.googlecode.mp4parser.srt.SrtParser;

import java.util.List;

public class Object_TodoList {

    private String userID;
    private String todoTitle;
    private String content;

    private String todoItemID;
    private String detailTime;
    private int dayChosen, timeSlot;
    private List<Integer> weekChosen;
    private Boolean isClock;

    public Object_TodoList(){
        // TODO Auto-generated constructor stub
    }

    public Object_TodoList(String userID, String todoTitle, List<Integer> weekChosen, int dayChosen, int timeSlot, String detailTime, boolean isClock, String content, String todoItemID){
        this.userID = userID;
        this.todoTitle = todoTitle;
        this.detailTime = detailTime;
        this.content = content;
        this.detailTime = detailTime;
        this.dayChosen = dayChosen;
        this.weekChosen = weekChosen;
        this.timeSlot = timeSlot;
        this.isClock = isClock;
        this.todoItemID = todoItemID;
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

    public List<Integer> getWeekChosen() {
        return weekChosen;
    }

    public void setWeekChosen(List<Integer> weekChosen) {
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


    public String getTodoItemID() {
        return todoItemID;
    }

    public void setTodoItemID(String todoItemID) {
        this.todoItemID = todoItemID;
    }
    @NonNull
    @Override
    public String toString() {
        return todoTitle + content + isClock + detailTime + dayChosen + weekChosen + timeSlot;

    }
}
