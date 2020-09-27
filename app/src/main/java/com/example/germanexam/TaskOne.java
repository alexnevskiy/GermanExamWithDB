package com.example.germanexam;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class TaskOne extends AppCompatActivity {

    long timeLeft = 90000;
    int counter = 0;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("LifeCycleActivity", "onCreate()");
        setContentView(R.layout.task1);
        final TextView timeRemaining = (TextView) findViewById(R.id.time_remaining);
        final ProgressBar timeline = (ProgressBar) findViewById(R.id.timeline);
        countDownTimer = new CountDownTimer(timeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                updateTimer();
                counter++;
                timeline.setProgress(counter);
            }

            private void updateTimer() {
                int minutes = (int) (timeLeft / 1000) / 60;
                int seconds = (int) (timeLeft / 1000) % 60;

                String timeLeftText = String.format(Locale.getDefault(), "-%02d:%02d", minutes, seconds);

                timeRemaining.setText(timeLeftText);
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("LifeCycleActivity", "onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("LifeCycleActivity", "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("LifeCycleActivity", "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("LifeCycleActivity", "onStop()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("LifeCycleActivity", "onRestart()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("LifeCycleActivity", "onDestroy()");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        Log.d("LifeCycleActivity", "onSaveInstanceState()");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("LifeCycleActivity", "onSaveInstanceState()");
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d("LifeCycleActivity", "onRestoreInstanceState()");
    }

    @Override
    public void onRestoreInstanceState(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        Log.d("LifeCycleActivity", "onRestoreInstanceState()");
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        Log.d("LifeCycleActivity", "setContentView()");
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        Log.d("LifeCycleActivity", "setContentView()");
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        Log.d("LifeCycleActivity", "setContentView()");
    }
}