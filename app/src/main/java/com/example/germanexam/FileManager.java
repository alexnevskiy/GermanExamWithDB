package com.example.germanexam;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.germanexam.database.Database;
import com.example.germanexam.datahandler.VariantWithAudioFiles;

import java.io.File;
import java.util.List;

import static com.example.germanexam.constants.Constants.*;

public class FileManager extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FileManagerAdapter fileManagerAdapter;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_manager);

        sharedPreferences = getSharedPreferences("StudentData", MODE_PRIVATE);
        int userId = sharedPreferences.getInt(USER_ID, 0);

        String path = getFilesDir().toString() + "/audio";
        File directory = new File(path);
        File[] files = directory.listFiles();

        List<String> fileNames = Database.getAudioFiles();
        String[] userInfo = Database.getUserInfo(userId);
        List<VariantWithAudioFiles> userAudios = Database.getSolvedVariantsWithAudioFiles(userId, path + "/");

        for (File file : files) {
            if (!fileNames.contains(file.getName())) {
                boolean delete = file.delete();
                Log.i("FileManager", "Audio with name " + file.getName() + " is deleted: " + delete);
            }
        }

        recyclerView = findViewById(R.id.recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setHasFixedSize(true);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(itemDecoration);

        fileManagerAdapter = new FileManagerAdapter(userAudios, userInfo,  this);
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