package com.example.germanexam;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.Locale;

public class Answers extends AppCompatActivity {
    final String TASK1 = "Task1";
    final String TASK2 = "Task2";
    final String TASK3 = "Task3";
    final String TASK4 = "Task4";
    final String VARIANT = "Variant";

    SharedPreferences sharedPreferences;

    ImageView playButton1 = null;
    ImageView playButton2 = null;
    ImageView playButton3 = null;
    ImageView playButton4 = null;

    ProgressBar progressBar1 = null;
    ProgressBar progressBar2 = null;
    ProgressBar progressBar3 = null;
    ProgressBar progressBar4 = null;

    TextView timeRemaining1 = null;
    TextView timeRemaining2 = null;
    TextView timeRemaining3 = null;
    TextView timeRemaining4 = null;

    boolean playButton1Pressed = false;
    boolean playButton2Pressed = false;
    boolean playButton3Pressed = false;
    boolean playButton4Pressed = false;

    private MediaPlayer player = null;
    private String fileName = null;

    long timeLeft1 = 0;
    long timeLeft2 = 0;
    long timeLeft3 = 0;
    long timeLeft4 = 0;
    int counter1 = 0;
    int counter2 = 0;
    int counter3 = 0;
    int counter4 = 0;
    CountDownTimer countDownTimer;

    boolean isPlaying = false;

    String[] times = new String[4];

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.answers);
        playButton1 = findViewById(R.id.answer_play1);
        playButton2 = findViewById(R.id.answer_play2);
        playButton3 = findViewById(R.id.answer_play3);
        playButton4 = findViewById(R.id.answer_play4);
        progressBar1 = findViewById(R.id.timeline1);
        progressBar2 = findViewById(R.id.timeline2);
        progressBar3 = findViewById(R.id.timeline3);
        progressBar4 = findViewById(R.id.timeline4);
        timeRemaining1 = findViewById(R.id.time_remaining1);
        timeRemaining2 = findViewById(R.id.time_remaining2);
        timeRemaining3 = findViewById(R.id.time_remaining3);
        timeRemaining4 = findViewById(R.id.time_remaining4);

        playButton1.setImageDrawable(getResources().getDrawable(R.drawable.button_play));
        playButton2.setImageDrawable(getResources().getDrawable(R.drawable.button_play));
        playButton3.setImageDrawable(getResources().getDrawable(R.drawable.button_play));
        playButton4.setImageDrawable(getResources().getDrawable(R.drawable.button_play));

        changeAllTimeRemaining();

        playButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playButton2Pressed || playButton3Pressed || playButton4Pressed) {
                    playButton2Pressed = false;
                    playButton3Pressed = false;
                    playButton4Pressed = false;
                    countDownTimer.cancel();
                    stopPlaying();
                }
                playButton1Pressed = !playButton1Pressed;
                loadData(TASK1);
                if (playButton1Pressed) {
                    startPlaying();
                    timeLeft1 = player.getDuration();
                    progressBar1.setMax((int) (timeLeft1 / 1000) + 1);
                    countDownTimer = new CountDownTimer(timeLeft1, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            if (!isPlaying) {
                                countDownTimer.cancel();
                                resetAllButtons();
                                stopPlaying();
                                return;
                            }
                            timeLeft1 = millisUntilFinished;
                            updateTimer(timeLeft1, timeRemaining1);
                            counter1++;
                            progressBar1.setProgress(counter1);
                        }

                        @Override
                        public void onFinish() {
                            countDownTimer.cancel();
                            playButton1Pressed = false;
                            progressBar1.setProgress(0);
                            timeRemaining1.setText(times[0]);
                            counter1 = 0;
                            stopPlaying();
                            changeButtons();
                        }
                    }.start();
                } else {
                    countDownTimer.cancel();
                    progressBar1.setProgress(0);
                    timeRemaining1.setText(times[0]);
                    counter1 = 0;
                    stopPlaying();
                }
                changeButtons();
            }
        });

        playButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playButton1Pressed || playButton3Pressed || playButton4Pressed) {
                    playButton1Pressed = false;
                    playButton3Pressed = false;
                    playButton4Pressed = false;
                    countDownTimer.cancel();
                    stopPlaying();
                }
                playButton2Pressed = !playButton2Pressed;
                loadData(TASK2);
                if (playButton2Pressed) {
                    startPlaying();
                    timeLeft2 = player.getDuration();
                    progressBar2.setMax((int) (timeLeft2 / 1000) + 1);
                    countDownTimer = new CountDownTimer(timeLeft2, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            if (!isPlaying) {
                                countDownTimer.cancel();
                                resetAllButtons();
                                stopPlaying();
                                return;
                            }
                            timeLeft2 = millisUntilFinished;
                            updateTimer(timeLeft2, timeRemaining2);
                            counter2++;
                            progressBar2.setProgress(counter2);
                        }

                        @Override
                        public void onFinish() {
                            countDownTimer.cancel();
                            playButton2Pressed = false;
                            progressBar2.setProgress(0);
                            timeRemaining2.setText(times[1]);
                            counter2 = 0;
                            stopPlaying();
                            changeButtons();
                        }
                    }.start();
                } else {
                    countDownTimer.cancel();
                    progressBar2.setProgress(0);
                    timeRemaining2.setText(times[1]);
                    counter2 = 0;
                    stopPlaying();
                }
                changeButtons();
            }
        });

        playButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playButton1Pressed || playButton2Pressed || playButton4Pressed) {
                    playButton1Pressed = false;
                    playButton2Pressed = false;
                    playButton4Pressed = false;
                    countDownTimer.cancel();
                    stopPlaying();
                }
                playButton3Pressed = !playButton3Pressed;
                loadData(TASK3);
                if (playButton3Pressed) {
                    startPlaying();
                    timeLeft3 = player.getDuration();
                    progressBar3.setMax((int) (timeLeft3 / 1000) + 1);
                    countDownTimer = new CountDownTimer(timeLeft3, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            if (!isPlaying) {
                                countDownTimer.cancel();
                                resetAllButtons();
                                stopPlaying();
                                return;
                            }
                            timeLeft3 = millisUntilFinished;
                            updateTimer(timeLeft3, timeRemaining3);
                            counter3++;
                            progressBar3.setProgress(counter3);
                        }

                        @Override
                        public void onFinish() {
                            countDownTimer.cancel();
                            playButton3Pressed = false;
                            progressBar3.setProgress(0);
                            timeRemaining3.setText(times[2]);
                            counter3 = 0;
                            stopPlaying();
                            changeButtons();
                        }
                    }.start();
                } else {
                    countDownTimer.cancel();
                    progressBar3.setProgress(0);
                    timeRemaining3.setText(times[2]);
                    counter3 = 0;
                    stopPlaying();
                }
                changeButtons();
            }
        });

        playButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playButton1Pressed || playButton2Pressed || playButton3Pressed) {
                    playButton1Pressed = false;
                    playButton2Pressed = false;
                    playButton3Pressed = false;
                    countDownTimer.cancel();
                    stopPlaying();
                }
                playButton4Pressed = !playButton4Pressed;
                loadData(TASK4);
                if (playButton4Pressed) {
                    startPlaying();
                    timeLeft4 = player.getDuration();
                    progressBar4.setMax((int) (timeLeft4 / 1000) + 1);
                    countDownTimer = new CountDownTimer(timeLeft4, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            if (!isPlaying) {
                                countDownTimer.cancel();
                                resetAllButtons();
                                stopPlaying();
                                return;
                            }
                            timeLeft4 = millisUntilFinished;
                            updateTimer(timeLeft4, timeRemaining4);
                            counter4++;
                            progressBar4.setProgress(counter4);
                        }

                        @Override
                        public void onFinish() {
                            countDownTimer.cancel();
                            playButton4Pressed = false;
                            progressBar4.setProgress(0);
                            timeRemaining4.setText(times[3]);
                            counter4 = 0;
                            stopPlaying();
                            changeButtons();
                        }
                    }.start();
                } else {
                    countDownTimer.cancel();
                    progressBar4.setProgress(0);
                    timeRemaining4.setText(times[3]);
                    counter4 = 0;
                    stopPlaying();
                }
                changeButtons();
            }
        });

        Button endButton = findViewById(R.id.end_answers);

        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences = getSharedPreferences("StudentData", MODE_PRIVATE);
                int variantNumber = sharedPreferences.getInt(VARIANT, 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(VARIANT + variantNumber, true);
                editor.apply();
                Intent intent = new Intent(Answers.this, Share.class);
                startActivity(intent);
                finish();
            }
        });
    }

    void resetAllButtons() {
        playButton1Pressed = false;
        playButton2Pressed = false;
        playButton3Pressed = false;
        playButton4Pressed = false;
        progressBar1.setProgress(0);
        progressBar2.setProgress(0);
        progressBar3.setProgress(0);
        progressBar4.setProgress(0);
        timeRemaining1.setText(times[0]);
        timeRemaining2.setText(times[1]);
        timeRemaining3.setText(times[2]);
        timeRemaining4.setText(times[3]);
        counter1 = 0;
        counter2 = 0;
        counter3 = 0;
        counter4 = 0;
        changeButtons();
    }

    private void changeButtons() {
        if (playButton1Pressed) {
            playButton1.setImageDrawable(getResources().getDrawable(R.drawable.button_stop));
        } else {
            playButton1.setImageDrawable(getResources().getDrawable(R.drawable.button_play));
            progressBar1.setProgress(0);
            timeRemaining1.setText(times[0]);
            counter1 = 0;
        }
        if (playButton2Pressed) {
            playButton2.setImageDrawable(getResources().getDrawable(R.drawable.button_stop));
        } else {
            playButton2.setImageDrawable(getResources().getDrawable(R.drawable.button_play));
            progressBar2.setProgress(0);
            timeRemaining2.setText(times[1]);
            counter2 = 0;
        }
        if (playButton3Pressed) {
            playButton3.setImageDrawable(getResources().getDrawable(R.drawable.button_stop));
        } else {
            playButton3.setImageDrawable(getResources().getDrawable(R.drawable.button_play));
            progressBar3.setProgress(0);
            timeRemaining3.setText(times[2]);
            counter3 = 0;
        }
        if (playButton4Pressed) {
            playButton4.setImageDrawable(getResources().getDrawable(R.drawable.button_stop));
        } else {
            playButton4.setImageDrawable(getResources().getDrawable(R.drawable.button_play));
            progressBar4.setProgress(0);
            timeRemaining4.setText(times[3]);
            counter4 = 0;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isPlaying) {
            isPlaying = false;
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_window_title);
        builder.setNegativeButton(R.string.menu, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(Answers.this, Menu.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        builder.setNeutralButton(R.string.desktop, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finishAffinity();
            }
        });
        builder.setPositiveButton(R.string.variants_menu, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(Answers.this, Variants.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(fileName);
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e("startPlaying()", "prepare() failed");
        }
        isPlaying = true;
    }

    private void stopPlaying() {
        player.release();
        player = null;
        isPlaying = false;
    }

    private void loadData(String task) {
        sharedPreferences = getSharedPreferences("StudentData", MODE_PRIVATE);
        fileName = sharedPreferences.getString(task, "");
    }

    private void changeAllTimeRemaining() {
        sharedPreferences = getSharedPreferences("StudentData", MODE_PRIVATE);
        player = new MediaPlayer();
        for (int i = 1; i < 5; i++) {
            fileName = sharedPreferences.getString("Task" + i, "");
            try {
                player.setDataSource(fileName);
                player.prepare();
            } catch (IOException e) {
                Log.e("setDataSource()", "failed");
            }
            switch (i) {
                case 1:
                    timeLeft1 = player.getDuration();
                    updateTimer(timeLeft1, timeRemaining1);
                    times[0] = timeLeftCalculation(timeLeft1);
                    break;
                case 2:
                    timeLeft2 = player.getDuration();
                    updateTimer(timeLeft2, timeRemaining2);
                    times[1] = timeLeftCalculation(timeLeft2);
                    break;
                case 3:
                    timeLeft3 = player.getDuration();
                    updateTimer(timeLeft3, timeRemaining3);
                    times[2] = timeLeftCalculation(timeLeft3);
                    break;
                case 4:
                    timeLeft4 = player.getDuration();
                    updateTimer(timeLeft4, timeRemaining4);
                    times[3] = timeLeftCalculation(timeLeft4);
                    break;
            }
            player.reset();
        }
        player = null;
    }

    private void updateTimer(long timeLeft, TextView timeRemaining) {
        String timeLeftText = timeLeftCalculation(timeLeft);
        timeRemaining.setText(timeLeftText);
    }

    private String timeLeftCalculation(long timeLeft) {
        int minutes = (int) (timeLeft / 1000) / 60;
        int seconds = (int) (timeLeft / 1000) % 60;

        return String.format(Locale.getDefault(), "-%02d:%02d", minutes, seconds);
    }
}