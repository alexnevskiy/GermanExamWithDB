package com.example.germanexam;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.Locale;

import static com.example.germanexam.constants.Constants.*;

public class TaskThree extends AppCompatActivity {

    long timeLeft = TASK3_TIME;
    int counter = 0;
    CountDownTimer countDownTimer;

    private String fileName = null;
    private boolean isWorking = false;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task3);
        final TextView timeRemaining = findViewById(R.id.time_remaining);
        final ProgressBar timeline = findViewById(R.id.timeline);
        Button photoButton1 = findViewById(R.id.task3_photo1_button);
        Button photoButton2 = findViewById(R.id.task3_photo2_button);
        Button photoButton3 = findViewById(R.id.task3_photo3_button);

        TextView task3QuestionsView = findViewById(R.id.task3_questions);
        ImageView task3ImageView1 = findViewById(R.id.task3_photo1);
        ImageView task3ImageView2 = findViewById(R.id.task3_photo2);
        ImageView task3ImageView3 = findViewById(R.id.task3_photo3);

        Intent myIntent = getIntent();
        final String task3Questions = myIntent.getStringExtra("questions");
        final String task3ImagePath1 = myIntent.getStringExtra("image1");
        final String task3ImagePath2 = myIntent.getStringExtra("image2");
        final String task3ImagePath3 = myIntent.getStringExtra("image3");

        File task3Image1 = new File(task3ImagePath1);
        File task3Image2 = new File(task3ImagePath2);
        File task3Image3 = new File(task3ImagePath3);

        task3QuestionsView.setText(task3Questions);
        task3ImageView1.setImageURI(Uri.fromFile(task3Image1));
        task3ImageView2.setImageURI(Uri.fromFile(task3Image2));
        task3ImageView3.setImageURI(Uri.fromFile(task3Image3));

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
                Intent intent = new Intent(TaskThree.this, Ready.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.putExtra("task", 3);
                intent.putExtra("answer", true);
                intent.putExtra("photos", "all");
                intent.putExtra("questions", task3Questions);
                intent.putExtra("image1", task3ImagePath1);
                intent.putExtra("image2", task3ImagePath2);
                intent.putExtra("image3", task3ImagePath3);
                startActivity(intent);
                isWorking = false;
                countDownTimer.cancel();
            }
        }.start();

        isWorking = true;

        photoButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskThree.this, TaskThreePhoto.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.putExtra("timeLeft", timeLeft);
                intent.putExtra("counter", counter);
                intent.putExtra("questions", task3Questions);
                intent.putExtra("image", task3ImagePath1);
                intent.putExtra("photo", 1);
                startActivity(intent);
                isWorking = false;
                countDownTimer.cancel();
            }
        });

        photoButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskThree.this, TaskThreePhoto.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.putExtra("timeLeft", timeLeft);
                intent.putExtra("counter", counter);
                intent.putExtra("questions", task3Questions);
                intent.putExtra("image", task3ImagePath2);
                intent.putExtra("photo", 2);
                startActivity(intent);
                isWorking = false;
                countDownTimer.cancel();
            }
        });

        photoButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskThree.this, TaskThreePhoto.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.putExtra("timeLeft", timeLeft);
                intent.putExtra("counter", counter);
                intent.putExtra("questions", task3Questions);
                intent.putExtra("image", task3ImagePath3);
                intent.putExtra("photo", 3);
                startActivity(intent);
                isWorking = false;
                countDownTimer.cancel();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isWorking) {
            countDownTimer.cancel();

            deleteFiles();

            sharedPreferences = getSharedPreferences("StudentData", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(RESTART, true);
            editor.apply();
        }
    }

    private void loadData(String task) {
        sharedPreferences = getSharedPreferences("StudentData", MODE_PRIVATE);
        fileName = sharedPreferences.getString(task, "");
    }

    private  void deleteFiles() {
        loadData(TASK1);
        File file1 = new File(fileName);
        boolean deleted1 = file1.delete();
        Log.i("TaskThree", "Audio1 is deleting: " + deleted1);

        loadData(TASK2);
        File file2 = new File(fileName);
        boolean deleted2 = file2.delete();
        Log.i("TaskThree", "Audio2 is deleting: " + deleted2);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_window_title);
        builder.setNegativeButton(R.string.menu, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(TaskThree.this, Menu.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                countDownTimer.cancel();
                deleteFiles();
                isWorking = false;
            }
        });
        builder.setNeutralButton(R.string.desktop, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                countDownTimer.cancel();
                deleteFiles();
                isWorking = false;
                finishAffinity();
            }
        });
        builder.setPositiveButton(R.string.variants_menu, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(TaskThree.this, Variants.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                countDownTimer.cancel();
                deleteFiles();
                isWorking = false;
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}