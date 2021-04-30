package com.example.germanexam.cache;

import android.util.Log;

import com.example.germanexam.MyApplication;
import com.example.germanexam.database.Database;
import com.example.germanexam.taskdata.Task2;
import com.example.germanexam.taskdata.Task3;
import com.example.germanexam.taskdata.Task4;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CacheDatabase {

    public static Task2 getTask2(boolean cache, int variant) {
        Task2 task2;
        if (cache) {
            String cachePath = MyApplication.getAppContext().getCacheDir().getAbsolutePath();
            String filename = "variant" + variant + "_2.png";
            String imagePath = cachePath + "/" + filename;
            File image = new File(imagePath);
            boolean fileExists = image.exists();

            if (fileExists) {
                String sql = "SELECT title, questions, image_text" +
                        " FROM task2 WHERE variant_id = " + variant;
                task2 = Database.getTask2(true, true, variant, sql);
                task2.setImagePath(imagePath);

                Log.i("CacheDatabase", "Image in Task2 exists in cache");
            } else {
                String sql = "SELECT title, questions, image, image_text" +
                        " FROM task2 WHERE variant_id = " + variant;
                task2 = Database.getTask2(false, true, variant, sql);

                Log.i("CacheDatabase", "Image in Task2 not exists in cache");
            }
        } else {
            String sql = "SELECT title, questions, image, image_text" +
                    " FROM task2 WHERE variant_id = " + variant;
            task2 = Database.getTask2(false, false, variant, sql);
            byte[] imageByte = task2.getImageByteArray();
            String imagePath = byteArrayToCacheFile(imageByte, variant, 2);
            task2.setImagePath(imagePath);

            Log.i("CacheDatabase", "Image in Task2 was cached");
        }

        return task2;
    }

    public static Task3 getTask3(boolean cache, int variant) {
        Task3 task3;

        if (cache) {
            String cachePath = MyApplication.getAppContext().getCacheDir().getAbsolutePath();

            String filename1 = "variant" + variant + "_3_1.png";
            String filename2 = "variant" + variant + "_3_2.png";
            String filename3 = "variant" + variant + "_3_3.png";

            String imagePath1 = cachePath + "/" + filename1;
            String imagePath2 = cachePath + "/" + filename2;
            String imagePath3 = cachePath + "/" + filename3;

            File image1 = new File(imagePath1);
            File image2 = new File(imagePath2);
            File image3 = new File(imagePath3);

            boolean filesExists = image1.exists() && image2.exists() && image3.exists();

            if (filesExists) {
                String sql = "SELECT questions" +
                        " FROM task3 WHERE variant_id = " + variant;
                task3 = Database.getTask3(true, true, variant, sql);

                task3.setImagePath1(imagePath1);
                task3.setImagePath2(imagePath2);
                task3.setImagePath3(imagePath3);

                Log.i("CacheDatabase", "Images in Task3 exists in cache");
            } else {
                String sql = "SELECT questions, image1, image2, image3" +
                        " FROM task3 WHERE variant_id = " + variant;
                task3 = Database.getTask3(false, true, variant, sql);

                Log.i("CacheDatabase", "Images in Task3 not exists in cache");
            }
        } else {
            String sql = "SELECT questions, image1, image2, image3" +
                    " FROM task3 WHERE variant_id = " + variant;
            task3 = Database.getTask3(false, false, variant, sql);

            byte[] imageByte1 = task3.getImageByteArray1();
            byte[] imageByte2 = task3.getImageByteArray2();
            byte[] imageByte3 = task3.getImageByteArray3();

            String imagePath1 = byteArrayToCacheFile(imageByte1, variant, 3, 1);
            String imagePath2 = byteArrayToCacheFile(imageByte2, variant, 3, 2);
            String imagePath3 = byteArrayToCacheFile(imageByte3, variant, 3, 3);

            task3.setImagePath1(imagePath1);
            task3.setImagePath2(imagePath2);
            task3.setImagePath3(imagePath3);

            Log.i("CacheDatabase", "Images in Task3 was cached");
        }

        return task3;
    }

    public static Task4 getTask4(boolean cache, int variant) {
        Task4 task4;

        if (cache) {
            String cachePath = MyApplication.getAppContext().getCacheDir().getAbsolutePath();

            String filename1 = "variant" + variant + "_4_1.png";
            String filename2 = "variant" + variant + "_4_2.png";

            String imagePath1 = cachePath + "/" + filename1;
            String imagePath2 = cachePath + "/" + filename2;

            File image1 = new File(imagePath1);
            File image2 = new File(imagePath2);

            boolean filesExists = image1.exists() && image2.exists();

            if (filesExists) {
                String sql = "SELECT questions" +
                        " FROM task4 WHERE variant_id = " + variant;
                task4 = Database.getTask4(true, true, variant, sql);
                task4.setImagePath1(imagePath1);
                task4.setImagePath2(imagePath2);

                Log.i("CacheDatabase", "Images in Task4 exists in cache");
            } else {
                String sql = "SELECT questions, image1, image2" +
                        " FROM task4 WHERE variant_id = " + variant;
                task4 = Database.getTask4(false, true, variant, sql);

                Log.i("CacheDatabase", "Images in Task4 not exists in cache");
            }
        } else {
            String sql = "SELECT questions, image1, image2" +
                    " FROM task4 WHERE variant_id = " + variant;
            task4 = Database.getTask4(false, false, variant, sql);

            byte[] imageByte1 = task4.getImageByteArray1();
            byte[] imageByte2 = task4.getImageByteArray2();

            String imagePath1 = byteArrayToCacheFile(imageByte1, variant, 4, 1);
            String imagePath2 = byteArrayToCacheFile(imageByte2, variant, 4, 2);

            task4.setImagePath1(imagePath1);
            task4.setImagePath2(imagePath2);

            Log.i("CacheDatabase", "Images in Task4 was cached");
        }

        return task4;
    }

    public static String byteArrayToCacheFile(byte[] bytes, int variant, int task) {
        String filename = "variant" + variant + "_" + task + ".png";
        String cacheDirPath = MyApplication.getAppContext().getCacheDir().getAbsolutePath();
        String fileDestination = cacheDirPath + "/" + filename;

        Log.i("CacheDatabase", "Byte array filename: " + filename);

        return writeInFile(fileDestination, bytes);
    }

    public static String byteArrayToCacheFile(byte[] bytes, int variant, int task, int image) {
        String filename = "variant" + variant + "_" + task + "_" + image + ".png";
        String cacheDirPath = MyApplication.getAppContext().getCacheDir().getAbsolutePath();
        String fileDestination = cacheDirPath + "/" + filename;

        Log.i("CacheDatabase", "Byte array filename: " + filename);

        return writeInFile(fileDestination, bytes);
    }

    private static String writeInFile(String fileDestination, byte[] bytes) {
        try {
            FileOutputStream stream = new FileOutputStream(fileDestination);
            stream.write(bytes);
            stream.close();
        } catch (FileNotFoundException e) {
            Log.e("CacheDatabase", "Unable to create a file", e);
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("CacheDatabase", "Unable to write an array byte to a file", e);
            e.printStackTrace();
        }

        return fileDestination;
    }
}
