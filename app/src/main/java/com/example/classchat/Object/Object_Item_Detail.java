package com.example.classchat.Object;

import java.util.List;

public class Object_Item_Detail {
    private List<String> paramList;
    private List<List<String>> rangeList;
    private Object_Item item;
    private List<Object_Stock> stockList;


    public Object_Item_Detail(){}

    public Object_Item_Detail(List<String> paramList, List<List<String>> rangeList, Object_Item item, List<Object_Stock> stockList) {
        this.paramList = paramList;
        this.rangeList = rangeList;
        this.item = item;
        this.stockList = stockList;

    }

    public List<String> getParamList() {
        return paramList;
    }

    public void setParamList(List<String> paramList) {
        this.paramList = paramList;
    }

    public List<List<String>> getRangeList() {
        return rangeList;
    }

    public void setRangeList(List<List<String>> rangeList) {
        this.rangeList = rangeList;
    }

    public Object_Item getItem() {
        return item;
    }

    public void setItem(Object_Item item) {
        this.item = item;
    }

    public List<Object_Stock> getStockList() {
        return stockList;
    }

    public void setStockList(List<Object_Stock> stockList) {
        this.stockList = stockList;
    }



}
