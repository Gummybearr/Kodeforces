package com.example.map_pa;

import java.util.HashMap;
import java.util.Map;

public class RegisterUser {
    public String ID;
    public String pw;
    public String tier;
    public String rating;
    public String hasPicture;

    public RegisterUser(){

    }

    public RegisterUser(String ID, String pw, String tier, String rating, String hasPicture){
        this.ID = ID;
        this.pw = pw;
        this.tier = tier;
        this.rating = rating;
        this.hasPicture = hasPicture;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> register = new HashMap<>();
        register.put("ID", ID);
        register.put("pw", pw);
        register.put("tier", tier);
        register.put("rating", rating);
        register.put("hasPicture", hasPicture);
        return register;
    }
}
