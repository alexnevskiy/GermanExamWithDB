package com.example.germanexam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Variants extends AppCompatActivity {

    final String VARIANT = "Variant";

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.variants);

        defineButtons();
    }

    public void defineButtons() {
        int rows = 5;
        int columns = 5;

        TableLayout tableLayout = findViewById(R.id.variants_layout);

        for (int i = 0; i < rows; i++) {

            TableRow tableRow = new TableRow(this);
            TableRow.LayoutParams paramsTable = new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            tableRow.setLayoutParams(paramsTable);

            for (int j = 0; j < columns; j++) {
                TableRow.LayoutParams paramsButton = new TableRow.LayoutParams(
                        TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                paramsButton.weight = 1.0f;
                paramsButton.topMargin = 20;
                paramsButton.leftMargin = 20;
                paramsButton.rightMargin = 20;
                paramsButton.bottomMargin = 20;
                final Button button = new Button(this);
                button.setLayoutParams(paramsButton);
                button.setText("" + (j + 1 + (i * rows)));
                button.setId(j + 1 + (i * rows));
                button.setBackground(getResources().getDrawable(R.drawable.button_blue));
                button.setTextSize(30);
                button.setTextColor(Color.parseColor("#FFFFFF"));
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sharedPreferences = getSharedPreferences("StudentData", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(VARIANT, button.getText().toString());
                        editor.apply();
                        Intent intent = new Intent(Variants.this, VariantStartPage.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                    }
                });

                tableRow.addView(button, j);
            }

            tableLayout.addView(tableRow, i);
        }
    }
}