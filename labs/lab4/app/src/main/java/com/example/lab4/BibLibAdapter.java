package com.example.lab4;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
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
        BibEntry entry = database.getEntry(position % database.size());
        holder.textViewType.setText(entry.getType().name());
        switch (entry.getType()) {
            case ARTICLE:
                holder.textViewType.setBackground(ResourcesCompat.getDrawable(holder.textViewType.getResources(), R.color.ARTICLE, null));
                break;
            case BOOK:
                holder.textViewType.setBackground(ResourcesCompat.getDrawable(holder.textViewType.getResources(), R.color.BOOK, null));
                break;
            case BOOKLET:
                holder.textViewType.setBackground(ResourcesCompat.getDrawable(holder.textViewType.getResources(), R.color.BOOKLET, null));
                break;
            case INBOOK:
                holder.textViewType.setBackground(ResourcesCompat.getDrawable(holder.textViewType.getResources(), R.color.INBOOK, null));
                break;
            case INCOLLECTION:
                holder.textViewType.setBackground(ResourcesCompat.getDrawable(holder.textViewType.getResources(), R.color.INCOLLECTION, null));
                break;
            case INPROCEEDINGS:
                holder.textViewType.setBackground(ResourcesCompat.getDrawable(holder.textViewType.getResources(), R.color.INPROCEEDINGS, null));
                break;
            case MANUAL:
                holder.textViewType.setBackground(ResourcesCompat.getDrawable(holder.textViewType.getResources(), R.color.MANUAL, null));
                break;
            case MASTERSTHESIS:
                holder.textViewType.setBackground(ResourcesCompat.getDrawable(holder.textViewType.getResources(), R.color.MASTERSTHESIS, null));
                break;
            case MISC:
                holder.textViewType.setBackground(ResourcesCompat.getDrawable(holder.textViewType.getResources(), R.color.MISC, null));
                break;
            case PHDTHESIS:
                holder.textViewType.setBackground(ResourcesCompat.getDrawable(holder.textViewType.getResources(), R.color.PHDTHESIS, null));
                break;
            case PROCEEDINGS:
                holder.textViewType.setBackground(ResourcesCompat.getDrawable(holder.textViewType.getResources(), R.color.PROCEEDINGS, null));
                break;
            case TECHREPORT:
                holder.textViewType.setBackground(ResourcesCompat.getDrawable(holder.textViewType.getResources(), R.color.TECHREPORT, null));
                break;
            case UNPUBLISHED:
                holder.textViewType.setBackground(ResourcesCompat.getDrawable(holder.textViewType.getResources(), R.color.UNPUBLISHED, null));
                break;
            case SOFTWARE:
                holder.textViewType.setBackground(ResourcesCompat.getDrawable(holder.textViewType.getResources(), R.color.SOFTWARE, null));
                break;
            case EDITORIAL:
                holder.textViewType.setBackground(ResourcesCompat.getDrawable(holder.textViewType.getResources(), R.color.EDITORIAL, null));
                break;
            default:
                holder.textViewType.setBackground(ResourcesCompat.getDrawable(holder.textViewType.getResources(), R.color.colorPrimaryDark, null));
        }
        holder.textViewTitle.setText(entry.getField(Keys.TITLE));
        holder.textViewAuthor.setText(entry.getField(Keys.AUTHOR));
        holder.textViewYear.setText(entry.getField(Keys.YEAR));
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
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
