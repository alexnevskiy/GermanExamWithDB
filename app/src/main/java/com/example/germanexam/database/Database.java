package com.example.germanexam.database;

import android.util.Log;

import com.example.germanexam.cache.CacheDatabase;
import com.example.germanexam.datahandler.VariantWithAudioFiles;
import com.example.germanexam.taskdata.Task1;
import com.example.germanexam.taskdata.Task2;
import com.example.germanexam.taskdata.Task3;
import com.example.germanexam.taskdata.Task4;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static final String user = "\"user\"";
    private static final String solved_variant = "solved_variant";
    private static final String audiofile = "audiofile";
    private static final String feedback = "feedback";

    static final String DB_PATH = "jdbc:postgresql://192.168.1.40:5432/germanexam";
    static final String userName = "alex_nevskiy";
    static final String password = "123";
    static final String DRIVER = "org.postgresql.Driver";

    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName(DRIVER);
            conn = DriverManager.getConnection(DB_PATH, userName, password);
        } catch (SQLException e) {
            Log.e("Database", "Unable to connect to the database", e);
        } catch (ClassNotFoundException e) {
            Log.e("Database", "Driver for PostgreSQL missing", e);
        }
        return conn;
    }

    public static String[] getUserInfo(int id) {
        final String sql = "SELECT name, surname, class FROM " + user + " WHERE id = " + id;
        final String[] userInfo = new String[3];

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try (Connection connection = getConnection();
                     Statement statement = connection.createStatement();
                     ResultSet resultSet = statement.executeQuery(sql)) {
                    while (resultSet.next()) {
                        userInfo[0] = resultSet.getString("name");
                        userInfo[1] = resultSet.getString("surname");
                        userInfo[2] = resultSet.getString("class");
                    }
                } catch (SQLException throwables) {
                    Log.e("Database", "Unable to connect to the database when " +
                            "receiving user information", throwables);
                }
            }
        });

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            Log.e("Database", "The thread was interrupted until the database was sent", e);
        }

        return userInfo;
    }

    public static int insertUser(final String[] userInfo) {
        final int[] id = {0};
        final String sql = "SELECT id FROM " + user + "WHERE " +
                "name = '" + userInfo[0] + "' AND surname = '" + userInfo[1] +
                "' AND class = '" + userInfo[2] + "'";

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try (Connection connection = getConnection()) {
                    try (Statement statement = connection.createStatement()) {
                        statement.execute("INSERT INTO " + user + "(name, surname, class) " +
                                "SELECT * FROM (SELECT '" + userInfo[0] + "', '" + userInfo[1] + "', '"
                                + userInfo[2] + "') AS tmp WHERE NOT EXISTS (" +
                                "SELECT name, surname, class FROM " + user + "WHERE " +
                                "name = '" + userInfo[0] + "' AND surname = '" + userInfo[1] +
                                "' AND class = '" + userInfo[2] + "') LIMIT 1");
                    }

                    try (Statement statement = connection.createStatement();
                         ResultSet resultSet = statement.executeQuery(sql)) {
                        while (resultSet.next()) {
                            id[0] = resultSet.getInt(1);
                        }
                    }
                } catch (SQLException throwables) {
                    Log.e("Database", "Unable to connect to the database when sending user", throwables);
                }

                Log.i("Database", "User added");
            }
        });

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            Log.e("Database", "The thread was interrupted until the database was sent", e);
        }

        return id[0];
    }

    public static int getVariantsNumber() {
        final String sql = "SELECT COUNT(*) FROM variant";
        final int[] number = {0};

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try (Connection connection = getConnection();
                     Statement statement = connection.createStatement();
                     ResultSet resultSet = statement.executeQuery(sql)) {
                    while (resultSet.next()) {
                        number[0] = resultSet.getInt(1);
                    }
                } catch (SQLException throwables) {
                    Log.e("Database", "Unable to connect to the database when " +
                            "receipt of the number of options", throwables);
                }

                Log.i("Database", "The number of variants was obtained");
            }
        });

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            Log.e("Database", "The thread was interrupted until the database was sent", e);
        }

        return number[0];
    }

    public static List<VariantWithAudioFiles> getSolvedVariantsWithAudioFiles(int userId, final String path) {
        final String sql = "SELECT sv.id, a.name, sv.variant_id FROM audiofile a " +
                "INNER JOIN solved_variant sv ON sv.id = a.solved_variant_id WHERE sv.user_id = "
                + userId + "ORDER BY a.solved_variant_id, a.name";
        final List<VariantWithAudioFiles> list = new ArrayList<>();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try (Connection connection = getConnection();
                     Statement statement = connection.createStatement();
                     ResultSet resultSet = statement.executeQuery(sql)) {
                    int counter = 0;
                    String[] names = new String[4];

                    while (resultSet.next()) {
                        names[counter] = path + resultSet.getString("name");
                        counter++;
                        int variantId = resultSet.getInt("variant_id");
                        int id = resultSet.getInt("id");
                        if (counter == 4) {
                            list.add(new VariantWithAudioFiles(id, variantId, names));
                            counter = 0;
                            names = new String[4];
                        }
                    }
                } catch (SQLException throwables) {
                    Log.e("Database", "Unable to connect to the database when " +
                            "obtaining solved variants and audio recordings", throwables);
                }

                Log.i("Database", "Audio files received");
            }
        });

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            Log.e("Database", "The thread was interrupted until the database was sent", e);
        }

        return list;
    }

    public static void deleteSolvedVariant(final int solvedVariantId) {
        final String sql = "DELETE FROM " + solved_variant + " WHERE id = " + solvedVariantId;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try (Connection connection = getConnection();
                     Statement statement = connection.createStatement()) {
                    statement.execute(sql);
                } catch (SQLException throwables) {
                    Log.e("Database", "Unable to connect to the database when " +
                            "removing solved variants", throwables);
                }

                Log.i("Database", "Solved variant deleted");
            }
        });

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            Log.e("Database", "The thread was interrupted until the database was sent", e);
        }
    }

    public static List<Integer> getSolvedVariants(int userId) {
        final String sql = "SELECT variant_id FROM solved_variant WHERE user_id = " + userId;
        final List<Integer> list = new ArrayList<>();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try (Connection connection = getConnection();
                     Statement statement = connection.createStatement();
                     ResultSet resultSet = statement.executeQuery(sql)) {
                    while (resultSet.next()) {
                        list.add(resultSet.getInt("variant_id"));
                    }
                } catch (SQLException throwables) {
                    Log.e("Database", "Unable to connect to the database when " +
                            "obtaining solved variants", throwables);
                }

                Log.i("Database", "Solved variants received");
            }
        });

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            Log.e("Database", "The thread was interrupted until the database was sent", e);
        }

        return list;
    }

    public static Task1 getTask1(int variant) {
        final String sql = "SELECT text FROM task1 WHERE variant_id = " + variant;
        final Task1[] task1 = new Task1[1];

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try (Connection connection = getConnection();
                     Statement statement = connection.createStatement();
                     ResultSet resultSet = statement.executeQuery(sql)) {
                    while (resultSet.next()) {
                        task1[0] = new Task1(resultSet.getString(1));
                    }
                } catch (SQLException throwables) {
                    Log.e("Database", "Unable to connect to the database when " +
                            "receipt of data for task 1", throwables);
                }

                Log.i("Database", "Task1 received");
            }
        });

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            Log.e("Database", "The thread was interrupted until the database was sent", e);
        }

        return task1[0];
    }

    public static Task2 getTask2(boolean fileExists, final boolean cache,
                                 final int variant, final String sql) {
        final Task2[] task2 = new Task2[1];
        Thread thread;
        final double[] runtime = new double[1];

        if (fileExists) {
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String title = null;
                    String questions = null;
                    String imageText = null;
                    long time = System.nanoTime();
                    try (Connection connection = getConnection();
                         Statement statement = connection.createStatement();
                         ResultSet resultSet = statement.executeQuery(sql)) {
                        while (resultSet.next()) {
                            title = resultSet.getString("title");
                            questions = resultSet.getString("questions");
                            imageText = resultSet.getString("image_text");
                        }
                    } catch (SQLException throwables) {
                        Log.e("Database", "Unable to connect to the database when " +
                                "receipt of data for task 2", throwables);
                    }

                    Log.i("Database", "Task2 received without image byte array");
                    task2[0] = new Task2(title, questions, imageText);
                    runtime[0] = (System.nanoTime() - time) / 1000000.0;
                }
            });
        } else {
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String title = null;
                    String questions = null;
                    byte[] byteImage = null;
                    String imageText = null;
                    long time = System.nanoTime();
                    try (Connection connection = getConnection();
                         Statement statement = connection.createStatement();
                         ResultSet resultSet = statement.executeQuery(sql)) {
                        while (resultSet.next()) {
                            title = resultSet.getString("title");
                            questions = resultSet.getString("questions");
                            byteImage = resultSet.getBytes("image");
                            imageText = resultSet.getString("image_text");
                        }
                    } catch (SQLException throwables) {
                        Log.e("Database", "Unable to connect to the database when " +
                                "receipt of data for task 2", throwables);
                    }

                    Log.i("Database", "Task2 received with image byte array");

                    if (cache) {
                        String imagePath = CacheDatabase.byteArrayToCacheFile(byteImage, variant, 2);
                        task2[0] = new Task2(title, questions, imagePath, imageText);

                        Log.i("Database", "Image from task 2 was cached");
                    } else {
                        task2[0] = new Task2(title, questions, byteImage, imageText);
                    }

                    runtime[0] = (System.nanoTime() - time) / 1000000.0;
                }
            });
        }

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            Log.e("Database", "The thread was interrupted until the database was sent", e);
        }

        Log.i("Database", "Runtime getTask2() is " + runtime[0]);

        return task2[0];
    }

    public static Task3 getTask3(boolean fileExists, final boolean cache,
                                 final int variant, final String sql) {
        final Task3[] task3 = new Task3[1];
        Thread thread;
        final double[] runtime = new double[1];

        if (fileExists) {
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String questions = null;
                    long time = System.nanoTime();
                    try (Connection connection = getConnection();
                         Statement statement = connection.createStatement();
                         ResultSet resultSet = statement.executeQuery(sql)) {
                        while (resultSet.next()) {
                            questions = resultSet.getString("questions");
                        }
                    } catch (SQLException throwables) {
                        Log.e("Database", "Unable to connect to the database when " +
                                "receipt of data for task 3", throwables);
                    }

                    Log.i("Database", "Task3 received without image byte arrays");
                    task3[0] = new Task3(questions);
                    runtime[0] = (System.nanoTime() - time) / 1000000.0;
                }
            });
        } else {
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String questions = null;
                    byte[] byteImage1 = null;
                    byte[] byteImage2 = null;
                    byte[] byteImage3 = null;
                    long time = System.nanoTime();
                    try (Connection connection = getConnection();
                         Statement statement = connection.createStatement();
                         ResultSet resultSet = statement.executeQuery(sql)) {
                        while (resultSet.next()) {
                            questions = resultSet.getString("questions");
                            byteImage1 = resultSet.getBytes("image1");
                            byteImage2 = resultSet.getBytes("image2");
                            byteImage3 = resultSet.getBytes("image3");
                        }
                    } catch (SQLException throwables) {
                        Log.e("Database", "Unable to connect to the database when " +
                                "receipt of data for task 3", throwables);
                    }

                    Log.i("Database", "Task3 received with image byte array");

                    if (cache) {
                        String imagePath1 =
                                CacheDatabase.byteArrayToCacheFile(byteImage1, variant, 3, 1);
                        String imagePath2 =
                                CacheDatabase.byteArrayToCacheFile(byteImage2, variant, 3, 2);
                        String imagePath3 =
                                CacheDatabase.byteArrayToCacheFile(byteImage3, variant, 3, 3);
                        task3[0] = new Task3(questions, imagePath1, imagePath2, imagePath3);

                        Log.i("Database", "Images from task 3 was cached");
                    } else {
                        task3[0] = new Task3(questions, byteImage1, byteImage2, byteImage3);
                    }

                    runtime[0] = (System.nanoTime() - time) / 1000000.0;
                }
            });
        }

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            Log.e("Database", "The thread was interrupted until the database was sent", e);
        }

        Log.i("Database", "Runtime getTask3() is " + runtime[0]);

        return task3[0];
    }

    public static Task4 getTask4(boolean fileExists, final boolean cache,
                                 final int variant, final String sql) {
        final Task4[] task4 = new Task4[1];
        Thread thread;
        final double[] runtime = new double[1];

        if (fileExists) {
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String questions = null;
                    long time = System.nanoTime();
                    try (Connection connection = getConnection();
                         Statement statement = connection.createStatement();
                         ResultSet resultSet = statement.executeQuery(sql)) {
                        while (resultSet.next()) {
                            questions = resultSet.getString("questions");
                        }
                    } catch (SQLException throwables) {
                        Log.e("Database", "Unable to connect to the database when " +
                                "receipt of data for task 4", throwables);
                    }

                    Log.i("Database", "Task4 received without image byte arrays");
                    task4[0] = new Task4(questions);
                    runtime[0] = (System.nanoTime() - time) / 1000000.0;
                }
            });
        } else {
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String questions = null;
                    byte[] byteImage1 = null;
                    byte[] byteImage2 = null;
                    long time = System.nanoTime();
                    try (Connection connection = getConnection();
                         Statement statement = connection.createStatement();
                         ResultSet resultSet = statement.executeQuery(sql)) {
                        while (resultSet.next()) {
                            questions = resultSet.getString("questions");
                            byteImage1 = resultSet.getBytes("image1");
                            byteImage2 = resultSet.getBytes("image2");
                        }
                    } catch (SQLException throwables) {
                        Log.e("Database", "Unable to connect to the database when " +
                                "receipt of data for task 4", throwables);
                    }

                    Log.i("Database", "Task4 received with image byte array");

                    if (cache) {
                        String imagePath1 =
                                CacheDatabase.byteArrayToCacheFile(byteImage1, variant, 4, 1);
                        String imagePath2 =
                                CacheDatabase.byteArrayToCacheFile(byteImage2, variant, 4, 2);
                        task4[0] = new Task4(questions, imagePath1, imagePath2);

                        Log.i("Database", "Images from task 4 was cached");
                    } else {
                        task4[0] = new Task4(questions, byteImage1, byteImage2);
                    }

                    runtime[0] = (System.nanoTime() - time) / 1000000.0;
                }
            });
        }

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            Log.e("Database", "The thread was interrupted until the database was sent", e);
        }

        Log.i("Database", "Runtime getTask4() is " + runtime[0]);

        return task4[0];
    }

    public static void insertSolvedVariant(final int userId, final int variant, final String[] audioFiles) {
        final String sqlInsert = "INSERT INTO " + solved_variant +
                "(user_id, variant_id) VALUES " +
                "(" + userId + ", " + variant + ") RETURNING id";

        final String sqlExists = "SELECT EXISTS (SELECT user_id, variant_id FROM " +  solved_variant +
                " WHERE user_id = " + userId + " AND variant_id = " + variant + ")";

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean solvedVariantExists = false;
                try (Connection connection = getConnection()) {
                    try (Statement statement = connection.createStatement();
                         ResultSet resultSet = statement.executeQuery(sqlExists)) {
                        while (resultSet.next()) {
                            solvedVariantExists = resultSet.getBoolean(1);
                        }
                    }

                    if (!solvedVariantExists) {
                        int solvedVariantId = 0;

                        try (Statement statement = connection.createStatement();
                             ResultSet resultSet = statement.executeQuery(sqlInsert)) {
                            while (resultSet.next()) {
                                solvedVariantId = resultSet.getInt(1);
                            }
                        }

                        try (Statement statement = connection.createStatement()) {
                            for (String audioFileName : audioFiles) {
                                statement.execute("INSERT INTO " + audiofile +
                                        "(solved_variant_id, name) VALUES " +
                                        "(" + solvedVariantId + ", '" + audioFileName + "')");
                            }
                        }
                    }
                } catch (SQLException throwables) {
                    Log.e("Database", "Unable to connect to the database when " +
                            "sending solved variants", throwables);
                }

                Log.i("Database", "Solved variants have been successfully added");
            }
        });

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            Log.e("Database", "The thread was interrupted until the database was sent", e);
        }
    }

    public static List<String> getAudioFiles() {
        final String sql = "SELECT name FROM audiofile";
        final List<String> list = new ArrayList<>();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try (Connection connection = getConnection();
                     Statement statement = connection.createStatement();
                     ResultSet resultSet = statement.executeQuery(sql)) {
                    while (resultSet.next()) {
                        list.add(resultSet.getString(1));
                    }
                } catch (SQLException throwables) {
                    Log.e("Database", "Unable to connect to the database when " +
                            "obtaining audio files", throwables);
                }

                Log.i("Database", "Audio files received");
            }
        });

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            Log.e("Database", "The thread was interrupted until the database was sent", e);
        }

        return list;
    }

    public static void insertFeedback(final int rating, final int userId, final int variantId) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try (Connection connection = getConnection();
                     Statement statement = connection.createStatement()) {
                    statement.execute("INSERT INTO " + feedback +
                            "(rating, user_id, variant_id) VALUES " +
                            "(" + rating + ", " + userId + ", " + variantId + ")");
                } catch (SQLException throwables) {
                    Log.e("Database", "Unable to connect to the database when " +
                            "sending feedback", throwables);
                }

                Log.i("Database", "Feedback received");
            }
        });

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            Log.e("Database", "The thread was interrupted until the database was sent", e);
        }
    }
}
