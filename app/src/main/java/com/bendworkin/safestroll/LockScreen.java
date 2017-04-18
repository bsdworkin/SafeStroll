package com.bendworkin.safestroll;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.SharedPreferencesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.andrognito.patternlockview.utils.ResourceUtils;

import java.util.List;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Lock Screen Activity
 *
 * TODO: make layout pretty
 * in order to run this, we need to create an intent of this class - i.e. LockScreen.class as param
 * then start the activity.
 *
 * Stores password to shared preferences.
 */
public class LockScreen extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    private static PatternLockView lock;
    private static int password = -1;
    public TextView passwordTxt;
    private TextView invalidPwTxt;

    private PatternLockViewListener lockListener = new PatternLockViewListener() {
        @Override
        public void onStarted() {
            Log.d(getClass().getName(), "Pattern drawing started");
        }

        @Override
        public void onProgress(List<PatternLockView.Dot> progressPattern) {
            Log.d(getClass().getName(), "Pattern progress: " +
                    PatternLockUtils.patternToString(lock, progressPattern));
        }

        @Override
        public void onComplete(List<PatternLockView.Dot> pattern) {
            Log.d(getClass().getName(), "Pattern complete: " +
                    PatternLockUtils.patternToString(lock, pattern) + "current pw: " + password);
            int input = Integer.parseInt(PatternLockUtils.patternToString(lock, pattern));
            if (password == -1) {
                Log.d(getClass().getName(), "password set");
                password = input;
                sharedPreferences.edit().putInt("password", password).apply();
                Intent setPW = new Intent(getApplicationContext(), MainActivity.class);

                startActivity(setPW);

            } else {

                if (password == input) {
                    //This is called in alarm activity

                    Intent correctPWIntent = new Intent(getApplicationContext(), AlarmActivity.class);

                    startActivity(correctPWIntent);
                    Log.d(getClass().getName(), "Correct Password!");

                } else {
                    passwordTxt.setText("Wrong! Please Try Again!");
                    lock.clearPattern();
                }
            }

        }

        @Override
        public void onCleared() {
            Log.d(getClass().getName(), "Pattern has been cleared");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);
        passwordTxt = (TextView) findViewById(R.id.textView5);
        passwordTxt.setVisibility(View.VISIBLE);
        sharedPreferences = this.getSharedPreferences("com.bendworkin.safestroll", Context.MODE_PRIVATE);
        password = sharedPreferences.getInt("password", -1);
        if (lock == null && password == -1) {
            passwordTxt.setText("Set a new SafeStroll password");
            initializeLockSettings();
        } else {
            passwordTxt.setText("Enter your SafeStroll password");
            initializeLockSettings();
        }



    }

    private void initializeLockSettings() {
        lock = (PatternLockView) findViewById(R.id.pattern_lock_view);
        lock.setDotCount(3);
        lock.setDotNormalSize((int) ResourceUtils.getDimensionInPx(this, R.dimen.pattern_lock_dot_size));
        lock.setDotSelectedSize((int) ResourceUtils.getDimensionInPx(this, R.dimen.pattern_lock_dot_selected_size));
        lock.setPathWidth((int) ResourceUtils.getDimensionInPx(this, R.dimen.pattern_lock_path_width));
        lock.setAspectRatioEnabled(true);
        lock.setAspectRatio(PatternLockView.AspectRatio.ASPECT_RATIO_HEIGHT_BIAS);
        lock.setViewMode(PatternLockView.PatternViewMode.CORRECT);
        lock.setDotAnimationDuration(150);
        lock.setPathEndAnimationDuration(100);
        lock.setCorrectStateColor(ResourceUtils.getColor(this, R.color.pomegranate));
        lock.setInStealthMode(false);
        lock.setTactileFeedbackEnabled(true);
        lock.setInputEnabled(true);
        lock.addPatternLockListener(lockListener);
    }
}
