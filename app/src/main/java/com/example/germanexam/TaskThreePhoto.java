package com.example.germanexam;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.Locale;

public class TaskThreePhoto extends AppCompatActivity {

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
        setContentView(R.layout.task3_photo);
        final TextView timeRemaining = findViewById(R.id.time_remaining);
        final ProgressBar timeline = findViewById(R.id.timeline);
        TextView photoTitle = findViewById(R.id.task3_photo_title);
        ImageView photo = findViewById(R.id.task3_photo);
        TextView task3QuestionsView = findViewById(R.id.task3_questions);

        sharedPreferences = getSharedPreferences("StudentData", MODE_PRIVATE);
        String task3Questions = sharedPreferences.getString(TASK3QUESTIONS, "");
        task3QuestionsView.setText(task3Questions);

        Intent myIntent = TaskThreePhoto.this.getIntent();

        final int photoNumber = myIntent.getIntExtra("photo", 1);
        photoTitle.setText("Foto " + photoNumber);
        switch (photoNumber) {
            case 1:
                String task3Image1 = sharedPreferences.getString(TASK3PICTURE1, "");
                int picture1Id = getResources().getIdentifier(task3Image1, "drawable", getPackageName());
                photo.setImageDrawable(getResources().getDrawable(picture1Id));
                break;
            case 2:
                String task3Image2 = sharedPreferences.getString(TASK3PICTURE2, "");
                int picture2Id = getResources().getIdentifier(task3Image2, "drawable", getPackageName());
                photo.setImageDrawable(getResources().getDrawable(picture2Id));
                break;
            case 3:
                String task3Image3 = sharedPreferences.getString(TASK3PICTURE3, "");
                int picture3Id = getResources().getIdentifier(task3Image3, "drawable", getPackageName());
                photo.setImageDrawable(getResources().getDrawable(picture3Id));
                break;
        }

        timeLeft = myIntent.getLongExtra("timeLeft", 90000);
        counter = myIntent.getIntExtra("counter", 0);

        countDownTimer = new CountDownTimer(timeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                updateTimer();
                counter++;
                timeline.setProgress(counter);
                if (timeLeft < 1000) {
                    Intent intent = new Intent(TaskThreePhoto.this, Ready.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.putExtra("task", "3");
                    intent.putExtra("answer", "yes");
                    intent.putExtra("photo", photoNumber);
                    startActivity(intent);
                    isWorking = false;
                    countDownTimer.cancel();
                }
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

        isWorking = true;
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
                Intent intent = new Intent(TaskThreePhoto.this, Menu.class);
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
                Intent intent = new Intent(TaskThreePhoto.this, Variants.class);
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