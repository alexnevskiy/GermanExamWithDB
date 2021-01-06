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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public class MainActivity extends AppCompatActivity {

    final String NAME = "Name";
    final String SURNAME = "Surname";
    final String CLASS = "Class";
    final String JSON = "Json";

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

    public String readJSONFromRaw() {
        String json;
        try {
            InputStream is = getResources().openRawResource(R.raw.german_variants);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        sharedPreferences = getSharedPreferences("StudentData", MODE_PRIVATE);
        String name = sharedPreferences.getString(NAME, "");
        String surname = sharedPreferences.getString(SURNAME, "");
        String classPerson = sharedPreferences.getString(CLASS, "");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(JSON, readJSONFromRaw());
        editor.apply();

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
                if (personName.getText().toString().equals("")
                        || personSurname.getText().toString().equals("")
                        || personClass.getText().toString().equals("")) {
                    if (personName.getText().toString().equals("")) {
                        personName.setError("Введите имя");
                    }
                    if (personSurname.getText().toString().equals("")) {
                        personSurname.setError("Введите фамилию");
                    }
                    if (personClass.getText().toString().equals("")) {
                        personClass.setError("Введите класс");
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
        sharedPreferences = getSharedPreferences("StudentData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(NAME, personName.getText().toString());
        editor.putString(SURNAME, personSurname.getText().toString());
        editor.putString(CLASS, personClass.getText().toString());
        editor.putString(JSON, readJSONFromRaw());
        editor.apply();
        Toast.makeText(MainActivity.this, "Данные сохранены", Toast.LENGTH_SHORT).show();
    }
}