package com.example.minyoung.finding_dog;

import java.io.Serializable;

public class SingerItem implements Serializable {
    String manufacturer;
    String product;
    int price;
    int resId;


    public SingerItem(String manufacturer, String product, int price, int resId){
        this.manufacturer = manufacturer;
        this.product = product;
        this.price = price;
        this.resId = resId;
    }

    public String getmanufacturer(){return manufacturer;}
    public void setgetmanufacturer(String manufacturer){this.manufacturer = manufacturer;}
    public String getproduct(){return product;}
    public void setproduct(String product){this.product = product;}
    public int getprice(){return price;}
    public void setprice(int price){this.price = price;}

    public int getResId(){return resId;}
    public void setResId(int resId){this.resId = resId;}
}
