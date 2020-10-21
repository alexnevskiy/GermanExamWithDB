package com.example.lab4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.io.IOException;
import java.io.InputStream;

import name.ank.lab4.BibDatabase;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BibLibAdapter bibLibAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InputStream publications = getResources().openRawResource(R.raw.publications_ferro_en);

        recyclerView = findViewById(R.id.recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setHasFixedSize(true);

        try {
            bibLibAdapter = new BibLibAdapter(publications);
        } catch (IOException e) {
            e.printStackTrace();
        }
        recyclerView.setAdapter(bibLibAdapter);
    }
}