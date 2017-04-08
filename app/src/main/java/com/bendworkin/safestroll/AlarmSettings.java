package com.bendworkin.safestroll;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by bendworkin on 4/4/17.
 */

public class AlarmSettings {

    private ArrayList<SSContact> contacts;
    private boolean passwordPref;
    private String alarmName;
    private int timerPref;
    private SSContact ssContact;

    public AlarmSettings(ArrayList<SSContact>contacts, boolean passwordPref, String alarmName, int timerPref){
        this.contacts = contacts;
        this.passwordPref = passwordPref;
        this.alarmName = alarmName;
        this.timerPref = timerPref;
    }


    public ArrayList<SSContact> getContacts(){return contacts;}

    public String getAlarmName(){return alarmName;}

    public boolean getPasswordPref(){return passwordPref;}

    public int getTimerPref(){return timerPref;}

    public ArrayList<SSContact> setContacts(ArrayList<SSContact> contacts, int index){return this.contacts;}

    public boolean setPasswordPref(boolean passwordPref){return this.passwordPref;}

    public int setTimerPref(int timerPref){return this.timerPref;}

    public String setAlarmName(String alarmName){return this.alarmName;}

}

