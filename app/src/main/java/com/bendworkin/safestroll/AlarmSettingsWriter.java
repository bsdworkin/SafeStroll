package com.bendworkin.safestroll;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.io.File;

import android.Manifest;
import android.app.Activity;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.content.pm.PackageManager;
import android.util.Log;
import android.os.Environment;

/**
 * @author Brandon Manke
 */
public class AlarmSettingsWriter {
    private Gson gson = new Gson();
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public AlarmSettingsWriter() {
        super();
    }

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public void toJson(ArrayList<AlarmSettings> list) {
        File root = new File(Environment.getExternalStorageDirectory().toString());
        File json = new File(root, "alarm-settings.json");
        Log.i("message", Environment.getExternalStorageDirectory().toString());
        if (!json.exists()) {
            try { json.createNewFile(); }
            catch (IOException e) { e.printStackTrace(); }
        }

        try (FileWriter writer = new FileWriter(json)) {
            gson.toJson(list, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("message", root.listFiles().toString());
    }

    public ArrayList<AlarmSettings> fromJson() throws FileNotFoundException {
        File root = new File(Environment.getExternalStorageDirectory().toString());
        File json = new File(root, "alarm-settings.json");
        if (!json.exists()) {
            throw new FileNotFoundException("File not found");
        }
        ArrayList<AlarmSettings> list = new ArrayList<>();
        AlarmSettings[] aSettings = null;
        try {
            JsonReader reader = new JsonReader(new FileReader(json));
            aSettings = gson.fromJson(reader, AlarmSettings[].class);
            for (AlarmSettings a : aSettings) {
                list.add(a);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * this is purely to write and read from the json file
     * then prints the contact of the file after it is read and serialized back to an object
     */
    public void testWriter() {
        SSContact ss = new SSContact("name", "profile");
        Log.i("message", "contact: " + ss.toString());
        ArrayList<SSContact> ssList = new ArrayList<>();
        ssList.add(ss);
        ssList.add(ss);
        ssList.add(ss);
        AlarmSettings settings = new AlarmSettings(ssList, false, "alarm", 123);
        Log.i("message", "contact: " + settings.toString());
        ArrayList<AlarmSettings> aList = new ArrayList<>();
        aList.add(settings);
        aList.add(settings);
        toJson(aList);
        ArrayList<AlarmSettings> readList = new ArrayList<>();
        try {
            readList = fromJson();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        for (AlarmSettings a : readList) {
            Log.i("message", a.getContacts().toString());
        }
    }
}
