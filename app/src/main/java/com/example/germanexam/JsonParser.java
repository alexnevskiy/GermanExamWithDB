package com.example.germanexam;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonParser {
    JSONObject variant;
    JSONObject task1;
    JSONObject task2;
    JSONObject task3;
    JSONObject task4;

    JsonParser(String jsonString, int variantNumber) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray("variants");
            variant = jsonArray.getJSONObject(variantNumber - 1);
            task1 = variant.getJSONObject("task1");
            task2 = variant.getJSONObject("task2");
            task3 = variant.getJSONObject("task3");
            task4 = variant.getJSONObject("task4");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getTask1Text() {
        String task1Text = null;
        try {
            task1Text =  task1.getString("text");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return task1Text;
    }

    public String getTask2Title() {
        String task2Title = null;
        try {
            task2Title =  task2.getString("title");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return task2Title;
    }

    public String getTask2Questions() {
        String task2Questions = null;
        try {
            task2Questions =  task2.getString("questions");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return task2Questions;
    }

    public String getTask2Picture() {
        String task2Picture = null;
        try {
            task2Picture =  task2.getString("picture");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return task2Picture;
    }

    public String getTask2Question1() {
        String task2Question1 = null;
        try {
            task2Question1 =  task2.getString("question1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return task2Question1;
    }

    public String getTask2Question2() {
        String task2Question2 = null;
        try {
            task2Question2 =  task2.getString("question2");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return task2Question2;
    }

    public String getTask2Question3() {
        String task2Question3 = null;
        try {
            task2Question3 =  task2.getString("question3");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return task2Question3;
    }

    public String getTask2Question4() {
        String task2Question4 = null;
        try {
            task2Question4 =  task2.getString("question4");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return task2Question4;
    }

    public String getTask2Question5() {
        String task2Question5 = null;
        try {
            task2Question5 =  task2.getString("question5");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return task2Question5;
    }

    public String getTask2PictureText() {
        String task2PictureText = null;
        try {
            task2PictureText =  task2.getString("pictureText");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return task2PictureText;
    }

    public String getTask3Questions() {
        String task3Questions = null;
        try {
            task3Questions =  task3.getString("questions");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return task3Questions;
    }

    public String getTask3Picture1() {
        String task3Picture1 = null;
        try {
            task3Picture1 =  task3.getString("picture1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return task3Picture1;
    }

    public String getTask3Picture2() {
        String task3Picture2 = null;
        try {
            task3Picture2 =  task3.getString("picture2");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return task3Picture2;
    }

    public String getTask3Picture3() {
        String task3Picture3 = null;
        try {
            task3Picture3 =  task3.getString("picture3");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return task3Picture3;
    }

    public String getTask4Questions() {
        String task4Questions = null;
        try {
            task4Questions =  task4.getString("questions");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return task4Questions;
    }

    public String getTask4Picture1() {
        String task4Picture1 = null;
        try {
            task4Picture1 =  task4.getString("picture1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return task4Picture1;
    }

    public String getTask4Picture2() {
        String task4Picture2 = null;
        try {
            task4Picture2 =  task4.getString("picture2");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return task4Picture2;
    }
}
