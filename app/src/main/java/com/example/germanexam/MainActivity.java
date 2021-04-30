package com.example.germanexam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.germanexam.database.Database;

import java.io.File;

import static com.example.germanexam.constants.Constants.*;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    EditText personName;
    EditText personSurname;
    EditText personClass;

    private final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

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
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        sharedPreferences = getSharedPreferences("StudentData", MODE_PRIVATE);
        String name = sharedPreferences.getString(NAME, "");
        String surname = sharedPreferences.getString(SURNAME, "");
        String classPerson = sharedPreferences.getString(CLASS, "");

        File file = new File(getFilesDir(), "/audio");
        if (file.exists()) {
            Log.d("File", "exists");
        } else {
            Log.d("File", "not exists");
            file.mkdir();
        }

        if (!name.equals("") && !surname.equals("") && !classPerson.equals("")) {
            Intent intent = new Intent(MainActivity.this, Menu.class);
            startActivity(intent);
            finish();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button buttonStart = findViewById(R.id.buttonStart);

        personName = findViewById(R.id.editTextTextPersonName);
        personSurname = findViewById(R.id.editTextTextPersonSurname);
        personClass = findViewById(R.id.editTextTextPersonClass);

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameText = personName.getText().toString();
                String surnameText = personSurname.getText().toString();
                String classText = personClass.getText().toString();
                if (nameText.equals("") || surnameText.equals("") || classText.equals("")
                        || !nameText.matches("[A-Za-zА-Яа-яЁё]+")
                        || !surnameText.matches("[A-Za-zА-Яа-яЁё]+")
                        || !classText.matches("(\\d*[A-Za-zА-Яа-яЁё]*)+")) {
                    if (nameText.equals("")) {
                        personName.setError("Введите имя");
                    } else if (!nameText.matches("[A-Za-zА-Яа-яЁё]+")) {
                        personName.setError("Введите корректное имя");
                    }
                    if (surnameText.equals("")) {
                        personSurname.setError("Введите фамилию");
                    } else if (!surnameText.matches("[A-Za-zА-Яа-яЁё]+")) {
                        personSurname.setError("Введите корректную фамилию");
                    }
                    if (classText.equals("")) {
                        personClass.setError("Введите класс");
                    } else if (!classText.matches("(\\d*[A-Za-zА-Яа-яЁё]*)+")) {
                        personClass.setError("Введите корректный класс");
                    }
                } else {
                    saveData();
                    Intent intent = new Intent(MainActivity.this, Menu.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void saveData() {
        String name = personName.getText().toString();
        String surname = personSurname.getText().toString();
        String userClass = personClass.getText().toString();
        int userId = Database.insertUser(new String[] {name, surname, userClass});

        sharedPreferences = getSharedPreferences("StudentData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(NAME, name);
        editor.putString(SURNAME, surname);
        editor.putString(CLASS, userClass);
        editor.putInt(USER_ID, userId);
        editor.apply();

        Toast.makeText(MainActivity.this, "Данные сохранены", Toast.LENGTH_SHORT).show();
    }
}