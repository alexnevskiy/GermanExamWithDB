package com.example.germanexam.taskdata;

public class Task2 {
    private final String title;
    private final String questions;
    private final byte[] imageByteArray;
    private String imagePath = null;
    private final String imageText;

    public Task2(String title, String questions, byte[] imageByteArray, String imageText) {
        this.title = title;
        this.questions = questions;
        this.imageByteArray = imageByteArray;
        this.imageText = imageText;
    }

    public Task2(String title, String questions, String imagePath, String imageText) {
        this.title = title;
        this.questions = questions;
        this.imageByteArray = null;
        this.imagePath = imagePath;
        this.imageText = imageText;
    }

    public Task2(String title, String questions, String imageText) {
        this.title = title;
        this.questions = questions;
        this.imageByteArray = null;
        this.imageText = imageText;
    }

    public String getTitle() {
        return this.title;
    }

    public String getQuestions() {
        return this.questions;
    }

    public byte[] getImageByteArray() {
        return this.imageByteArray;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImageText() {
        return this.imageText;
    }
}
