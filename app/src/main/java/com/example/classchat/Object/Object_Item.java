package com.example.classchat.Object;

import android.support.annotation.NonNull;

public class Object_Item {
    private String id;
    private String name;
    private String type;
    private String img_list_1;
    private String img_list_2;
    private String price;
    private String seller;
    private String comment_list;
    private String attribute_kv;
    private String count;
    private String params;

    public Object_Item(){}

    public Object_Item(String id, String name, String type, String img_list_1, String img_list_2, String price, String seller, String comment_list, String attribute_kv, String count, String params) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.img_list_1 = img_list_1;
        this.img_list_2 = img_list_2;
        this.price = price;
        this.seller = seller;
        this.comment_list = comment_list;
        this.attribute_kv = attribute_kv;
        this.count = count;
        this.params = params;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImg_list_1() {
        return img_list_1;
    }

    public void setImg_list_1(String img_list_1) {
        this.img_list_1 = img_list_1;
    }

    public String getImg_list_2() {
        return img_list_2;
    }

    public void setImg_list_2(String img_list_2) {
        this.img_list_2 = img_list_2;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public String getComment_list() {
        return comment_list;
    }

    public void setComment_list(String comment_list) {
        this.comment_list = comment_list;
    }

    public String getAttribute_kv() {
        return attribute_kv;
    }

    public void setAttribute_kv(String attribute_kv) {
        this.attribute_kv = attribute_kv;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    @NonNull
    @Override
    public String toString() {
        return id + name + type;
    }
}
