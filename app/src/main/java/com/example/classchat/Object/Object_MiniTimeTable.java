package com.example.classchat.Object;

import java.util.ArrayList;
import java.util.List;

public class Object_MiniTimeTable {
	private int startnum;
	private int endnum;
	private int week;
	private String name = "";//当总节课人数
	private List<String>nameList = new ArrayList<>();
	private List<Integer>numList = new ArrayList<>();
	private String comparisonID;

	public Object_MiniTimeTable() {
		// TODO Auto-generated constructor stub
	}

	public Object_MiniTimeTable(int startnum, int endnum, int week, String name, List<String> nameList, List<Integer> numList, String comparisonID) {
		this.startnum = startnum;
		this.endnum = endnum;
		this.week = week;
		this.name = name;
		this.nameList = nameList;
		this.numList = numList;
		this.comparisonID = comparisonID;
	}

	public int getStartnum() {
		return startnum;
	}

	public int getEndnum() {
		return endnum;
	}

	public int getWeek() {
		return week;
	}

	public String getName() {
		return name;
	}

	public void setStartnum(int startnum) {
		this.startnum = startnum;
	}

	public void setEndnum(int endnum) {
		this.endnum = endnum;
	}

	public void setWeek(int week) {
		this.week = week;
	}

	public void setName(String name) {
		this.name = name;
	}


	public List<String> getNameList() {
		return nameList;
	}

	public void setNameList(List<String> nameList) {
		this.nameList = nameList;
	}

	public List<Integer> getNumList() {
		return numList;
	}

	public void setNumList(List<Integer> numList) {
		this.numList = numList;
	}


	public String getComparisonID() {
		return comparisonID;
	}

	public void setComparisonID(String comparisonID) {
		this.comparisonID = comparisonID;
	}

	@Override
	public String toString() {
		return "Object_MiniTimeTable [startnum=" + startnum
				+ ", endnum=" + endnum + ", week=" + week + ", name=" + name
				+ "]";
	}


}
