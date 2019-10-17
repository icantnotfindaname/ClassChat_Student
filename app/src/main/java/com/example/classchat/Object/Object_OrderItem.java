package com.example.classchat.Object;

import java.util.List;

public class Object_OrderItem extends Object_ItemShoppingCart {

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

    //订单状态
    private int state;


}
