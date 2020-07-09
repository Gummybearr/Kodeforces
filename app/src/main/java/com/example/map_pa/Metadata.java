package com.example.map_pa;

import java.util.HashMap;
import java.util.Map;

public class Metadata {
    public String username;
    public String Content;
    public String tier;
    public String imageCheck;

    public Metadata(){

    }

    public Metadata(String username, String Content, String tier, String imageCheck){
        this.username = username;
        this.Content = Content;
        this.tier = tier;
        this.imageCheck = imageCheck;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> register = new HashMap<>();
        register.put("username", username);
        register.put("Content", Content);
        register.put("tier", tier);
        register.put("imageCheck", imageCheck);
        return register;
    }

}
