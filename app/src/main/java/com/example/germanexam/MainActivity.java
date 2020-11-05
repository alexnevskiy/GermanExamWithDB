package com.example.germanexam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    final String NAME = "Name";
    final String SURNAME = "Surname";
    final String CLASS = "Class";

    SharedPreferences sharedPreferences;

    EditText personName;
    EditText personSurname;
    EditText personClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button buttonStart = findViewById(R.id.buttonStart);

        personName = findViewById(R.id.editTextTextPersonName);
        personSurname = findViewById(R.id.editTextTextPersonSurname);
        personClass = findViewById(R.id.editTextTextPersonClass);

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
                Intent intent = new Intent(MainActivity.this, Menu.class);
                startActivity(intent);
                finish();
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
        Toast.makeText(MainActivity.this, "Данные сохранены", Toast.LENGTH_SHORT).show();
    }
}