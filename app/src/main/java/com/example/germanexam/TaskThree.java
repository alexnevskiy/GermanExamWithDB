package com.example.germanexam;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class TaskThree extends AppCompatActivity {

    long timeLeft = 90000;
    int counter = 0;
    CountDownTimer countDownTimer;

    final String TASK3QUESTIONS = "Task3Questions";
    final String TASK3PICTURE1 = "Task3Picture1";
    final String TASK3PICTURE2 = "Task3Picture2";
    final String TASK3PICTURE3 = "Task3Picture3";
    final String TASK1 = "Task1";
    final String TASK2 = "Task2";
    final String RESTART = "Restart";

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
        sharedPreferences = getSharedPreferences("StudentData", MODE_PRIVATE);
        String task3Questions = sharedPreferences.getString(TASK3QUESTIONS, "");
        String task3Image1 = sharedPreferences.getString(TASK3PICTURE1, "");
        String task3Image2 = sharedPreferences.getString(TASK3PICTURE2, "");
        String task3Image3 = sharedPreferences.getString(TASK3PICTURE3, "");
        int picture1Id = getResources().getIdentifier(task3Image1, "drawable", getPackageName());
        int picture2Id = getResources().getIdentifier(task3Image2, "drawable", getPackageName());
        int picture3Id = getResources().getIdentifier(task3Image3, "drawable", getPackageName());
        task3QuestionsView.setText(task3Questions);
        task3ImageView1.setImageDrawable(getResources().getDrawable(picture1Id));
        task3ImageView2.setImageDrawable(getResources().getDrawable(picture2Id));
        task3ImageView3.setImageDrawable(getResources().getDrawable(picture3Id));

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
                intent.putExtra("task", "3");
                intent.putExtra("answer", "yes");
                intent.putExtra("photos", "all");
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
        Log.i("TaskFourAnswer", "Audio1 is deleting:" + deleted1);

        loadData(TASK2);
        File file2 = new File(fileName);
        boolean deleted2 = file2.delete();
        Log.i("TaskFourAnswer", "Audio2 is deleting:" + deleted2);
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