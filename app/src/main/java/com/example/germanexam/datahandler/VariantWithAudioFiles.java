package com.example.germanexam.datahandler;

public class VariantWithAudioFiles {
    private final int id;
    private final int variant;
    private final String[] audioFiles;

    public VariantWithAudioFiles(int id, int variant, String[] audioFiles) {
        this.id = id;
        this.variant = variant;
        this.audioFiles = audioFiles;
    }

    public int getId() {
        return id;
    }

    public int getVariant() {
        return variant;
    }

    public String[] getAudioFiles() {
        return audioFiles;
    }
}
