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

public class TaskFour extends AppCompatActivity {

    long timeLeft = 2000;
    int counter = 0;
    CountDownTimer countDownTimer;

    final String TASK4QUESTIONS = "Task4Questions";
    final String TASK4PICTURE1 = "Task4Picture1";
    final String TASK4PICTURE2 = "Task4Picture2";

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
                Intent intent = new Intent(TaskFour.this, Menu.class);
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
                Intent intent = new Intent(TaskFour.this, Variants.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                countDownTimer.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}