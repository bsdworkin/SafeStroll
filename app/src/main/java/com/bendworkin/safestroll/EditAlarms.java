package com.bendworkin.safestroll;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class EditAlarms extends AppCompatActivity {

    private SeekBar timeSeekBar;
    private TextView passwordTextView;
    private Switch passwordSwitch;
    private EditText alarmNameEditText;
    private boolean passwordPref;
    private int timerPref;
    private String alarmName;
    private int mins;
    private int secs;


    static ArrayList<SSContact> myContacts = new ArrayList<>();
    static ArrayList<AlarmSettings> myAlarms = new ArrayList<>();
    static ArrayList<Integer> alarmTimes = new ArrayList<>();
    static ArrayList<Boolean> alarmPasswordPrefs = new ArrayList<>();
    static ArrayList<String> alarmNames = new ArrayList<>();
    static SSContactAdapter adapter;

    SharedPreferences sharedPreferences;
    private ArrayList<SSContact> alarmContacts;




    public void backToMM(View view){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);

        //Grabs the shared prefs of the app
        sharedPreferences = this.getSharedPreferences("com.bendworkin.safestroll",Context.MODE_PRIVATE);

        try{

            ArrayList<String> alarmContactNames = new ArrayList<>();
            ArrayList<String> alarmPhoneNumbers = new ArrayList<>();


            for(SSContact alarmContact : alarmContacts){

                alarmContactNames.add(alarmContact.name);
                alarmPhoneNumbers.add(alarmContact.phoneNumber);

            }

            sharedPreferences.edit().putString("alarmNames", ObjectSerializer.serialize(alarmContactNames)).apply();
            sharedPreferences.edit().putString("alarmPhoneNumbers", ObjectSerializer.serialize(alarmPhoneNumbers)).apply();

            timerPref = mins * 60 + secs;
            alarmTimes.add(timerPref);
            sharedPreferences.edit().putInt("alarmTimes", Integer.parseInt(ObjectSerializer.serialize(alarmTimes))).apply();



            alarmNameEditText = (EditText)findViewById(R.id.alarmNameEditText);
            alarmName = alarmNameEditText.getText().toString();
            alarmNames.add(alarmName);
            sharedPreferences.edit().putString("alarmTitles", ObjectSerializer.serialize(alarmNames)).apply();

            alarmPasswordPrefs.add(passwordPref);
            sharedPreferences.edit().putBoolean("alarmPasswordPrefs", Boolean.parseBoolean(ObjectSerializer.serialize(alarmPasswordPrefs))).apply();

            AlarmSettings alarm = new AlarmSettings(alarmContacts, passwordPref, alarmName, timerPref);
            myAlarms.add(alarm);


        }catch (Exception e){

            e.printStackTrace();

        }

        Toast.makeText(this, "Alarm Added!", Toast.LENGTH_LONG).show();
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_alarms);



        SeekBar timerSeekBar = (SeekBar) findViewById(R.id.timerSeekBar);
        final TextView timerTextView = (TextView) findViewById(R.id.timerTextView);


        //Setting max time allowed to be 30mins
        timerSeekBar.setMax(1800);
        timerSeekBar.setProgress(30);

        //Allowing the seek bar to represent its number by a text view
        timerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                mins = (int) progress / 60;
                secs = progress - mins * 60;


                String secondString = Integer.toString(secs);

                if(secondString == "0"){

                    secondString = "00";
                }

                timerTextView.setText(Integer.toString(mins) + ":" + secondString);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        final TextView passwordTextView = (TextView)findViewById(R.id.passwordTextView);
        Switch passwordSwitch = (Switch)findViewById(R.id.passwordSwitch);

        passwordSwitch.setChecked(true);

        //tells the user what the switch will do for the password preference
        passwordSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    passwordTextView.setText("Password is asked after each alarm fire");
                    passwordPref = true;
                }else{
                    passwordTextView.setText("Password is asked ONLY when the alarm ends");
                    passwordPref = false;
                }

            }
        });

        if(passwordSwitch.isChecked()){
            passwordTextView.setText("Password is asked after each alarm fire");
        }
        else {
            passwordTextView.setText("Password is asked ONLY when the alarm ends");
        }

        ListView contactListView  = (ListView)findViewById(R.id.contactListView);

        sharedPreferences = this.getSharedPreferences("com.bendworkin.safestroll", Context.MODE_PRIVATE);

        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> phoneNumbers = new ArrayList<>();

        names.clear();
        phoneNumbers.clear();
        myContacts.clear();

        try {

            names = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("names", ObjectSerializer.serialize(new ArrayList<String>())));

            phoneNumbers = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("phoneNumbers", ObjectSerializer.serialize(new ArrayList<String>())));



        } catch (IOException e) {
            e.printStackTrace();
        }

        if (names.size() > 0 && phoneNumbers.size() > 0 ) {

            if (names.size() == phoneNumbers.size()) {

                for (int i = 0; i < names.size(); i++) {

                    myContacts.add(new SSContact(names.get(i), phoneNumbers.get(i)));

                }

            }
        } else {

            myContacts.add(new SSContact("Name", "Phone Number"));

        }

        adapter = new SSContactAdapter(this, myContacts);
        contactListView.setAdapter(adapter);

        alarmContacts = new ArrayList<SSContact>();


        //On click to be able to add contacts to alarmSettings
        contactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                if(alarmContacts.isEmpty()){

                    alarmContacts.add(myContacts.get(position));
                    view.setBackgroundColor(Color.CYAN);

                }
                else if(alarmContacts.contains(myContacts.get(position))){

                    view.setBackgroundColor(Color.WHITE);
                    alarmContacts.remove(myContacts.get(position));

                }
                else{

                    alarmContacts.add(myContacts.get(position));
                    view.setBackgroundColor(Color.CYAN);

                }

            }
        });

    }


}

