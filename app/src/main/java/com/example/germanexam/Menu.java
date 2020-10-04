package com.example.germanexam;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        Button buttonVariants = (Button) findViewById(R.id.button_variants);

        buttonVariants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Menu.this, Variants.class);
                    startActivity(intent);
                } catch (Exception e) {

                }
            }
        });

        Button buttonExam = (Button) findViewById(R.id.button_exam);

        buttonExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Menu.this, VariantStartPage.class);
                    startActivity(intent);
                } catch (Exception e) {

                }
            }
        });

        Button buttonSettings = (Button) findViewById(R.id.button_settings);

        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Menu.this, Settings.class);
                    startActivity(intent);
                } catch (Exception e) {

                }
            }
        });
    }
}