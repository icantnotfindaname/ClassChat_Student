package com.example.classchat.Object;

import com.googlecode.mp4parser.srt.SrtParser;

public class Object_TodoList {

    private String id;
    private String title;
    private int classtime;
    private String todotime;
    private String todoplace;
    private String details;
    private Boolean isClock;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getClasstime() {
        return classtime;
    }

    public void setClasstime(int classtime) {
        this.classtime = classtime;
    }

    public String getTodotime() {
        return todotime;
    }

    public void setTodotime(String todotime) {
        this.todotime = todotime;
    }

    public String getTodoplace() {
        return todotime;
    }

    public void setTodoplace(String todoplace) {
        this.todoplace = todoplace;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Boolean getClock() {
        return isClock;
    }

    public void setClock(Boolean clock) {
        isClock = clock;
    }


    public Object_TodoList(){
        // TODO Auto-generated constructor stub
    }

    public Object_TodoList(String id, String title, int classtime, String todotime, String todoplace, String details, boolean isClock){
        this.id = id;
        this.title = title;
        this.classtime = classtime;
        this.todotime=todotime;
        this.todoplace=todoplace;
        this.details=details;
        this.isClock=isClock;
    }

}
