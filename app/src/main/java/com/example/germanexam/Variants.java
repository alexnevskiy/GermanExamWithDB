package com.example.germanexam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

import androidx.appcompat.app.AppCompatActivity;

import com.example.germanexam.database.Database;

import java.util.ArrayList;
import java.util.List;

import static com.example.germanexam.constants.Constants.*;

public class Variants extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    List<Button> buttonList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.variants);

        defineButtons();
    }

    public void defineButtons() {
        sharedPreferences = getSharedPreferences("StudentData", MODE_PRIVATE);

        final int variantsNumber = Database.getVariantsNumber();
        int rows;
        int columns;

        if (variantsNumber % 5 == 0) {
            rows = variantsNumber / 5;
        } else {
            rows = variantsNumber / 5 + 1;
        }
        columns = 5;

        TableLayout tableLayout = findViewById(R.id.variants_layout);

        int userId = sharedPreferences.getInt(USER_ID, 0);
        List<Integer> solvedVariants = Database.getSolvedVariants(userId);

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
                int number = j + 1 + (i * 5);
                button.setText("" + number);
                button.setId(number);
                if (solvedVariants.contains(number)) {
                    button.setBackground(getResources().getDrawable(R.drawable.button_green_fade));
                } else {
                    button.setBackground(getResources().getDrawable(R.drawable.button_blue));
                }
                button.setTextSize(30);
                button.setTextColor(Color.parseColor("#FFFFFF"));
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(VARIANT, Integer.parseInt(button.getText().toString()));
                        editor.apply();
                        Intent intent = new Intent(Variants.this, VariantStartPage.class);
                        startActivity(intent);
                        finish();
                    }
                });
                if (number > variantsNumber) {
                    button.setVisibility(View.INVISIBLE);
                }

                buttonList.add(button);
                tableRow.addView(button, j);
            }

            tableLayout.addView(tableRow, i);
        }
    }
}