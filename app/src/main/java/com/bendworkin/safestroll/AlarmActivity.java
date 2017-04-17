package com.bendworkin.safestroll;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AlarmActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;
    private TextView alarmNameText;
    private TextView alarmTimeText;
    private AlarmSettings alarmSettings = new AlarmSettings(null, true, "Test Alarm", 10);//To test class
    public String phoneNumber = "8475023699";//To test class

    AlertDialog safeCheckWindow;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            //Checking to see if we asked for location permission
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){

                //listening to the users location
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            }

        }
    }

    public SmsManager smsManager;

    //When the user wants to start specified alarm
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

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                Log.i("Location", location.toString());

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        //Checking to see if we asked for location permission
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){

            //If we dont have permission we need to ask for it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }else {

            //We already have permission so we listen to the location
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }

        alarmNameText = (TextView) findViewById(R.id.alarmNameActivity);
        alarmTimeText = (TextView) findViewById(R.id.timerActivity);

        alarmNameText.setText(alarmSettings.getAlarmName());
        int startTime = alarmSettings.getTimerPref();
        int startMins = (int) startTime / 60;
        int startSecs = startTime - startMins * 60;

        //Setting the timer to users preference
        if(startSecs == 0){

            alarmTimeText.setText(startMins + ":00");

        }else if(startSecs <10){

            alarmTimeText.setText(startMins + ":0" + startSecs);

        }else{

            alarmTimeText.setText(startMins + ":" + startSecs);

        }

    }

    //Method called to update the timer after it is started 
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
