package com.example.germanexam;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static androidx.core.content.FileProvider.getUriForFile;

public class MicrophoneTest extends AppCompatActivity {
    private final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private String fileName = null;

    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    private MediaRecorder recorder = null;
    private MediaPlayer player = null;

    boolean recordingButtonPressed = false;
    boolean playingButtonPressed = false;

    Button buttonRecording = null;
    Button buttonPlaying = null;

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
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.microphone_test);

        fileName = getFilesDir().getAbsolutePath();
        fileName += "/audio/" + "microphone_test.mp3";

        buttonRecording = findViewById(R.id.microphone_test_button);

        buttonRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordingButtonPressed = !recordingButtonPressed;
                if (recordingButtonPressed) {
                    startRecording();
                    buttonRecording.setBackground(getResources().getDrawable(R.drawable.button_green));
                    buttonRecording.setText(R.string.recording_microphone_stop);
                } else {
                    stopRecording();
                    buttonRecording.setBackground(getResources().getDrawable(R.drawable.button_blue));
                    buttonRecording.setText(R.string.recording_microphone);
                }
            }
        });

        buttonPlaying = findViewById(R.id.play_recording);

        buttonPlaying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playingButtonPressed = !playingButtonPressed;
                if (playingButtonPressed) {
                    buttonPlaying.setBackground(getResources().getDrawable(R.drawable.button_red));
                    buttonPlaying.setText(R.string.play_recording_stop);
                    startPlaying();
                    player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        public void onCompletion(MediaPlayer mp) {
                            playingButtonPressed = !playingButtonPressed;
                            stopPlaying();
                            buttonPlaying.setBackground(getResources().getDrawable(R.drawable.button_blue));
                            buttonPlaying.setText(R.string.play_recording);
                        }
                    });
                } else {
                    stopPlaying();
                    buttonPlaying.setBackground(getResources().getDrawable(R.drawable.button_blue));
                    buttonPlaying.setText(R.string.play_recording);
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            stopPlaying();
        }
        if (recorder != null) {
            stopRecording();
        }
        File file = new File(fileName);
        boolean deleted = file.delete();
        Log.i("MicrophoneTest", "File is deleting:" + deleted);
        recordingButtonPressed = false;
        playingButtonPressed = false;

        buttonRecording.setBackground(getResources().getDrawable(R.drawable.button_blue));
        buttonRecording.setText(R.string.recording_microphone);

        buttonPlaying.setBackground(getResources().getDrawable(R.drawable.button_blue));
        buttonPlaying.setText(R.string.play_recording);
    }

    @Override
    public void onBackPressed() {
        File file = new File(fileName);
        boolean deleted = file.delete();
        Log.i("MicrophoneTest", "File is deleting:" + deleted);
        super.onBackPressed();
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

    private void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(fileName);
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e("startPlaying()", "prepare() failed");
            Toast.makeText(MicrophoneTest.this, "Сначала запишите свой голос.", Toast.LENGTH_SHORT).show();
            buttonPlaying.setBackground(getResources().getDrawable(R.drawable.button_blue));
            buttonPlaying.setText(R.string.play_recording);
        }
    }

    private void stopPlaying() {
        player.release();
        player = null;
    }
}