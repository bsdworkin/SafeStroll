package com.bendworkin.safestroll;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

public class PickAlarm extends AppCompatActivity {

    public static ArrayList<AlarmSettings> alarms = new ArrayList<>();

    public void toCancelAlarm(View view){

        Intent cancelAlarm = new Intent(getApplicationContext(), MainActivity.class);

        startActivity(cancelAlarm);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_alarm);

        //Grab things stored in Gson and populate an array list with info
    }
}
