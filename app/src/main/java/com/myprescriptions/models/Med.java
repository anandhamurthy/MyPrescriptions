package com.myprescriptions.models;

import java.io.Serializable;

public class Med implements Serializable {

    String name, mor, nig, eve, aft, quantity;
    public Med() {
    }

    public Med(String name, String mor, String nig, String eve, String aft, String quantity) {
        this.name = name;
        this.mor = mor;
        this.nig = nig;
        this.eve = eve;
        this.aft = aft;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMor() {
        return mor;
    }

    public void setMor(String mor) {
        this.mor = mor;
    }

    public String getNig() {
        return nig;
    }

    public void setNig(String nig) {
        this.nig = nig;
    }

    public String getEve() {
        return eve;
    }

    public void setEve(String eve) {
        this.eve = eve;
    }

    public String getAft() {
        return aft;
    }

    public void setAft(String aft) {
        this.aft = aft;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
