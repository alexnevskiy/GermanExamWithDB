package com.example.germanexam;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;

public class Settings extends AppCompatActivity {

    TextView studentName;
    TextView studentClass;

    EditText personName;
    EditText personSurname;
    EditText personClass;

    final String NAME = "Name";
    final String SURNAME = "Surname";
    final String CLASS = "Class";

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        studentName = findViewById(R.id.student_name);
        studentClass = findViewById(R.id.person_class);

        loadData();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_map:
                        AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
                        LayoutInflater inflater = Settings.this.getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.name_dialog, null);
                        builder.setView(dialogView);
                        personName = dialogView.findViewById(R.id.editTextTextPersonName);
                        personSurname = dialogView.findViewById(R.id.editTextTextPersonSurname);
                        personClass = dialogView.findViewById(R.id.editTextTextPersonClass);
                        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                        final AlertDialog dialog = builder.create();
                        dialog.show();
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                boolean close = false;
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
                                    loadData();
                                    close = true;
                                }
                                if (close) {
                                    dialog.dismiss();
                                }
                            }
                        });
                }
                return true;
            }
        });

        final Button microphoneTest = findViewById(R.id.microphone_test);

        microphoneTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.this, MicrophoneTest.class);
                startActivity(intent);
            }
        });

        final Button fileManager = findViewById(R.id.file_manager);

        fileManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = getFilesDir().toString() + "/audio";
                File directory = new File(path);
                File[] files = directory.listFiles();
                Intent intent;
                if (files.length < 4) {
                    intent = new Intent(Settings.this, FilesNotFound.class);
                } else {
                    intent = new Intent(Settings.this, FileManager.class);
                }
                startActivity(intent);
            }
        });

        Button aboutApp = findViewById(R.id.about_application);

        aboutApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.this, AboutApp.class);
                startActivity(intent);
            }
        });
    }

    private void saveData() {
        sharedPreferences = getSharedPreferences("StudentData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(NAME, personName.getText().toString());
        editor.putString(SURNAME, personSurname.getText().toString());
        editor.putString(CLASS, personClass.getText().toString());
        editor.apply();
        Toast.makeText(Settings.this, "Данные сохранены", Toast.LENGTH_SHORT).show();
    }

    private void loadData() {
        sharedPreferences = getSharedPreferences("StudentData", MODE_PRIVATE);
        String personNameString = "Ученик: ";
        personNameString += sharedPreferences.getString(SURNAME, "") + " ";
        personNameString += sharedPreferences.getString(NAME, "");
        studentName.setText(personNameString);
        String personClassString = "Класс: ";
        personClassString += sharedPreferences.getString(CLASS, "");
        studentClass.setText(personClassString);
    }
}
