package com.bendworkin.safestroll;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import android.util.Log;

public class MainActivity extends AppCompatActivity {


    //Changing the activity from Main Menu to Edit Contacts using an intent
    public void toEditContacts(View view){
        // When you click edit contacts this should run the tests
        Log.i("message", "test");
        AlarmSettingsWriter writer = new AlarmSettingsWriter();
        writer.testWriter();


        Intent intent = new Intent(getApplicationContext(), EditContacts.class);

        startActivity(intent);
    }

    //Changing the activity from Main Menu to Choose Alarms using an intent
    public void toEditAlarms(View view){
        Intent intent2 = new Intent (getApplicationContext(), ChooseAlarm.class);

        startActivity(intent2);
    }






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // this verifies read and write permissions at start of app
        AlarmSettingsWriter.verifyStoragePermissions(this);
        Log.i("message", "verified permissions");
        //Two lines below will remove the permananent data stored in the app on the device
        //SharedPreferences sharedPreferences = this.getSharedPreferences("com.bendworkin.safestroll", Context.MODE_PRIVATE);
        //sharedPreferences.edit().clear().apply();


    }
}
