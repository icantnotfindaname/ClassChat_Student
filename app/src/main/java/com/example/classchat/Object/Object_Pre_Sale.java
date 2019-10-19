package com.example.classchat.Object;

import java.util.List;

public class Object_Pre_Sale {

    public Object_Pre_Sale(String itemName, String itemId, List<String> paramList, int num, float price, String imgurl) {
        this.itemName = itemName;
        this.itemId = itemId;
        this.paramList = paramList;
        this.num = num;
        this.price = price;
        this.imgurl = imgurl;
    }

    public Object_Pre_Sale(){}

    //商品名
    private String itemName;

    //商品Id
    private String itemId;

    //被选中的参数列表 严格按照顺序
    private List<String> paramList;

    //要购买的数量
    private int num;

    //单价
    private float price;
    
    //给购物车每一条的一个缩略图
    private String imgurl;

    //是否选中
    private boolean isChildSelected;

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public List<String> getParamList() {
        return paramList;
    }

    public void setParamList(List<String> paramList) {
        this.paramList = paramList;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public boolean isChildSelected() {
        return isChildSelected;
    }

    public void setChildSelected(boolean childSelected) {
        isChildSelected = childSelected;
    }
}
