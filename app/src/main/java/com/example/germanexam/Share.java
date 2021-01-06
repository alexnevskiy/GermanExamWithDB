package com.example.germanexam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static androidx.core.content.FileProvider.getUriForFile;

public class Share extends AppCompatActivity {
    final String TASK1 = "Task1";
    final String TASK2 = "Task2";
    final String TASK3 = "Task3";
    final String TASK4 = "Task4";

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share);

        Button buttonShare = findViewById(R.id.share);

        buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences = getSharedPreferences("StudentData", MODE_PRIVATE);
                String audioPath1 = sharedPreferences.getString(TASK1, "");
                String audioPath2 = sharedPreferences.getString(TASK2, "");
                String audioPath3 = sharedPreferences.getString(TASK3, "");
                String audioPath4 = sharedPreferences.getString(TASK4, "");
                File audio1 = new File(audioPath1);
                File audio2 = new File(audioPath2);
                File audio3 = new File(audioPath3);
                File audio4 = new File(audioPath4);
                Uri audioUri1 = getUriForFile(Share.this, "com.example.germanexam.fileprovider", audio1);
                Uri audioUri2 = getUriForFile(Share.this, "com.example.germanexam.fileprovider", audio2);
                Uri audioUri3 = getUriForFile(Share.this, "com.example.germanexam.fileprovider", audio3);
                Uri audioUri4 = getUriForFile(Share.this, "com.example.germanexam.fileprovider", audio4);

                ArrayList<Uri> imageUris = new ArrayList<>();
                imageUris.add(audioUri1);
                imageUris.add(audioUri2);
                imageUris.add(audioUri3);
                imageUris.add(audioUri4);

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
                shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
                shareIntent.setType("video/mp4");

                Intent chooser = Intent.createChooser(shareIntent, "Share File");
                List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    for (Uri uri : imageUris) {
                        grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                }

                startActivity(chooser);
            }
        });

        Button buttonMainMenu = findViewById(R.id.to_main_menu);

        buttonMainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Share.this, Menu.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(Share.this, "Для выхода нажмите на \"Главное меню\"", Toast.LENGTH_SHORT).show();
    }
}