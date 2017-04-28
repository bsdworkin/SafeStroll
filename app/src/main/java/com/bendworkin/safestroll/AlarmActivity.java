package com.bendworkin.safestroll;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Permission;
import java.util.ArrayList;


public class AlarmActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;
    SmsManager smsManager;
    SharedPreferences sharedPreferences;
    private TextView alarmNameText;
    private TextView alarmTimeText;
    private AlarmSettings alarmSettings; //new AlarmSettings(null, true, "Test Alarm", 10);//To test class
    //public String phoneNumber = "3312209405";//To test class
    private int startMins;
    private int startSecs;
    private Button start;
    private Button stop;
    private double latitude;
    private double longitude;
    private ArrayList<String> emailTemp;
    private String[] emails;
    private String checkAlarmName;
    public static boolean pwPref;

    private AlarmSettingsWriter writer = new AlarmSettingsWriter();
    private ArrayList<AlarmSettings> jsonAlarmSettingsList;

    AlertDialog safeCheckWindow;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0) {

            for (int i = 0; i < grantResults.length; i++) {

                if (requestCode == 1 && grantResults[i] == PackageManager.PERMISSION_GRANTED) {

                    //Checking to see if we asked for location permission
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        //listening to the users location
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                    }

                }

                //if(requestCode == 2 && grantResults[i] == PackageManager.PERMISSION_GRANTED){

                //Checking to see if we asked for location permission
                //    if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {

                //        Log.i("Permission", "PermissionGranted");
                //    }

                //}
            }
        }

    }


    //When the user wants to start specified alarm
    public void startAlarm(final View view) {

        start.setVisibility(View.INVISIBLE);

        new CountDownTimer(alarmSettings.getTimerPref() * 1000 + 100, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                updateTimer((int) millisUntilFinished / 1000, alarmTimeText);

            }

            @Override
            public void onFinish() {

                alarmTimeText.setText("0:00");
                AlertDialog.Builder safeCheck = new AlertDialog.Builder(AlarmActivity.this);
                safeCheck.setIcon(R.drawable.logo36);
                safeCheck.setTitle("SafeStroll Check");
                safeCheck.setMessage("Proceed with Safe Stroll?");
                safeCheck.setCancelable(false);
                safeCheck.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        startAlarm(view);

                    }
                });
                safeCheck.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(alarmSettings.getPasswordPref() == true){

                            Intent pw = new Intent(getApplicationContext(), LockScreen.class);
                            startActivity(pw);
                        }

                    }
                });

                //If the user picks no option and touches outside the dialog box

                //Resetting the visibility of start button
                start.setVisibility(View.VISIBLE);



                //Setting the timer to users preference
                if (startSecs == 0) {

                    alarmTimeText.setText(startMins + ":00");

                } else if (startSecs < 10) {

                    alarmTimeText.setText(startMins + ":0" + startSecs);

                } else {

                    alarmTimeText.setText(startMins + ":" + startSecs);

                }

                safeCheckWindow = safeCheck.create();
                safeCheckWindow.show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if(safeCheckWindow.isShowing()){
                            sendEmail();
                            safeCheckWindow.dismiss();
                        }


                        //Code to send email and and lat/long

                       

                    }
                }, 15000);


            }

        }.start();


    }


    public void toStopAlarm(View view) {

        pwPref = false;
        Intent password = new Intent(getApplicationContext(), LockScreen.class);

            //sharedPreferences.edit().remove("thisAlarmName").apply();

        startActivity(password);

    }

    public void sos(View view) {

        //Code to send email and lat/long
        sendEmail();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        sharedPreferences = this.getSharedPreferences("com.bendworkin.safestroll", Context.MODE_PRIVATE);

        //Loop to find correct alarm in file
        checkAlarmName = sharedPreferences.getString("thisAlarmName", "No Name");

        if (!checkAlarmName.equals("No name")) {
            try {
                jsonAlarmSettingsList = writer.fromJson();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if (jsonAlarmSettingsList != null) {
                for (AlarmSettings settings : jsonAlarmSettingsList) {
                    if (checkAlarmName.equals(settings.getAlarmName())) {
                        alarmSettings = settings;
                    }
                }
            }
        }

        pwPref = alarmSettings.getPasswordPref();
        try {
            emailTemp = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("emails", ObjectSerializer.serialize(new ArrayList<String>())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(int i=0; i < emailTemp.size(); i++){

            if(!emailTemp.get(i).contains("@")){

                emailTemp.remove(i);
            }
        }
        emails = emailTemp.toArray(new String[0]);

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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //If we dont have permission we need to ask for it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        } else {

            //We already have permission so we listen to the location
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }

        //Checking to see if we asked for sens sms permission
        //if(ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED){

        //If we dont have permission we need to ask for it
        //   ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 2);

        //}else {

        //We already have permission so get smsManager
        //   Log.i("Permission", "Alreadly Granted");
        //}

        //Assigning Variables to respective ids of the widgets
        alarmNameText = (TextView) findViewById(R.id.alarmNameActivity);
        alarmTimeText = (TextView) findViewById(R.id.timerActivity);
        start = (Button) findViewById(R.id.startButton);
        stop = (Button) findViewById(R.id.stopButton);

        alarmNameText.setText(alarmSettings.getAlarmName());
        int startTime = alarmSettings.getTimerPref();
        startMins = (int) startTime / 60;
        startSecs = startTime - startMins * 60;

        //Setting the timer to users preference
        if (startSecs == 0) {

            alarmTimeText.setText(startMins + ":00");

        } else if (startSecs < 10) {

            alarmTimeText.setText(startMins + ":0" + startSecs);

        } else {

            alarmTimeText.setText(startMins + ":" + startSecs);

        }

    }

    //Method called to update the timer after it is started
    public void updateTimer(int secsLeft, TextView timerView) {

        int mins = (int) secsLeft / 60;
        int secs = secsLeft - mins * 60;

        String secsString = Integer.toString(secs);

        if (secsString == "0") {

            secsString = "00";

        } else if (secs < 10) {

            secsString = "0" + secsString;

        }

        timerView.setText(Integer.toString(mins) + ":" + secsString);

    }

    private void sendEmail() {

        //Checking to see if we asked for location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //If we dont have permission we need to ask for it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        } else {
            //We already have permission so we listen to the location
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }

        Intent sos = new Intent(Intent.ACTION_SEND);
        sos.setType("message/rfc822");

        // pulls contact names from alarm settings
        ArrayList<SSContact> contacts = alarmSettings.getContacts();
        ArrayList<String> tempList = new ArrayList<>();
        for (SSContact contact : contacts) {
            tempList.add(contact.getName());
        }
        // assigns all emails/names to recipients
        String[] temp = tempList.toArray(new String[0]);
        sos.putExtra(Intent.EXTRA_EMAIL, temp);
        sos.putExtra(Intent.EXTRA_SUBJECT, "SOS Alert from: ");
        sos.putExtra(Intent.EXTRA_TEXT, "I haven't responded to my SafeStroll alarm. " +
                "\n Here is my location: https://www.google.com/maps?q=loc:" + latitude + "," + longitude + "&z=14" );

        try {
            startActivity(Intent.createChooser(sos, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(AlarmActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }

        //smsmanager = SMSManager.getDefault();
        //smsmanager.sendTextMessage(phoneNumber, null, "SafeStroll Test", null, null);

    }



}
