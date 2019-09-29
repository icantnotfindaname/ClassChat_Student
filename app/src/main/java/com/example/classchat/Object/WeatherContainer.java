package com.example.classchat.Object;

public class WeatherContainer {
    String wea;
    String high;
    String low;
    String date;
    String wea_img;

    public WeatherContainer(){}
    public WeatherContainer(String wea,
            String high,
            String low,
            String date,
            String wea_img){
        setDate(date);
        setWea_img((wea_img));
        setWea(wea);
        setLow(low);
        setHigh(high);
    }




    public String getWea() {
        return wea;
    }

    public void setWea(String wea) {
        this.wea = wea;
    }

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWea_img() {
        return wea_img;
    }

    public void setWea_img(String wea_img) {
        this.wea_img = wea_img;
    }

}
