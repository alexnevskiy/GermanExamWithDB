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
        Intent myIntent = getIntent();
        switch (myIntent.getStringExtra("task")) {
            case "2":
                deleteTask(TASK1);
                break;
            case "3":
                deleteTask(TASK1);
                deleteTask(TASK2);
                break;
            case "4":
                deleteTask(TASK1);
                deleteTask(TASK2);
                deleteTask(TASK3);
                break;
        }
    }

    private void deleteTask(String task) {
        loadData(task);
        File file = new File(fileName);
        boolean deleted = file.delete();
        Log.i("TaskFourAnswer", "Audio " + task + " is deleting: " + deleted);
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(Ready.this, "Подождите окончания отсчёта времени", Toast.LENGTH_SHORT).show();
    }
}
