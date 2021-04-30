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

import com.example.germanexam.cache.CacheDatabase;
import com.example.germanexam.database.Database;
import com.example.germanexam.taskdata.Task1;
import com.example.germanexam.taskdata.Task2;
import com.example.germanexam.taskdata.Task3;
import com.example.germanexam.taskdata.Task4;

import java.io.File;
import java.util.Locale;

import static com.example.germanexam.constants.Constants.*;

public class Ready extends AppCompatActivity {

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
        sharedPreferences = getSharedPreferences("StudentData", MODE_PRIVATE);
        int variant = sharedPreferences.getInt(VARIANT, 0);
        boolean cache = sharedPreferences.getBoolean(CACHE, false);

        switch (myIntent.getIntExtra("task", 0)) {
            case 1:
                taskText.setText(R.string.task_one);
                preparationText.setText(R.string.prep_ans_task1);
                if (myIntent.getBooleanExtra("answer", false)) {
                    String text = myIntent.getStringExtra("text");
                    upperText.setText(R.string.ready_upper_answer_text);

                    intent = new Intent(Ready.this, TaskOneAnswer.class);
                    intent.putExtra("text", text);
                } else {
                    Task1 task1 = Database.getTask1(variant);
                    String text = task1.getText();
                    upperText.setText(R.string.ready_upper_start_text);

                    intent = new Intent(Ready.this, TaskOne.class);
                    intent.putExtra("text", text);
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                break;
            case 2:
                taskText.setText(R.string.task_two);
                preparationText.setText(R.string.prep_ans_task2);
                if (myIntent.getBooleanExtra("answer", false)) {
                    String questions = myIntent.getStringExtra("questions");
                    String image = myIntent.getStringExtra("image");
                    String imageText = myIntent.getStringExtra("imageText");
                    upperText.setText(R.string.ready_upper_answer_text);

                    intent = new Intent(Ready.this, TaskTwoAnswer.class);
                    intent.putExtra("questions", questions);
                    intent.putExtra("image", image);
                    intent.putExtra("imageText", imageText);
                } else {
                    Task2 task2 = CacheDatabase.getTask2(cache, variant);
                    upperText.setText(R.string.ready_upper_next_text);

                    intent = new Intent(Ready.this, TaskTwo.class);
                    intent.putExtra("title", task2.getTitle());
                    intent.putExtra("questions", task2.getQuestions());
                    intent.putExtra("image", task2.getImagePath());
                    intent.putExtra("imageText", task2.getImageText());
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                break;
            case 3:
                taskText.setText(R.string.task_three);
                preparationText.setText(R.string.prep_ans_task3);
                if (myIntent.getBooleanExtra("answer", false)) {
                    upperText.setText(R.string.ready_upper_answer_text);
                    if (myIntent.getStringExtra("photos").equals("all")) {
                        String questions = myIntent.getStringExtra("questions");
                        String image1 = myIntent.getStringExtra("image1");
                        String image2 = myIntent.getStringExtra("image2");
                        String image3 = myIntent.getStringExtra("image3");

                        intent = new Intent(Ready.this, TaskThreeAnswerAllPhotos.class);
                        intent.putExtra("questions", questions);
                        intent.putExtra("image1", image1);
                        intent.putExtra("image2", image2);
                        intent.putExtra("image3", image3);
                    } else {
                        String questions = myIntent.getStringExtra("questions");
                        String image = myIntent.getStringExtra("image");
                        int photoNumber = myIntent.getIntExtra("photo", 0);

                        intent = new Intent(Ready.this, TaskThreeAnswer.class);
                        intent.putExtra("questions", questions);
                        intent.putExtra("image", image);
                        intent.putExtra("photo", photoNumber);
                    }
                } else {
                    Task3 task3 = CacheDatabase.getTask3(cache, variant);
                    upperText.setText(R.string.ready_upper_next_text);

                    intent = new Intent(Ready.this, TaskThree.class);
                    intent.putExtra("questions", task3.getQuestions());
                    intent.putExtra("image1", task3.getImagePath1());
                    intent.putExtra("image2", task3.getImagePath2());
                    intent.putExtra("image3", task3.getImagePath3());
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                break;
            case 4:
                taskText.setText(R.string.task_four);
                preparationText.setText(R.string.prep_ans_task4);
                if (myIntent.getBooleanExtra("answer", false)) {
                    String questions = myIntent.getStringExtra("questions");
                    String image1 = myIntent.getStringExtra("image1");
                    String image2 = myIntent.getStringExtra("image2");
                    upperText.setText(R.string.ready_upper_answer_text);

                    intent = new Intent(Ready.this, TaskFourAnswer.class);
                    intent.putExtra("questions", questions);
                    intent.putExtra("image1", image1);
                    intent.putExtra("image2", image2);
                } else {
                    Task4 task4 = CacheDatabase.getTask4(cache, variant);
                    upperText.setText(R.string.ready_upper_next_text);

                    intent = new Intent(Ready.this, TaskFour.class);
                    intent.putExtra("questions", task4.getQuestions());
                    intent.putExtra("image1", task4.getImagePath1());
                    intent.putExtra("image2", task4.getImagePath2());
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
            }

            private void updateTimer() {
                int seconds = (int) (timeLeft / 1000) % 60;

                String timeLeftText = String.format(Locale.getDefault(), "%02d", seconds);

                timeRemaining.setText(timeLeftText);
            }

            @Override
            public void onFinish() {
                isWorking = false;
                startActivity(finalIntent);
                countDownTimer.cancel();
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
        switch (myIntent.getIntExtra("task", 0)) {
            case 2:
                deleteTask(TASK1);
                break;
            case 3:
                deleteTask(TASK1);
                deleteTask(TASK2);
                break;
            case 4:
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
        Log.i("Ready", "Audio " + task + " is deleting: " + deleted);
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(Ready.this, "Подождите окончания отсчёта времени", Toast.LENGTH_SHORT).show();
    }
}
