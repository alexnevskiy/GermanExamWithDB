package com.example.germanexam;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

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
        ArrayList<String[]> audios = new ArrayList<>();
        for (int i = 0; i < files.length; i += 4) {
            String fileName1 = files[i].getAbsolutePath();
            String fileName2 = files[i + 1].getAbsolutePath();
            String fileName3 = files[i + 2].getAbsolutePath();
            String fileName4 = files[i + 3].getAbsolutePath();
            variant[0] = fileName1;
            variant[1] = fileName2;
            variant[2] = fileName3;
            variant[3] = fileName4;
            audios.add(variant);
            variant = new String[4];
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