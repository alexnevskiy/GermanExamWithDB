package com.example.germanexam;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class VariantStartPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.variant_one_start);
        Button buttonVariants = (Button) findViewById(R.id.button_start_test);

        buttonVariants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(VariantStartPage.this, TaskOne.class);
                    startActivity(intent);
                } catch (Exception e) {

                }
            }
        });
    }
}