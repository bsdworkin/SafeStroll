package com.bendworkin.safestroll;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;



public class MainActivity extends AppCompatActivity {


    //Changing the activity from Main Menu to Edit Contacts using an intent
    public void toEditContacts(View view){
        Intent intent = new Intent(getApplicationContext(), EditContacts.class);

        startActivity(intent);
    }

    //Changing the activity from Main Menu to EditAlarms using an intent
    public void toEditAlarms(View view){
        Intent intent2 = new Intent (getApplicationContext(), EditAlarms.class);

        startActivity(intent2);
    }

    //Changing the activity from Main Menu to Start Stroll using an intent
    public void toStartStroll(View view){
        Intent intent3 = new Intent(getApplicationContext(), ChooseAlarm.class);

        startActivity(intent3);
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Two lines below will remove the permananent data stored in the app on the device
        //SharedPreferences sharedPreferences = this.getSharedPreferences("com.bendworkin.safestroll", Context.MODE_PRIVATE);
        //sharedPreferences.edit().clear().apply();


    }
}
