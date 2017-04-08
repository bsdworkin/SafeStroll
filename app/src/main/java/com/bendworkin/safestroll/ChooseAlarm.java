package com.bendworkin.safestroll;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class ChooseAlarm extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    private ArrayList<Alarm> listViewAlarms = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_alarm);

        ListView alarmsListView = (ListView) findViewById(R.id.alarmsListView);

        sharedPreferences = this.getSharedPreferences("com.bendworkin.safestroll", Context.MODE_PRIVATE);

        ArrayList<String> listViewAlarmNames = new ArrayList<>();
        ArrayList<Integer> listViewAlarmTimes = new ArrayList<>();

        listViewAlarmNames.clear();
        listViewAlarmTimes.clear();
        listViewAlarms.clear();

        //Attempts to grab stored data from SharedPreferences
        try {

            //Have to use object serializer to unflatten the data from shared preferences
            listViewAlarmNames = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("alarmTitles", ObjectSerializer.serialize(new ArrayList<String>())));

            listViewAlarmTimes = (ArrayList<Integer>) ObjectSerializer.deserialize(String.valueOf(sharedPreferences.getInt("alarmTimes", Integer.parseInt(ObjectSerializer.serialize(new ArrayList<String>())))));

        } catch (IOException e) {
            e.printStackTrace();
        }


        //Making sure there is data to add to array list
        if (listViewAlarmNames.size() > 0 && listViewAlarmTimes.size() > 0 ) {

            if (listViewAlarmNames.size() == listViewAlarmTimes.size()) {

                for (int i = 0; i < listViewAlarmNames.size(); i++) {

                    //creating a SSContact object for each contact stored
                    listViewAlarms.add(new Alarm(listViewAlarmNames.get(i), listViewAlarmTimes.get(i)));

                }


            }
        } else {

            listViewAlarms.add(new Alarm("Alarm Name", 0));

        }

        AlarmAdapter alarmAdapter = new AlarmAdapter(this, listViewAlarms);

        alarmsListView.setAdapter(alarmAdapter);
        

    }
}
