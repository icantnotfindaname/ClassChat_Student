package com.example.classchat.Object;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class Object_Comparison implements Serializable {

    private String comparisonID;
    private String comparisonTitle;
    private String comparisonData;
    private int comparisonWeek;
    private int comparisonPerson;
    private String comparisonMember;

    public Object_Comparison(){}

    public Object_Comparison(String comparisonID, String comparisonTitle, String comparisonData, int comparisonWeek, int comparisonPerson, String comparisonMember) {
        this.comparisonID = comparisonID;
        this.comparisonTitle = comparisonTitle;
        this.comparisonData = comparisonData;
        this.comparisonWeek = comparisonWeek;
        this.comparisonPerson = comparisonPerson;
        this.comparisonMember = comparisonMember;
    }

    public String getComparisonID() {
        return comparisonID;
    }

    public void setComparisonID(String comparisonID) {
        this.comparisonID = comparisonID;
    }

    public String getComparisonTitle() {
        return comparisonTitle;
    }

    public void setComparisonTitle(String comparisonTitle) {
        this.comparisonTitle = comparisonTitle;
    }

    public String getComparisonData() {
        return comparisonData;
    }

    public void setComparisonData(String comparisonData) {
        this.comparisonData = comparisonData;
    }

    public int getComparisonWeek() {
        return comparisonWeek;
    }

    public void setComparisonWeek(int comparisonWeek) {
        this.comparisonWeek = comparisonWeek;
    }

    public int getComparisonPerson() {
        return comparisonPerson;
    }

    public void setComparisonPerson(int comparisonPerson) {
        this.comparisonPerson = comparisonPerson;
    }

    public String getComparisonMember() {
        return comparisonMember;
    }

    public void setComparisonMember(String comparisonMember) {
        this.comparisonMember = comparisonMember;
    }

    @Override
    public String toString() {
        return "Object_Comparison [comparisonID=" + comparisonID + ", comparisonTitle=" + comparisonTitle + ", comparisonWeek=" + comparisonWeek + ", comparisonPerson=" + comparisonPerson + ", comparisonData=" + comparisonData + ", comparisonMember=" + comparisonMember + "]";
    }
}
