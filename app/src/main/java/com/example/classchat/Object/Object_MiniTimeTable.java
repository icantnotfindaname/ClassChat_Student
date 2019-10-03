package com.example.classchat.Object;

public class Object_MiniTimeTable {
	private int startnum;
	private int endnum;
	private int week;
	private String name="";


	@Override
	public String toString() {
		return "Object_MiniTimeTable [startnum=" + startnum
				+ ", endnum=" + endnum + ", week=" + week + ", name=" + name
				 + "]";
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

	public Object_MiniTimeTable() {
		// TODO Auto-generated constructor stub
	}

	public Object_MiniTimeTable(int startnum, int endnum, int week, String name) {
		super();
		this.startnum = startnum;
		this.endnum = endnum;
		this.week = week;
		this.name = name;
	}

}
