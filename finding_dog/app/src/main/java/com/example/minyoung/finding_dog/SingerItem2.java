package com.example.minyoung.finding_dog;

import android.net.Uri;

public class SingerItem2 {
    String name;
    String feature;
    String location;
    String uid;

    Uri uri;

    public SingerItem2(String name, String location, String feature, Uri uri, String uid){
        this.name = name;
        this.location = location;
        this.feature = feature;
        this.uri = uri;
        this.uid = uid;
    }

    public String getFeature(){return feature;}
    public void setFeature(String feature){this.feature = feature;}
    public String getLocation(){return location;}
    public void setLocation(String location){this.location = location;}
    public String getName(){return name;}
    public void setName(String name){this.name = name;}

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri resId) {
        this.uri = resId;
    }

    public String getUid(){
        return this.uid;
    }
    public void setUid(String uid){
        this.uid = uid;
    }
}