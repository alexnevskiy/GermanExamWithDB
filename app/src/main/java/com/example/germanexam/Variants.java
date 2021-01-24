package com.example.germanexam;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class Variants extends AppCompatActivity {

    final String VARIANT = "Variant";
    final String JSON = "Json";

    SharedPreferences sharedPreferences;

    List<Button> buttonList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.variants);

        defineButtons();

        Button buttonReset = findViewById(R.id.reset_button);

        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Variants.this);
                builder.setTitle(R.string.reset_title);
                sharedPreferences = getSharedPreferences("StudentData", MODE_PRIVATE);
                builder.setPositiveButton(R.string.yes_rus, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        for (Button button : buttonList) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean(VARIANT + button.getText(), false);
                            editor.apply();
                            button.setBackground(getResources().getDrawable(R.drawable.button_blue));
                        }
                    }
                });
                builder.setNegativeButton(R.string.no_rus, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    public void defineButtons() {
        sharedPreferences = getSharedPreferences("StudentData", MODE_PRIVATE);
        String json = sharedPreferences.getString(JSON, "");

        JsonParser jsonParser = new JsonParser(json);
        final int variantsNumber = jsonParser.getVariantsNumber();

        int rows = 5;
        int columns = variantsNumber / 5;

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
                int number = j + 1 + (i * rows);
                button.setText("" + number);
                button.setId(number);
                boolean isFinished = sharedPreferences.getBoolean(VARIANT + number, false);
                if (isFinished) {
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

                buttonList.add(button);
                tableRow.addView(button, j);
            }

            tableLayout.addView(tableRow, i);
        }
    }
}