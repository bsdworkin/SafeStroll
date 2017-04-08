package com.bendworkin.safestroll;

import java.io.Serializable;

/**
 * Created by bendworkin on 4/6/17.
 */

public class Alarm implements Serializable {

    public String thisName;
    public int thisTime;

    public Alarm(String thisName, int thisTime){

        this.thisName = thisName;
        this.thisTime = thisTime;
    }

    public int getThisTime() {
        return thisTime;
    }

    public String getThisName() {
        return thisName;
    }

    public void setThisName(String thisName) {
        this.thisName = thisName;
    }

    public void setThisTime(int thisTime) {
        this.thisTime = thisTime;
    }

    @Override
    public String toString() {
        return this.thisName + this.thisTime;
    }
}
