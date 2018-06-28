package com.example.minyoung.finding_dog.Fragment;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

import static android.os.Build.ID;

class Dog {
    String species;
    String location;
    String feature;
    public Dog(String species, String location, String feature) {
        this.species = species;
        this.location = location;
        this.feature = feature;
    }

    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("species", species);
        result.put("location", location);
        result.put("feature", feature);
        return result;
    }
}
