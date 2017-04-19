package com.bendworkin.safestroll;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class PickAlarm extends AppCompatActivity {

    public static ArrayList<AlarmSettings> pickAlarmList = new ArrayList<>();
    private AlarmSettingsWriter writer = new AlarmSettingsWriter();
    public static ArrayAdapter pickAlarm;
    private ArrayList<String> emails;
    SharedPreferences sharedPreferences;
    AlarmSettings alarmSettings;
    Alarm alarm;



    public void toCancelAlarm(View view){

        Intent cancelAlarm = new Intent(getApplicationContext(), MainActivity.class);

        startActivity(cancelAlarm);

    }

    public void startThisAlarm(View view){


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_alarm);

        sharedPreferences = this.getSharedPreferences("com.bendworkin.safestroll", Context.MODE_PRIVATE);

        sharedPreferences.edit().remove("thisAlarmName").apply();

        //Grab things stored in Gson and populate an array list with info
        try {
            pickAlarmList = writer.fromJson();
        } catch (Exception e) {
            e.printStackTrace();
        }

        final ListView pickAlarmListView = (ListView)findViewById(R.id.pickAlarmView);
        final ArrayList<AlarmSettings> alarmHolder = new ArrayList<>();
        ArrayList<String> alarmNameView = new ArrayList<>();

        if(pickAlarmList.size() > 0 ) {

            for (AlarmSettings thisAlarm : pickAlarmList) {

                alarmNameView.add(thisAlarm.getAlarmName());
                alarmSettings = new AlarmSettings(thisAlarm.getContacts(), thisAlarm.getPasswordPref(), thisAlarm.getAlarmName()
                , thisAlarm.getTimerPref());
                alarmHolder.add(alarmSettings);


            }
        } else {

            Toast.makeText(this, "No Alarms To Start Please Make One", Toast.LENGTH_LONG).show();

        }

        if (!alarmNameView.isEmpty()){

            pickAlarm = new ArrayAdapter(this, android.R.layout.simple_list_item_1, alarmNameView);
            pickAlarmListView.setAdapter(pickAlarm);
            pickAlarmListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String name = (String) pickAlarm.getItem(position);

                    sharedPreferences.edit().putString("thisAlarmName", name).apply();

                    Intent start = new Intent(getApplicationContext(), AlarmActivity.class);
                    startActivity(start);


                }
            });

        }

    }
}
