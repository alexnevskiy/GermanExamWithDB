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

public class TaskFour extends AppCompatActivity {

    long timeLeft = 90000;
    int counter = 0;
    CountDownTimer countDownTimer;

    final String TASK4QUESTIONS = "Task4Questions";
    final String TASK4PICTURE1 = "Task4Picture1";
    final String TASK4PICTURE2 = "Task4Picture2";
    final String TASK1 = "Task1";
    final String TASK2 = "Task2";
    final String TASK3 = "Task3";
    final String TASK4 = "Task4";
    final String RESTART = "Restart";

    private String fileName = null;
    private boolean isWorking = false;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task4);
        final TextView timeRemaining = findViewById(R.id.time_remaining);
        final ProgressBar timeline = findViewById(R.id.timeline);

        TextView task4CompareView = findViewById(R.id.task4_compare);
        ImageView task4ImageView1 = findViewById(R.id.task4_photo1);
        ImageView task4ImageView2 = findViewById(R.id.task4_photo2);
        sharedPreferences = getSharedPreferences("StudentData", MODE_PRIVATE);
        String task4Questions = sharedPreferences.getString(TASK4QUESTIONS, "");
        String task4Image1 = sharedPreferences.getString(TASK4PICTURE1, "");
        String task4Image2 = sharedPreferences.getString(TASK4PICTURE2, "");
        int picture1Id = getResources().getIdentifier(task4Image1, "drawable", getPackageName());
        int picture2Id = getResources().getIdentifier(task4Image2, "drawable", getPackageName());
        task4CompareView.setText(task4Questions);
        task4ImageView1.setImageDrawable(getResources().getDrawable(picture1Id));
        task4ImageView2.setImageDrawable(getResources().getDrawable(picture2Id));

        countDownTimer = new CountDownTimer(timeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                updateTimer();
                counter++;
                timeline.setProgress(counter);
                if (timeLeft < 1000) {
                    Intent intent = new Intent(TaskFour.this, Ready.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.putExtra("task", "4");
                    intent.putExtra("answer", "yes");
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

        loadData(TASK3);
        File file3 = new File(fileName);
        boolean deleted3 = file3.delete();
        Log.i("TaskFourAnswer", "Audio3 is deleting:" + deleted3);

        loadData(TASK4);
        File file4 = new File(fileName);
        boolean deleted4 = file4.delete();
        Log.i("TaskFourAnswer", "Audio4 is deleting:" + deleted4);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_window_title);
        builder.setNegativeButton(R.string.menu, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(TaskFour.this, Menu.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                countDownTimer.cancel();
                deleteFiles();
            }
        });
        builder.setNeutralButton(R.string.desktop, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                countDownTimer.cancel();
                deleteFiles();
                finishAffinity();
            }
        });
        builder.setPositiveButton(R.string.variants_menu, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(TaskFour.this, Variants.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                countDownTimer.cancel();
                deleteFiles();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}