package com.example.germanexam;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class TaskThree extends AppCompatActivity {

    long timeLeft = 2000;
    int counter = 0;
    CountDownTimer countDownTimer;

    final String TASK3QUESTIONS = "Task3Questions";
    final String TASK3PICTURE1 = "Task3Picture1";
    final String TASK3PICTURE2 = "Task3Picture2";
    final String TASK3PICTURE3 = "Task3Picture3";

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
                if (timeLeft < 1000) {
                    Intent intent = new Intent(TaskThree.this, Ready.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.putExtra("task", "3");
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

        photoButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskThree.this, TaskThreePhoto.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.putExtra("timeLeft", timeLeft);
                intent.putExtra("counter", counter);
                intent.putExtra("photo", 1);
                startActivity(intent);
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
                countDownTimer.cancel();
            }
        });
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
                Intent intent = new Intent(TaskThree.this, Variants.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                countDownTimer.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}