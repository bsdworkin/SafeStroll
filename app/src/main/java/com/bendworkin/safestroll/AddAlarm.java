package com.bendworkin.safestroll;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import android.util.Log;

public class AddAlarm extends AppCompatActivity {

    private TextView passwordTextView;
    private Switch passwordSwitch;
    private EditText alarmNameEditText;
    private boolean passwordPref;
    private int timerPref;
    private String alarmName;
    private int mins;
    private int secs;
    private ArrayList<SSContact> alarmContacts;
    public static ArrayList<AlarmSettings> alarms = new ArrayList<>();

    static ArrayList<SSContact> myContacts = new ArrayList<>();
    static SSContactAdapter adapter;

    private AlarmSettingsWriter writer = new AlarmSettingsWriter();

    SharedPreferences sharedPreferences;

    public void backToEditAlarms(View view){

        Intent intent = new Intent(getApplicationContext(), EditAlarms.class);
        startActivity(intent);

    }

    public void addNewAlarm(View view){

        alarmNameEditText = (EditText)findViewById(R.id.alarmNameEditText);
        String nullCheck = "";
        nullCheck = alarmNameEditText.getText().toString();

        if(nullCheck.matches("")){

            //Notifies user to input data
            Toast.makeText(this, "Please Enter An Alarm Name", Toast.LENGTH_LONG).show();
            return;
        }else {

            alarmName = alarmNameEditText.getText().toString();

            timerPref = mins * 60 + secs;
            Alarm newAlarm = new Alarm(alarmName, Integer.toString(timerPref));
            EditAlarms.listViewAlarms.add(newAlarm);
            EditAlarms.alarmAdapter.notifyDataSetChanged();

            sharedPreferences = this.getSharedPreferences("com.bendworkin.safestroll", Context.MODE_PRIVATE);

            try {

                ArrayList<String> alarmNamesView = new ArrayList<>();
                ArrayList<String> alarmTimesView = new ArrayList<>();

                //Advanced for loop to restore and add the new contact to the lists
                for(Alarm thisAlarm: EditAlarms.listViewAlarms){

                    alarmNamesView.add(thisAlarm.thisName);
                    alarmTimesView.add(thisAlarm.thisTime);

                }

                sharedPreferences.edit().putString("alarmNames", ObjectSerializer.serialize(alarmNamesView)).apply();
                sharedPreferences.edit().putString("alarmTimes", ObjectSerializer.serialize(alarmTimesView)).apply();

            } catch (IOException e) {

                e.printStackTrace();

            }


            AlarmSettings alarm = new AlarmSettings(alarmContacts, passwordPref, alarmName ,timerPref);
            alarms.add(alarm);
            writer.toJson(alarms);
            ArrayList<AlarmSettings> list = new ArrayList<>();
            try {
                list = writer.fromJson();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                for (AlarmSettings a : alarms)
                    Log.i("message", a.getAlarmName());

                for (AlarmSettings a : list)
                    Log.i("message", a.getAlarmName());

            }

        }

        Toast.makeText(this, "Alarm Added!", Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_alarms);



        SeekBar timerSeekBar = (SeekBar) findViewById(R.id.timerSeekBar);
        final SeekBar secSeekBar = (SeekBar)findViewById(R.id.secSeekBar);
        final TextView timerTextView = (TextView) findViewById(R.id.timerTextView);


        //Setting max time allowed to be 60mins
        timerSeekBar.setMax(59);
        timerSeekBar.setProgress(1);

        secSeekBar.setMax(59);
        secSeekBar.setProgress(30);

        //Allowing the seek bar to represent its number by a text view
        timerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                mins = progress;

                if(secSeekBar.getProgress() == 0){

                    timerTextView.setText(Integer.toString(mins) + ":00");

                }else if (secSeekBar.getProgress() < 10 && secSeekBar.getProgress()!= 0) {

                    timerTextView.setText(Integer.toString(mins) + ":0" + Integer.toString(secSeekBar.getProgress()));

                }else{

                    timerTextView.setText(Integer.toString(mins) + ":" + Integer.toString(secSeekBar.getProgress()));

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        secSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                secs = progress;

                if(secs == 0 ) {

                    timerTextView.setText(Integer.toString(mins) + ":" + Integer.toString(secs) + "0");

                }else if (secs < 10) {

                    timerTextView.setText(Integer.toString(mins) + ":0" + Integer.toString(secs));

                }else{

                    timerTextView.setText(Integer.toString(mins) + ":" + Integer.toString(secs));
                }



            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        final TextView passwordTextView = (TextView)findViewById(R.id.passwordTextView);
        final Switch passwordSwitch = (Switch)findViewById(R.id.passwordSwitch);

        passwordSwitch.setChecked(true);
        passwordPref = true;

        //tells the user what the switch will do for the password preference
        passwordSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(passwordSwitch.isChecked()){
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
            passwordPref = true;
        }
        else {
            passwordTextView.setText("Password is asked ONLY when the alarm ends");
            passwordPref = false;
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

