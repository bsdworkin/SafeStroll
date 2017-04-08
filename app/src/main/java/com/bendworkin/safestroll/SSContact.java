package com.bendworkin.safestroll;

import java.io.Serializable;

/**
 * Created by bendworkin on 3/27/17.
 */

public class SSContact implements Serializable{
    public String name;
    public String phoneNumber;

    public SSContact (String name, String phoneNumber){
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name + this.phoneNumber;
    }
}
