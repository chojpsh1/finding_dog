package com.example.minyoung.finding_dog;

import android.net.Uri;

public class SingerItem2 {
    String name;
    String feature;
    String location;
    Uri resId;

    public SingerItem2(String name, String location, String feature, Uri resId){
        this.name = name;
        this.location = location;
        this.feature = feature;
        this.resId = resId;
    }

    public String getFeature(){return feature;}
    public void setFeature(String feature){this.feature = feature;}
    public Uri getResId(){return resId;}
    public void setResId(Uri resId){this.resId = resId;}
    public String getLocation(){return location;}
    public void setLocation(String location){this.location = location;}
    public String getName(){return name;}
    public void setName(String name){this.name = name;}
}
