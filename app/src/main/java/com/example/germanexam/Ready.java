package com.example.germanexam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.Locale;

public class Ready extends AppCompatActivity {
    final String TASK1 = "Task1";
    final String TASK2 = "Task2";
    final String TASK3 = "Task3";
    final String TASK4 = "Task4";
    final String RESTART = "Restart";

    private String fileName = null;
    private boolean isWorking = false;

    SharedPreferences sharedPreferences;

    long timeLeft = 6000;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ready);
        final TextView timeRemaining = findViewById(R.id.ready_seconds);
        TextView upperText = findViewById(R.id.ready_upper);
        TextView taskText = findViewById(R.id.ready_task);
        TextView preparationText = findViewById(R.id.ready_prep_ans);
        Intent myIntent = getIntent();
        Intent intent = null;

        switch (myIntent.getStringExtra("task")) {
            case "1":
                taskText.setText(R.string.task_one);
                preparationText.setText(R.string.prep_ans_task1);
                if (myIntent.getStringExtra("answer").equals("yes")) {
                    upperText.setText(R.string.ready_upper_answer_text);
                    intent = new Intent(Ready.this, TaskOneAnswer.class);
                } else {
                    upperText.setText(R.string.ready_upper_start_text);
                    intent = new Intent(Ready.this, TaskOne.class);
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                break;
            case "2":
                taskText.setText(R.string.task_two);
                preparationText.setText(R.string.prep_ans_task2);
                if (myIntent.getStringExtra("answer").equals("yes")) {
                    upperText.setText(R.string.ready_upper_answer_text);
                    intent = new Intent(Ready.this, TaskTwoAnswer.class);
                } else {
                    upperText.setText(R.string.ready_upper_next_text);
                    intent = new Intent(Ready.this, TaskTwo.class);
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                break;
            case "3":
                taskText.setText(R.string.task_three);
                preparationText.setText(R.string.prep_ans_task3);
                if (myIntent.getStringExtra("answer").equals("yes")) {
                    upperText.setText(R.string.ready_upper_answer_text);
                    intent = new Intent(Ready.this, TaskThreeAnswer.class);
                    int photoNumber = Ready.this.getIntent().getIntExtra("photo", 1);
                    intent.putExtra("photo", photoNumber);
                } else {
                    upperText.setText(R.string.ready_upper_next_text);
                    intent = new Intent(Ready.this, TaskThree.class);
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                break;
            case "4":
                taskText.setText(R.string.task_four);
                preparationText.setText(R.string.prep_ans_task4);
                if (myIntent.getStringExtra("answer").equals("yes")) {
                    upperText.setText(R.string.ready_upper_answer_text);
                    intent = new Intent(Ready.this, TaskFourAnswer.class);
                } else {
                    upperText.setText(R.string.ready_upper_next_text);
                    intent = new Intent(Ready.this, TaskFour.class);
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                break;
        }

        final Intent finalIntent = intent;
        countDownTimer = new CountDownTimer(timeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                updateTimer();
                if (timeLeft < 1000) {
                    isWorking = false;
                    startActivity(finalIntent);
                    countDownTimer.cancel();
                }
            }

            private void updateTimer() {
                int seconds = (int) (timeLeft / 1000) % 60;

                String timeLeftText = String.format(Locale.getDefault(), "%02d", seconds);

                timeRemaining.setText(timeLeftText);
            }

            @Override
            public void onFinish() {
            }
        }.start();

        isWorking = true;
    }

    @Override
    public void onStop() {
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
        Toast.makeText(Ready.this, "Подождите окончания отсчёта времени", Toast.LENGTH_SHORT).show();
    }
}
