package com.example.germanexam;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class TaskTwo extends AppCompatActivity {

    final String TASK2TITLE = "Task2Title";
    final String TASK2QUESTIONS = "Task2Questions";
    final String TASK2PICTURE = "Task2Picture";
    final String TASK2PICTURETEXT = "Task2PictureText";

    SharedPreferences sharedPreferences;

    long timeLeft = 2000;
    int counter = 0;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task2);
        final TextView timeRemaining = findViewById(R.id.time_remaining);
        final ProgressBar timeline = findViewById(R.id.timeline);

        TextView task2TextView = findViewById(R.id.task2_text);
        TextView task2QuestionsView = findViewById(R.id.task2_questions);
        TextView task2PictureTextView = findViewById(R.id.task2_title_image);
        ImageView task2ImageView = findViewById(R.id.task2_image);
        sharedPreferences = getSharedPreferences("StudentData", MODE_PRIVATE);
        String task2PictureText = sharedPreferences.getString(TASK2PICTURETEXT, "");
        String task2Text = sharedPreferences.getString(TASK2TITLE, "");
        String task2Questions = sharedPreferences.getString(TASK2QUESTIONS, "");
        String task2Image = sharedPreferences.getString(TASK2PICTURE, "");
        int pictureId = getResources().getIdentifier(task2Image, "drawable", getPackageName());
        task2TextView.setText("Aufgabe 2. Sehen Sie sich folgende Anzeige an.\n" + task2Text);
        task2QuestionsView.setText(task2Questions);
        task2PictureTextView.setText(task2PictureText);
        task2ImageView.setImageDrawable(getResources().getDrawable(pictureId));

        countDownTimer = new CountDownTimer(timeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                updateTimer();
                counter++;
                timeline.setProgress(counter);
                if (timeLeft < 1000) {
                    Intent intent = new Intent(TaskTwo.this, Ready.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.putExtra("task", "2");
                    intent.putExtra("answer", "yes");
                    startActivity(intent);
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
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_window_title);
        builder.setNegativeButton(R.string.menu, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(TaskTwo.this, Menu.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                countDownTimer.cancel();
            }
        });
        builder.setNeutralButton(R.string.desktop, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                countDownTimer.cancel();
                finishAffinity();
            }
        });
        builder.setPositiveButton(R.string.variants_menu, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(TaskTwo.this, Variants.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                countDownTimer.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}