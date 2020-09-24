package com.example.germanexam;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import androidx.appcompat.app.AppCompatActivity;

public class Variants extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.variants);

        defineButtons();
    }

    public void defineButtons() {
        int rows = 5;
        int columns = 5;

        TableLayout tableLayout = (TableLayout) findViewById(R.id.variants_layout);

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
                Button button = new Button(this);
                button.setLayoutParams(paramsButton);
                button.setText("" + (j + 1 + (i * rows)));
                button.setId(j + 1 + (i * rows));
                button.setBackground(getResources().getDrawable(R.drawable.button_blue));
                button.setTextSize(30);
                button.setTextColor(Color.parseColor("#FFFFFF"));
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent intent = new Intent(Variants.this, VariantStartPage.class);
                            startActivity(intent);
                        } catch (Exception e) {

                        }
                    }
                });

                tableRow.addView(button, j);
            }

            tableLayout.addView(tableRow, i);
        }
    }
}