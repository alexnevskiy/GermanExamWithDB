package com.example.germanexam.taskdata;

public class Task3 {
    private final String questions;
    private final byte[] imageByteArray1;
    private final byte[] imageByteArray2;
    private final byte[] imageByteArray3;
    private String imagePath1;
    private String imagePath2;
    private String imagePath3;

    public Task3(String questions, byte[] imageByteArray1, byte[] imageByteArray2, byte[] imageByteArray3) {
        this.questions = questions;
        this.imageByteArray1 = imageByteArray1;
        this.imageByteArray2 = imageByteArray2;
        this.imageByteArray3 = imageByteArray3;
    }

    public Task3(String questions, String imagePath1, String imagePath2, String imagePath3) {
        this.questions = questions;
        this.imageByteArray1 = null;
        this.imageByteArray2 = null;
        this.imageByteArray3 = null;
        this.imagePath1 = imagePath1;
        this.imagePath2 = imagePath2;
        this.imagePath3 = imagePath3;
    }

    public Task3(String questions) {
        this.questions = questions;
        this.imageByteArray1 = null;
        this.imageByteArray2 = null;
        this.imageByteArray3 = null;
    }

    public String getQuestions() {
        return this.questions;
    }

    public byte[] getImageByteArray1() {
        return this.imageByteArray1;
    }

    public byte[] getImageByteArray2() {
        return this.imageByteArray2;
    }

    public byte[] getImageByteArray3() {
        return this.imageByteArray3;
    }

    public String getImagePath1() {
        return imagePath1;
    }

    public void setImagePath1(String imagePath1) {
        this.imagePath1 = imagePath1;
    }

    public String getImagePath2() {
        return imagePath2;
    }

    public void setImagePath2(String imagePath2) {
        this.imagePath2 = imagePath2;
    }

    public String getImagePath3() {
        return imagePath3;
    }

    public void setImagePath3(String imagePath3) {
        this.imagePath3 = imagePath3;
    }
}
