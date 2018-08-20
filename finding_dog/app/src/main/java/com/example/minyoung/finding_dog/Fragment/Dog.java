package com.example.minyoung.finding_dog.Fragment;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

import static android.os.Build.ID;

class Dog {
    String species;
    String location;
    String feature;
    String loseState;

    public Dog(String species, String location, String feature, String loseState) {
        this.species = species;
        this.location = location;
        this.feature = feature;
        this.loseState = loseState;
    }

    public void setLoseState(String loseState) {
        loseState = loseState;
    }

    public String getLoseState() {
        return loseState;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getSpecies() {
        return species;
    }

    public String getLocation() {
        return location;
    }

    public String getFeature() {
        return feature;
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
