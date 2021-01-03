package com.example.germanexam;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class Ready extends AppCompatActivity {

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
            case ("1"):
                taskText.setText(R.string.task_one);
                preparationText.setText(R.string.prep_ans_task1);
                if (myIntent.getStringExtra("answer").equals("yes")) {
                    upperText.setText(R.string.ready_upper_answer_text);
                    intent = new Intent(Ready.this, TaskOneAnswer.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                } else {
                    upperText.setText(R.string.ready_upper_start_text);
                    intent = new Intent(Ready.this, TaskOne.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                }
                break;
            case ("2"):
                taskText.setText(R.string.task_two);
                preparationText.setText(R.string.prep_ans_task2);
                if (myIntent.getStringExtra("answer").equals("yes")) {
                    upperText.setText(R.string.ready_upper_answer_text);
                    intent = new Intent(Ready.this, TaskTwoAnswer.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                } else {
                    upperText.setText(R.string.ready_upper_next_text);
                    intent = new Intent(Ready.this, TaskTwo.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                }
                break;
            case ("3"):
                taskText.setText(R.string.task_three);
                preparationText.setText(R.string.prep_ans_task3);
                if (myIntent.getStringExtra("answer").equals("yes")) {
                    upperText.setText(R.string.ready_upper_answer_text);
                    intent = new Intent(Ready.this, TaskThreeAnswer.class);
                    int photoNumber = Ready.this.getIntent().getIntExtra("photo", 1);
                    intent.putExtra("photo", photoNumber);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                } else {
                    upperText.setText(R.string.ready_upper_next_text);
                    intent = new Intent(Ready.this, TaskThree.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                }
                break;
            case ("4"):
                taskText.setText(R.string.task_four);
                preparationText.setText(R.string.prep_ans_task4);
                if (myIntent.getStringExtra("answer").equals("yes")) {
                    upperText.setText(R.string.ready_upper_answer_text);
                    intent = new Intent(Ready.this, TaskFourAnswer.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                } else {
                    upperText.setText(R.string.ready_upper_next_text);
                    intent = new Intent(Ready.this, TaskFour.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                }
                break;
        }

        final Intent finalIntent = intent;
        countDownTimer = new CountDownTimer(timeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                updateTimer();
                if (timeLeft < 1000) {
                    startActivity(finalIntent);
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
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(Ready.this, "Подождите окончания отсчёта времени", Toast.LENGTH_SHORT).show();
    }
}
