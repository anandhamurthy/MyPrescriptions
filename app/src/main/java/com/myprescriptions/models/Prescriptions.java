package com.myprescriptions.models;

import com.myprescriptions.models.Med;

import java.io.Serializable;
import java.util.ArrayList;

public class Prescriptions implements Serializable {

    String name, email, user_id, details, profile_image, timestamp;
    ArrayList<Med> medicine;

    public Prescriptions() {
    }

    public Prescriptions(String name, String email, String user_id, String details, String profile_image, String timestamp, ArrayList<Med> medicine) {
        this.name = name;
        this.email = email;
        this.user_id = user_id;
        this.details = details;
        this.profile_image = profile_image;
        this.timestamp = timestamp;
        this.medicine = medicine;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public ArrayList<Med> getMedicine() {
        return medicine;
    }

    public void setMedicine(ArrayList<Med> medicine) {
        this.medicine = medicine;
    }
}
