package com.bendworkin.safestroll;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;

public class EditAlarms extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    static ArrayList<Alarm> listViewAlarms = new ArrayList<>();
    static AlarmAdapter alarmAdapter;

    public void toAddNewAlarm(View view){

        Intent intent = new Intent(getApplicationContext(), AddAlarm.class);
        startActivity(intent);

    }

    public void toMainM(View view){

        Intent intent2 = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent2);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_alarm);

        ListView alarmsListView = (ListView) findViewById(R.id.alarmsListView);

        sharedPreferences = this.getSharedPreferences("com.bendworkin.safestroll", Context.MODE_PRIVATE);

        ArrayList<String> listViewAlarmNames = new ArrayList<>();
        ArrayList<String> listViewAlarmTimes = new ArrayList<>();

        listViewAlarmNames.clear();
        listViewAlarmTimes.clear();
        listViewAlarms.clear();

        //Attempts to grab stored data from SharedPreferences
        try {

            //Have to use object serializer to unflatten the data from shared preferences
            listViewAlarmNames = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("alarmNames", ObjectSerializer.serialize(new ArrayList<String>())));

            listViewAlarmTimes = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("alarmTimes", ObjectSerializer.serialize(new ArrayList<String>())));

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

            listViewAlarms.add(new Alarm("Push me to delete", ""));

        }

        alarmAdapter = new AlarmAdapter(this, listViewAlarms);

        alarmsListView.setAdapter(alarmAdapter);

        alarmsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                //Alert dialog to ensure user wants to delete contact
                AlertDialog.Builder show1 = new AlertDialog.Builder(EditAlarms.this);
                        show1.setIcon(R.drawable.logo36);
                        show1.setTitle("Delete Contact?");
                        show1.setMessage("Are you sure you would like to delete this contact from you SSContact List?");

                        //Allows user to delete contact
                        show1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //To delete contact from listview in EditContact
                                Alarm toRemove = alarmAdapter.getItem(position);
                                alarmAdapter.remove(toRemove);
                                alarmAdapter.notifyDataSetChanged();

                                //To change the permanent data stored and restore it
                                try {

                                    ArrayList<String> listViewAlarmNames = new ArrayList<>();
                                    ArrayList<String> listViewAlarmTimes = new ArrayList<>();

                                    //Advanced for loop to restore and add the new contact to the lists
                                    for (Alarm alarm : EditAlarms.listViewAlarms) {

                                        listViewAlarmNames.add(alarm.thisName);
                                        listViewAlarmTimes.add(alarm.thisTime);

                                    }


                                    //Adds the lists to the shared prefs to be stored permanently
                                    sharedPreferences.edit().putString("alarmNames", ObjectSerializer.serialize(listViewAlarmNames)).apply();
                                    sharedPreferences.edit().putString("alarmTimes", ObjectSerializer.serialize(listViewAlarmTimes)).apply();

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }


                        });
                        show1.setNegativeButton("No", null);
                        show1.show();

            }
        });
        

    }
}
