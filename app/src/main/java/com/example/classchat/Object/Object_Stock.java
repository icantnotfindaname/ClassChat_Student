package com.example.classchat.Object;

public class Object_Stock {
    private String id;
    private int count;
    private String param1;
    private String param2;
    private String param3;
    private float price;

    public Object_Stock(){}

    public Object_Stock(String id, int count, String param1, String param2, String param3, float price) {
        this.id = id;
        this.count = count;
        this.param1 = param1;
        this.param2 = param2;
        this.param3 = param3;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getParam1() {
        return param1;
    }

    public void setParam1(String param1) {
        this.param1 = param1;
    }

    public String getParam2() {
        return param2;
    }

    public void setParam2(String param2) {
        this.param2 = param2;
    }

    public String getParam3() {
        return param3;
    }

    public void setParam3(String param3) {
        this.param3 = param3;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

}
