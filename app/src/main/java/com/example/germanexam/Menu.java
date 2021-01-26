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

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class Menu extends AppCompatActivity {
    TextView studentName;
    TextView studentClass;

    private long backPressedTime;
    private Toast backToast;

    final String NAME = "Name";
    final String SURNAME = "Surname";
    final String CLASS = "Class";
    final String VARIANT = "Variant";
    final String RESTART = "Restart";
    final String JSON = "Json";

    SharedPreferences sharedPreferences;

    EditText personName;
    EditText personSurname;
    EditText personClass;

    String json;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        studentName = findViewById(R.id.student_name);
        studentClass = findViewById(R.id.person_class);

        sharedPreferences = getSharedPreferences("StudentData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        json = readJSONFromRaw();
        editor.putString(JSON, json);
        editor.apply();

        loadData();

        JsonParser jsonParser = new JsonParser(json);
        final int variantsNumber = jsonParser.getVariantsNumber();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_map:
                        AlertDialog.Builder builder = new AlertDialog.Builder(Menu.this);
                        LayoutInflater inflater = Menu.this.getLayoutInflater();
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

        Button buttonExam = findViewById(R.id.button_exam);

        buttonExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random random = new Random();
                sharedPreferences = getSharedPreferences("StudentData", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(VARIANT, random.nextInt(variantsNumber - 1) + 1);
                editor.apply();
                Intent intent = new Intent(Menu.this, VariantStartPage.class);
                startActivity(intent);
            }
        });

        Button buttonVariants = findViewById(R.id.button_variants);

        buttonVariants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Menu.this, Variants.class);
                startActivity(intent);
            }
        });

        Button buttonSettings = findViewById(R.id.button_settings);

        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Menu.this, Settings.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        sharedPreferences = getSharedPreferences("StudentData", MODE_PRIVATE);
        boolean restart = sharedPreferences.getBoolean(RESTART, false);
        if (restart) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(RESTART, false);
            editor.apply();

            Intent intent = new Intent(Menu.this, Restart.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            return;
        } else {
            backToast = Toast.makeText(getBaseContext(), "Нажмите ещё раз, чтобы выйти", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }

    private void saveData() {
        sharedPreferences = getSharedPreferences("StudentData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(NAME, personName.getText().toString());
        editor.putString(SURNAME, personSurname.getText().toString());
        editor.putString(CLASS, personClass.getText().toString());
        editor.apply();
        Toast.makeText(Menu.this, "Данные сохранены", Toast.LENGTH_SHORT).show();
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
}