package com.bendworkin.safestroll;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AlarmActivity extends AppCompatActivity {

    private TextView alarmNameText;
    private TextView alarmTimeText;
    private AlarmSettings alarmSettings = new AlarmSettings(null, true, "Test Alarm", 30);


    public void startAlarm(View view){

        new CountDownTimer(alarmSettings.getTimerPref() * 1000 + 100, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                updateTimer((int)millisUntilFinished / 1000, alarmTimeText);

            }

            @Override
            public void onFinish() {

                alarmTimeText.setText("0:00");

            }
        }.start();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        alarmNameText = (TextView) findViewById(R.id.alarmNameActivity);
        alarmTimeText = (TextView) findViewById(R.id.timerActivity);

        alarmTimeText.setText(alarmSettings.getAlarmName());

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
