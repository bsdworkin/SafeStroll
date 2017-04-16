package com.bendworkin.safestroll;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AlarmActivity extends AppCompatActivity {

    private TextView alarmNameText;
    private TextView alarmTimeText;
    private AlarmSettings alarmSettings = new AlarmSettings(null, true, "Test Alarm", 10);
    public String phoneNumber = "8475023699";

    AlertDialog safeCheckWindow;


    public SmsManager smsManager;


    public void startAlarm(final View view){


        new CountDownTimer(alarmSettings.getTimerPref() * 1000 + 100, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                updateTimer((int)millisUntilFinished / 1000, alarmTimeText);

            }

            @Override
            public void onFinish() {

                alarmTimeText.setText("0:00");
                AlertDialog.Builder safeCheck = new AlertDialog.Builder(AlarmActivity.this);
                    safeCheck.setIcon(R.drawable.logo36);
                    safeCheck.setTitle("SafeStroll Check");
                    safeCheck.setMessage("Proceed with Safe Stroll?");
                    safeCheck.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            startAlarm(view);

                        }
                    });
                    safeCheck.setNegativeButton("No", null);


                safeCheckWindow = safeCheck.create();
                safeCheckWindow.show();

            }

        }.start();

    }


    public void toStopAlarm(View view){

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);


        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);


        alarmNameText = (TextView) findViewById(R.id.alarmNameActivity);
        alarmTimeText = (TextView) findViewById(R.id.timerActivity);

        alarmNameText.setText(alarmSettings.getAlarmName());
        int startTime = alarmSettings.getTimerPref();
        int startMins = (int) startTime / 60;
        int startSecs = startTime - startMins * 60;

        if(startSecs == 0){

            alarmTimeText.setText(startMins + ":00");

        }else if(startSecs <10){

            alarmTimeText.setText(startMins + ":0" + startSecs);

        }else{

            alarmTimeText.setText(startMins + ":" + startSecs);

        }

    }

    public void updateTimer(int secsLeft, TextView timerView){

        int mins = (int) secsLeft / 60;
        int secs = secsLeft - mins * 60;

        String secsString = Integer.toString(secs);

        if(secsString == "0"){

            secsString = "00";

        }else if(secs <10){

            secsString = "0" + secsString;

        }

        timerView.setText(Integer.toString(mins)+ ":" + secsString);

    }



}
