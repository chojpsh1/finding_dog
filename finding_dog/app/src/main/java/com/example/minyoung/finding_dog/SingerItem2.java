package com.example.minyoung.finding_dog;

public class SingerItem2 {
    String name;
    String feature;
    String location;
    int resId;

    public SingerItem2(String name, String location, String feature, int resId){
        this.name = name;
        this.location = location;
        this.feature = feature;
        this.resId = resId;
    }

    public String getFeature(){return feature;}
    public void setFeature(String feature){this.feature = feature;}
    public int getResId(){return resId;}
    public void setResId(int resId){this.resId = resId;}
    public String getLocation(){return location;}
    public void setLocation(String location){this.location = location;}
    public String getName(){return name;}
    public void setName(String name){this.name = name;}
}
