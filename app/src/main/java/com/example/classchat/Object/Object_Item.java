package com.example.classchat.Object;

public class Object_Item {
    private String id;
    private String name;
    private int type;
    private String img_list_1;
    private String img_list_2;
    private float price;
    private String seller;
    private String comment_list;
    private String attribute_kv;

    public Object_Item(){}

    public Object_Item(String id, String name, int type, String img_list_1, String img_list_2, float price, String seller, String comment_list, String attribute_kv) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.img_list_1 = img_list_1;
        this.img_list_2 = img_list_2;
        this.price = price;
        this.seller = seller;
        this.comment_list = comment_list;
        this.attribute_kv = attribute_kv;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
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

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
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

}
