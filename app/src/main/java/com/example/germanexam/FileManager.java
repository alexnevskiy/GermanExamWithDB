package com.example.germanexam;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileManager extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FileManagerAdapter fileManagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_manager);

        String path = getFilesDir().toString() + "/audio";
        File directory = new File(path);
        File[] files = directory.listFiles();
        String[] variant = new String[4];
        List<String[]> audios = new ArrayList<>();
        List<String> names = new ArrayList<>();
        for (File file : files) {
            if (names.contains(file.getName())) continue;

            String fileNameWithMp3 = file.getName();
            String[] words = fileNameWithMp3.substring(0, fileNameWithMp3.length() - 4).split("_");
            String fileName = fileNameWithMp3.substring(0,
                    fileNameWithMp3.length() - (7 + words[5].length() + words[4].length()));
            File audio1 = new File(path + "/" + fileName + "1_Variant_" + words[5] + ".mp3");
            File audio2 = new File(path + "/" + fileName + "2_Variant_" + words[5] + ".mp3");
            File audio3 = new File(path + "/" + fileName + "3_Variant_" + words[5] + ".mp3");
            File audio4 = new File(path + "/" + fileName + "4_Variant_" + words[5] + ".mp3");
            if (audio1.exists() && audio2.exists() && audio3.exists() && audio4.exists()) {
                variant[0] = audio1.getAbsolutePath();
                variant[1] = audio2.getAbsolutePath();
                variant[2] = audio3.getAbsolutePath();
                variant[3] = audio4.getAbsolutePath();
                audios.add(variant);
                variant = new String[4];
                names.add(audio1.getName());
                names.add(audio2.getName());
                names.add(audio3.getName());
                names.add(audio4.getName());
            } else {
                boolean delete1 = audio1.delete();
                boolean delete2 = audio2.delete();
                boolean delete3 = audio3.delete();
                boolean delete4 = audio4.delete();
                Log.i("FileManager", "audio1 with name " + audio1.getName() + " is deleted: " + delete1);
                Log.i("FileManager", "audio2 with name " + audio2.getName() + " is deleted: " + delete2);
                Log.i("FileManager", "audio3 with name " + audio3.getName() + " is deleted: " + delete3);
                Log.i("FileManager", "audio4 with name " + audio4.getName() + " is deleted: " + delete4);
            }
        }

        recyclerView = findViewById(R.id.recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setHasFixedSize(true);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(itemDecoration);

        fileManagerAdapter = new FileManagerAdapter(audios, this);
        recyclerView.setAdapter(fileManagerAdapter);
    }

    @Override
    protected void onPause() {
        if (fileManagerAdapter.whoPlayed != -1) {
            fileManagerAdapter.whoPlayed = -1;
        }
        super.onPause();
    }
}