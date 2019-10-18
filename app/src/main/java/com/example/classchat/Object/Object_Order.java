package com.example.classchat.Object;

public class Object_Order {
    //订单id
    private String order_id;
    //购买人id
    private String buyer_id;
    //货物信息
    private Object_Pre_Sale item;
    //收货地址详情
    private Object_Adress adress;
    //货物状态
    private int state = 0;
    //订单时间
    private String generatetime;
    //总价
    private float sumprice;

    public Object_Order(String order_id, String buyer_id, Object_Pre_Sale item, Object_Adress adress,  String generatetime, float sumprice) {
        this.order_id = order_id;
        this.buyer_id = buyer_id;
        this.item = item;
        this.adress = adress;
        this.state = 0;
        this.generatetime = generatetime;
        this.sumprice = sumprice;
    }

    public float getSumprice() {
        return sumprice;
    }

    public void setSumprice() {
        sumprice = item.getNum()*item.getPrice();
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getBuyer_id() {
        return buyer_id;
    }

    public void setBuyer_id(String buyer_id) {
        this.buyer_id = buyer_id;
    }

    public Object_Adress getAdress() {
        return adress;
    }

    public void setAdress(Object_Adress adress) {
        this.adress = adress;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getGeneratetime() {
        return generatetime;
    }

    public void setGeneratetime(String generatetime) {
        this.generatetime = generatetime;
    }



}
