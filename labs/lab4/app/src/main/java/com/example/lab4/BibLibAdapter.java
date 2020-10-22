package com.example.lab4;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import name.ank.lab4.BibDatabase;
import name.ank.lab4.BibEntry;
import name.ank.lab4.Keys;

public class BibLibAdapter extends RecyclerView.Adapter<BibLibAdapter.BibLibViewHolder> {

    BibDatabase database;

    BibLibAdapter(InputStream publications) throws IOException {
        InputStreamReader reader = new InputStreamReader(publications);
        database = new BibDatabase(reader);
    }

    @NonNull
    @Override
    public BibLibViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.biblib_entry, parent, false);

        return new BibLibViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BibLibViewHolder holder, int position) {
        BibEntry entry = database.getEntry(position);
        holder.textViewType.setText(entry.getType().name());
        holder.textViewTitle.setText(entry.getField(Keys.TITLE));
        holder.textViewAuthor.setText(entry.getField(Keys.AUTHOR));
        holder.textViewYear.setText(entry.getField(Keys.YEAR));
    }

    @Override
    public int getItemCount() {
        return database.size();
    }

    static class BibLibViewHolder extends RecyclerView.ViewHolder {

        TextView textViewType;
        TextView textViewTitle;
        TextView textViewAuthor;
        TextView textViewYear;

        public BibLibViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewType = itemView.findViewById(R.id.type);
            textViewTitle = itemView.findViewById(R.id.title);
            textViewAuthor = itemView.findViewById(R.id.author);
            textViewYear = itemView.findViewById(R.id.year);
        }
    }
}
