package com.example.germanexam;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.germanexam.database.Database;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static androidx.core.content.FileProvider.getUriForFile;

import static com.example.germanexam.constants.Constants.*;

public class Share extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share);

        sharedPreferences = getSharedPreferences("StudentData", MODE_PRIVATE);

        AlertDialog.Builder builder = new AlertDialog.Builder(Share.this);
        LayoutInflater inflater = Share.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.rating_variant, null);
        builder.setView(dialogView);
        final RatingBar ratingBar = dialogView.findViewById(R.id.ratingBar);
        builder.setPositiveButton(R.string.rate, new DialogInterface.OnClickListener() {
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
                int rating = (int) ratingBar.getRating();
                int userId = sharedPreferences.getInt(USER_ID, 0);
                int variant = sharedPreferences.getInt(VARIANT, 0);
                Database.insertFeedback(rating, userId, variant);
                dialog.dismiss();
            }
        });

        Button buttonShare = findViewById(R.id.share);

        buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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