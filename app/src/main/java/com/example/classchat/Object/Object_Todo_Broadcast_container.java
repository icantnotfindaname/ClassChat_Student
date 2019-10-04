package com.example.classchat.Object;

import java.io.Serializable;

public class Object_Todo_Broadcast_container implements Serializable {

    /**
     * detail_time: 具体的时间
     * time: 用户填的
     * title：标题
     * id：通知的标识符
     */

    private static final long serialVersionUID = 1L;

    private String title; // 通知栏显示的名称
    private String detail; // 具体内容
    private int id;  // 标识符
    private int hour, minute; // 设置的时间
    public static int ID = 0;
    private int week;  // 设置的周数
    private long calculated_time; // 经过计算之后的时间

    public Object_Todo_Broadcast_container(){}

    public Object_Todo_Broadcast_container(String title, long calculated_time, int week, int hour, int minute, String details, int id) {
        this.title = title;
        this.calculated_time = calculated_time;
        this.hour = hour;
        this.minute = minute;
        this.week = week;
        this.detail = details ;
        this.id = id;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public long getCalculated_time() {
        return calculated_time;
    }

    public void setCalculated_time(long calculated_time) {
        this.calculated_time = calculated_time;
    }
}



