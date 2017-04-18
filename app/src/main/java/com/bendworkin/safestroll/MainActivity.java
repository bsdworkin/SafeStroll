package com.bendworkin.safestroll;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;

import android.util.Log;

public class MainActivity extends AppCompatActivity {

    public SharedPreferences sharedPreferences;

    //Changing the activity from Main Menu to Edit Contacts using an intent
    public void toEditContacts(View view){
        Intent intent = new Intent(getApplicationContext(), EditContacts.class);

        startActivity(intent);
    }

    //Changing the activity from Main Menu to Choose Alarms using an intent
    public void toEditAlarms(View view){
        Intent intent2 = new Intent (getApplicationContext(), EditAlarms.class);

        startActivity(intent2);
    }


    public void toAlarmActivity(View view){
        Intent intent3 = new Intent (getApplicationContext(), LockScreen.class);
        startActivity(intent3);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Two lines below will remove the permananent data stored in the app on the device
        sharedPreferences = this.getSharedPreferences("com.bendworkin.safestroll", Context.MODE_PRIVATE);
        //sharedPreferences.edit().clear().apply();

        // this verifies read and write permissions at start of app
        AlarmSettingsWriter.verifyStoragePermissions(this);
        Log.i("message", "verified permissions");
        if (sharedPreferences.getInt("password", -1) == -1) {
            Intent passwordIntent = new Intent(getApplicationContext(), LockScreen.class);
            startActivity(passwordIntent);
        }
    }
}
