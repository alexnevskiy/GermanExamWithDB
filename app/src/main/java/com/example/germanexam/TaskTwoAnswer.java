package com.example.germanexam;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class TaskTwoAnswer extends AppCompatActivity {
    final String TASK1 = "Task1";
    final String TASK2 = "Task2";
    final String TASK3 = "Task3";
    final String TASK4 = "Task4";
    final String VARIANT = "Variant";
    final String NAME = "Name";
    final String SURNAME = "Surname";
    final String CLASS = "Class";
    final String TASK2PICTURE = "Task2Picture";
    final String TASK2QUESTION1 = "Task2Question1";
    final String TASK2QUESTION2 = "Task2Question2";
    final String TASK2QUESTION3 = "Task2Question3";
    final String TASK2QUESTION4 = "Task2Question4";
    final String TASK2QUESTION5 = "Task2Question5";
    final String TASK2PICTURETEXT = "Task2PictureText";
    final String RESTART = "Restart";

    private final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private String fileName = null;
    private boolean isWorking = false;

    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    private MediaRecorder recorder = null;

    SharedPreferences sharedPreferences;

    long timeLeft = 100000;
    int counter = 0;
    CountDownTimer countDownTimer;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        saveFilename();
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        startRecording();

        setContentView(R.layout.task2_answer);
        final TextView timeRemaining = findViewById(R.id.time_remaining);
        final ProgressBar timeline = findViewById(R.id.timeline);
        final TextView questionText = findViewById(R.id.task2_text);

        TextView task2PictureTextView = findViewById(R.id.task2_title_image);
        ImageView task2ImageView = findViewById(R.id.task2_image);
        sharedPreferences = getSharedPreferences("StudentData", MODE_PRIVATE);
        String task2Text = sharedPreferences.getString(TASK2PICTURETEXT, "");
        final String task2Question1 = sharedPreferences.getString(TASK2QUESTION1, "");
        final String task2Question2 = sharedPreferences.getString(TASK2QUESTION2, "");
        final String task2Question3 = sharedPreferences.getString(TASK2QUESTION3, "");
        final String task2Question4 = sharedPreferences.getString(TASK2QUESTION4, "");
        final String task2Question5 = sharedPreferences.getString(TASK2QUESTION5, "");
        String task2Image = sharedPreferences.getString(TASK2PICTURE, "");
        int pictureId = getResources().getIdentifier(task2Image, "drawable", getPackageName());
        task2PictureTextView.setText(task2Text);
        task2ImageView.setImageDrawable(getResources().getDrawable(pictureId));

        countDownTimer = new CountDownTimer(timeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                updateTimer();
                switch (counter) {
                    case 0:
                        questionText.setText("Frage 1.\n" + task2Question1);
                        break;
                    case 20:
                        questionText.setText("Frage 2.\n" + task2Question2);
                        break;
                    case 40:
                        questionText.setText("Frage 3.\n" + task2Question3);
                        break;
                    case 60:
                        questionText.setText("Frage 4.\n" + task2Question4);
                        break;
                    case 80:
                        questionText.setText("Frage 5.\n" + task2Question5);
                        break;
                }
                counter++;
                timeline.setProgress(counter);
                if (timeLeft < 1000) {
                    stopRecording();
                    Intent intent = new Intent(TaskTwoAnswer.this, Ready.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.putExtra("task", "3");
                    intent.putExtra("answer", "no");
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
    public void onStop() {
        super.onStop();
        if (isWorking) {
            countDownTimer.cancel();
            stopRecording();

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
                stopRecording();
                Intent intent = new Intent(TaskTwoAnswer.this, Menu.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                countDownTimer.cancel();
                deleteFiles();
            }
        });
        builder.setNeutralButton(R.string.desktop, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                stopRecording();
                countDownTimer.cancel();
                deleteFiles();
                finishAffinity();
            }
        });
        builder.setPositiveButton(R.string.variants_menu, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                stopRecording();
                Intent intent = new Intent(TaskTwoAnswer.this, Variants.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                countDownTimer.cancel();
                deleteFiles();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFile(fileName);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setAudioEncodingBitRate(128000);
        recorder.setAudioSamplingRate(96000);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e("startRecording()", "prepare() failed");
        }

        recorder.start();
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
        Log.i("Recording", "Recording stopped, file path: " + fileName);
    }

    private void saveFilename() {
        fileName = getFilesDir().getAbsolutePath();
        sharedPreferences = getSharedPreferences("StudentData", MODE_PRIVATE);
        fileName += "/audio/" + sharedPreferences.getString(SURNAME, "") + "_";
        fileName += sharedPreferences.getString(NAME, "") + "_";
        fileName += sharedPreferences.getString(CLASS, "") + "_Aufgabe2_Variant_";
        fileName += sharedPreferences.getInt(VARIANT, 0) + ".mp3";
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TASK2, fileName);
        editor.apply();
    }
}