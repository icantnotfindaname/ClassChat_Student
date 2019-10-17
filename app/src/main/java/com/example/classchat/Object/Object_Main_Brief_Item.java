package com.example.classchat.Object;

public class Object_Main_Brief_Item {

    private String id, name, image; // ,seller;
    private Double price;

    public Object_Main_Brief_Item(){}

    public Object_Main_Brief_Item(String id, String name, String image, Double price) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.price = price;
//        this.seller = seller;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

//    public String getSeller() {
//        return seller;
//    }

//    public void setSeller(String seller) {
//        this.seller = seller;
//    }


}
