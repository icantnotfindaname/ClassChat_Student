package com.example.classchat.Object;

public class Object_Main_Brief_Item_PriceSort extends Object_Main_Brief_Item implements Comparable{

    public Object_Main_Brief_Item_PriceSort(String id, String name, String image, Double price){
        super(id, name, image, price);
    }

    @Override
    public int compareTo(Object o) {
        Object_Main_Brief_Item c =(Object_Main_Brief_Item_PriceSort) o;
        if(this.getPrice() < c.getPrice())
            return -1;
        else if(this.getPrice() > c.getPrice())
            return 1;
        else if(this.getPrice() == c.getPrice())
            return 0;
        else
            return 2;
    }
}
