# Цели работы

- Ознакомиться с жизненным циклом Activity
- Изучить основные возможности и свойства alternative resources

# 1. Activity

Продемонстрируйте жизненный цикл Activity на любом нетривиальном примере.

## Пример 1: Split Screen

![](https://raw.githubusercontent.com/alexnevskiy/imagesForLabs/main/Split%20Screen.png)

*Рис. 1. Методы жизненного цикла приложения в режиме Split Screen*

При открытии окна вызываются методы в следующем порядке: `setContentView(), onCreate(), onStart()` и `onResume()`.  Данные 4 метода всегда вызываются при первом открытии окна, поэтому в следующих примерах они будут пропущены. Если перевести приложение в режим Split Screen, то в логах можно наблюдать следующие вызовы методов: `onPause(), onStop(), onSaveInstanceState(), onDestroy(), onCreate(), setContentView(), onStart(), onRestoreInstanceState(), onResume()` и `onPause()`. Если же окно растянуть снова на весь экран, то произойдёт вся таже процедура из этих же методов. Если же открыть второе окно, то появится вызов метода `onResume()`. При нажатии на экран в открытом втором приложении, никаких вызовов методов не происходит. При переведении моего приложения в режим Split Screen таймер, который ведёт обратный отсчёт времени для подготовки человека к ответу на задание, сбрасывается до начального времени, поэтому принято решение отключить возможность перевода приложения в режим разделённого экрана. Его можно отключить путём написания атрибута `android:resizeableActivity="false"` в файле манифеста.

## Пример 2: Звонок на устройство

![](https://raw.githubusercontent.com/alexnevskiy/imagesForLabs/main/Call%20device.png)

*Рис. 2. Методы жизненного цикла приложения во время звонка*

При звонке на устройство появляется всплывающие уведомление, при этом никакие методы жизненного цикла Activity не вызывались. Если же пользователь решит ответить на звонок, то вызовутся методы `onPause(), onStop()` и `onSaveInstanceState()`. После завершения звонка при возвращении в окно приложения вызываются следующие методы: `onRestart(), onStart()` и `onResume()`.

## Пример 3: Вызов Google Assistant

![](https://raw.githubusercontent.com/alexnevskiy/imagesForLabs/main/Google%20Assistant.png)

*Рис. 3. Методы жизненного цикла приложения при вызове Google Assistant*

При кратковременном вызове Google Assistant вызывается метод жизненного цикла `onPause()` и, соответственно, `onResume()` при возвращении в приложение. Если же вызвать ассистента и ждать, то через некоторое время откроется окно самого ассистента и будут вызваны следующие методы нашего приложения: `onStop(), onSaveInstanceState()`. При закрытии Google Assistant вызывается знакомый нам набор методов: `onRestart(), onStart()` и `onResume()`.

# 2. Alternative Resources

Продемонстрируйте работу альтернативного ресурса (тип ресурса согласно 4 варианту) на каком-либо примере.

![](https://raw.githubusercontent.com/alexnevskiy/imagesForLabs/main/Alternative%20Resources.png)

*Рис. 4. Экран запущенного приложения с разрешением меньше 480dp*

![](https://raw.githubusercontent.com/alexnevskiy/imagesForLabs/main/Alternative%20Resources%20480dp.png)

*Рис. 5. Экран запущенного приложения с разрешением больше 480dp и конфигурацией Available height*

![](https://raw.githubusercontent.com/alexnevskiy/imagesForLabs/main/Alternative%20Resources%20480dp%20without%20Available%20height.png)

*Рис. 6. Экран запущенного приложения с разрешением больше 480dp без конфигурации Available height*

Согласно 4 варианту нужно осуществить поддержку конфигурации Available height (Доступная высота). Для этого создана отдельная директория под названием `layout-h480dp`, в которой хранится `task1.xml` файл, переделанный специально для доступной высоты экрана устройства. При запуске приложения выбирается тот файл, который соответствует конфигурации устройства. Полезный эффект данного альтернативного ресурса заключается в том, что мы можем свободно переделывать вёрстку для разных доступных высот экрана. На примере моего приложения можно видеть, что если бы данное окно было запущено со стандартным `task1.xml` файлом, то интерфейс был бы слишком маленький, например, для современных планшетов с большой диагональю и разрешением, пользователю пришлось бы всматриваться в текст по центру экрана, как показано на рис. 6.

# 3. Best-matching resource

Для заданного набора альтернативных ресурсов, предоставляемых  приложением, и заданной конфигурации устройства (оба параметра согласно 4 варианту) объясните, какой ресурс будет выбран в конечном итоге. Ответ докажите.

***Вариант: 4***

**Конфигурация устройства:**

LOCALE_LANG: en

LOCALE_REGION: rFR

SCREEN_SIZE: small

SCREEN_ASPECT: notlong

ROUND_SCREEN: notround

ORIENTATION: land

UI_MODE: desk

NIGHT_MODE: night

PIXEL_DENSITY: xxxhdpi

TOUCH: finger

PRIMARY_INPUT: nokeys

NAV_KEYS: dpad

PLATFORM_VER: v26

**Конфигурация ресурсов:**

(default)

rCA-round-port-night-mdpi-v26

rUS-notlong-car-notnight-finger-nonav

fr-rFR-port-television-night

en-rCA-large-notlong-notnight-finger-dpad-v25

rCA-finger-v27

fr-round-v26

en-12key-dpad-v26

normal-round-v26

notlong-notround-port-television

xlarge-round-night-nodpi-dpad

Исполним пошагово алгоритм для нахождения наиболее подходящего ресурса:

1. Исключение файлов ресурсов, которые противоречат конфигурации устройства.

   | Конфигурация ресурсов                         | Конфигурация устройства | Подходит ли ресурс |
   | --------------------------------------------- | ----------------------- | ------------------ |
   | (default)                                     |                         | Да                 |
   | rCA-round-port-night-mdpi-v26                 | LOCALE_REGION: rFR      | Нет                |
   | rUS-notlong-car-notnight-finger-nonav         | LOCALE_REGION: rFR      | Нет                |
   | fr-rFR-port-television-night                  | LOCALE_LANG: en         | Нет                |
   | en-rCA-large-notlong-notnight-finger-dpad-v25 | LOCALE_REGION: rFR      | Нет                |
   | rCA-finger-v27                                | LOCALE_REGION: rFR      | Нет                |
   | fr-round-v26                                  | LOCALE_LANG: en         | Нет                |
   | en-12key-dpad-v26                             | PRIMARY_INPUT: nokeys   | Нет                |
   | normal-round-v26                              | SCREEN_SIZE: small      | Нет                |
   | notlong-notround-port-television              | ORIENTATION: land       | Нет                |
   | xlarge-round-night-nodpi-dpad                 | SCREEN_SIZE: small      | Нет                |

После первого шага сразу можно сделать вывод, что для конфигурации устройства 4 варианта подходит только *(default)* ресурс.

# 4. Сохранение состояния Activity

Студент написал приложение: continuewatch. Это приложение по заданию должно считать, сколько секунд пользователь провел в этом приложении. Найдите ошибки в этом приложении и исправьте их.

Написанное приложение студентом считает количество секунд постоянно, даже если приложение не активно, а также сбрасывает счётчик, если изменить ориентацию экрана или запустить приложение в режиме Split Screen. Для решения проблемы связанной с постоянным подсчётом секунд при неактивном приложении создана `boolean` переменная, которая отвечает за состояние приложения, то есть `false` - неактивно, `true` - активно. Вывод количества секунд на экран вынесен в отдельный метод `secondsDisplay()`. Активация данной переменной происходит в методах `onResume()` и `onPause()` соответственно. Чтобы решить проблему со сбросом счётчика  при изменении размеров окна, сохраняем значение счётчика на момент изменения окна в методе `onSaveInstanceState()` при помощи `Bundle`, который подаётся на вход самого `onSaveInstanceState()` и `onRestoreInstanceState()`. В методе `onRestoreInstanceState()` достаём сохранённое нами значение счётчика и присваиваем переменной `secondsElapsed` и далее запускаем `secondsDisplay()`.

# Выводы:

В процессе выполнения данной лабораторной работы в среде разработки Android Studio изучен жизненный цикл Activity и основные возможности и свойства alternative resources. Также изучен алгоритм для выбора best-matching resource, проведена работа по нахождению ошибок в приложении, связанных с жизненным циклом Activity, и их исправлению.

# Приложение:

## Листинг 1: Activity

```java
package com.example.germanexam;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class TaskOne extends AppCompatActivity {

    long timeLeft = 90000;
    int counter = 0;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("LifeCycleActivity", "onCreate()");
        setContentView(R.layout.task1);
        final TextView timeRemaining = (TextView) findViewById(R.id.time_remaining);
        final ProgressBar timeline = (ProgressBar) findViewById(R.id.timeline);
        countDownTimer = new CountDownTimer(timeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                updateTimer();
                counter++;
                timeline.setProgress(counter);
            }

            private void updateTimer() {
                int minutes = (int) (timeLeft / 1000) / 60;
                int seconds = (int) (timeLeft / 1000) % 60;

                String timeLeftText = String.format(Locale.getDefault(), "-%02d:%02d", minutes, seconds);

                timeRemaining.setText(timeLeftText);
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("LifeCycleActivity", "onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("LifeCycleActivity", "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("LifeCycleActivity", "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("LifeCycleActivity", "onStop()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("LifeCycleActivity", "onRestart()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("LifeCycleActivity", "onDestroy()");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        Log.d("LifeCycleActivity", "onSaveInstanceState()");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("LifeCycleActivity", "onSaveInstanceState()");
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d("LifeCycleActivity", "onRestoreInstanceState()");
    }

    @Override
    public void onRestoreInstanceState(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        Log.d("LifeCycleActivity", "onRestoreInstanceState()");
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        Log.d("LifeCycleActivity", "setContentView()");
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        Log.d("LifeCycleActivity", "setContentView()");
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        Log.d("LifeCycleActivity", "setContentView()");
    }
}
```

## Листинг 2: Alternative Resources (меньше 480dp)

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:orientation="vertical"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/greyExam">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:orientation="horizontal" >

        <Space
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_weight="0.4" />

        <TextView
            android:id="@+id/Task1"
            android:layout_width="0dp"
            android:layout_height="match_parent"
            android:layout_weight="2"
            android:lines="1"
            app:autoSizeMaxTextSize="70sp"
            app:autoSizeMinTextSize="10sp"
            app:autoSizeTextType="uniform"
            android:gravity="center"
            android:text="@string/task_one" />

        <Space
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_weight="5" />

        <ImageView
            android:id="@+id/clock"
            android:layout_width="0dp"
            android:layout_height="match_parent"
            android:layout_weight="1"
            app:srcCompat="@android:drawable/ic_menu_recent_history" />

        <TextView
            android:id="@+id/prep_ans"
            android:layout_width="0dp"
            android:layout_height="match_parent"
            android:layout_weight="2.5"
            android:lines="2"
            app:autoSizeMaxTextSize="70sp"
            app:autoSizeMinTextSize="10sp"
            app:autoSizeTextType="uniform"
            android:fontFamily="@font/calibri"
            android:text="@string/prep_ans" />

        <Space
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_weight="0.4" />
    </LinearLayout>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="7"
        android:orientation="vertical" >

        <LinearLayout
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:orientation="horizontal">

            <Space
                android:layout_width="0dp"
                android:layout_height="match_parent"
                android:layout_weight="1" />

            <TextView
                android:id="@+id/task1_logo"
                android:layout_width="30dp"
                android:layout_height="30dp"
                android:layout_weight="0"
                android:background="@drawable/button_blue_circle"
                android:gravity="center"
                android:layout_gravity="top"
                android:text="1"
                android:textColor="@android:color/white"
                android:textSize="20sp"
                android:fontFamily="@font/calibril"/>

            <TextView
                android:id="@+id/task1_text"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="23"
                android:layout_gravity="center_vertical"
                android:layout_marginLeft="10dp"
                android:fontFamily="@font/calibrib"
                android:lines="4"
                app:autoSizeMaxTextSize="70sp"
                app:autoSizeMinTextSize="10sp"
                app:autoSizeTextType="uniform"
                android:text="@string/task1"
                android:textColor="@android:color/black" />

            <Space
                android:layout_width="0dp"
                android:layout_height="match_parent"
                android:layout_weight="1" />
        </LinearLayout>

        <ScrollView
            android:layout_width="match_parent"
            android:layout_height="match_parent">

            <LinearLayout
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:orientation="horizontal">

                <Space
                    android:layout_width="0dp"
                    android:layout_height="match_parent"
                    android:layout_weight="1" />

                <TextView
                    android:id="@+id/text1"
                    android:layout_width="0dp"
                    android:layout_height="match_parent"
                    android:layout_weight="25"
                    android:fontFamily="@font/calibri"
                    android:text="@string/text1"
                    android:lines="22"
                    app:autoSizeMaxTextSize="70sp"
                    app:autoSizeMinTextSize="18sp"
                    app:autoSizeTextType="uniform"
                    android:textColor="@android:color/black"
                    android:background="@android:color/white"
                    android:padding="10dp"/>

                <Space
                    android:layout_width="0dp"
                    android:layout_height="match_parent"
                    android:layout_weight="1" />
            </LinearLayout>
        </ScrollView>

    </LinearLayout>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:orientation="horizontal" >

        <Space
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_weight="0.4" />

        <TextView
            android:id="@+id/preparation"
            android:layout_width="0dp"
            android:layout_height="match_parent"
            android:layout_weight="2"
            android:gravity="center"
            android:lines="1"
            android:paddingRight="10dp"
            app:autoSizeMaxTextSize="70sp"
            app:autoSizeMinTextSize="10sp"
            app:autoSizeTextType="uniform"
            android:text="@string/preparation" />

        <ProgressBar
            android:id="@+id/timeline"
            style="?android:attr/progressBarStyleHorizontal"
            android:max="90"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_gravity="center"
            android:layout_weight="6" />

        <TextView
            android:id="@+id/time_remaining"
            android:layout_width="0dp"
            android:layout_height="match_parent"
            android:layout_weight="1"
            android:gravity="center"
            android:paddingLeft="10dp"
            android:lines="1"
            app:autoSizeMaxTextSize="70sp"
            app:autoSizeMinTextSize="10sp"
            app:autoSizeTextType="uniform"
            android:text="" />

        <Space
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_weight="0.4" />
    </LinearLayout>
</LinearLayout>
```

## Листинг 3: Alternative Resources (больше 480dp)

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:orientation="vertical"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/greyExam">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:orientation="horizontal" >

        <Space
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_weight="0.4" />

        <TextView
            android:id="@+id/Task1"
            android:layout_width="0dp"
            android:layout_height="match_parent"
            android:layout_weight="2"
            android:lines="1"
            app:autoSizeMaxTextSize="70sp"
            app:autoSizeMinTextSize="10sp"
            app:autoSizeTextType="uniform"
            android:gravity="center"
            android:text="@string/task_one" />

        <Space
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_weight="5" />

        <ImageView
            android:id="@+id/clock"
            android:layout_width="0dp"
            android:layout_height="match_parent"
            android:layout_weight="1"
            app:srcCompat="@android:drawable/ic_menu_recent_history" />

        <TextView
            android:id="@+id/prep_ans"
            android:layout_width="0dp"
            android:layout_height="match_parent"
            android:layout_weight="2.5"
            android:lines="2"
            app:autoSizeMaxTextSize="70sp"
            app:autoSizeMinTextSize="10sp"
            app:autoSizeTextType="uniform"
            android:fontFamily="@font/calibri"
            android:text="@string/prep_ans" />

        <Space
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_weight="0.4" />
    </LinearLayout>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="7"
        android:orientation="vertical" >

        <LinearLayout
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:orientation="horizontal">

            <Space
                android:layout_width="0dp"
                android:layout_height="match_parent"
                android:layout_weight="1" />

            <TextView
                android:id="@+id/task1_logo"
                android:layout_width="50dp"
                android:layout_height="50dp"
                android:layout_weight="0"
                android:background="@drawable/button_blue_circle"
                android:gravity="center"
                android:layout_gravity="top"
                android:text="1"
                android:textColor="@android:color/white"
                android:textSize="35sp"
                android:fontFamily="@font/calibril"/>

            <TextView
                android:id="@+id/task1_text"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="23"
                android:layout_gravity="center_vertical"
                android:layout_marginLeft="10dp"
                android:fontFamily="@font/calibrib"
                android:lines="6"
                app:autoSizeMaxTextSize="100sp"
                app:autoSizeMinTextSize="10sp"
                app:autoSizeTextType="uniform"
                android:text="@string/task1"
                android:textColor="@android:color/black" />

            <Space
                android:layout_width="0dp"
                android:layout_height="match_parent"
                android:layout_weight="1" />
        </LinearLayout>

        <ScrollView
            android:layout_width="match_parent"
            android:layout_height="match_parent">

            <LinearLayout
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:orientation="horizontal">

                <Space
                    android:layout_width="0dp"
                    android:layout_height="match_parent"
                    android:layout_weight="1" />

                <TextView
                    android:id="@+id/text1"
                    android:layout_width="0dp"
                    android:layout_height="match_parent"
                    android:layout_weight="25"
                    android:fontFamily="@font/calibri"
                    android:text="@string/text1"
                    android:lines="30"
                    app:autoSizeMaxTextSize="100sp"
                    app:autoSizeMinTextSize="18sp"
                    app:autoSizeTextType="uniform"
                    android:textColor="@android:color/black"
                    android:background="@android:color/white"
                    android:padding="10dp"/>

                <Space
                    android:layout_width="0dp"
                    android:layout_height="match_parent"
                    android:layout_weight="1" />
            </LinearLayout>
        </ScrollView>

    </LinearLayout>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:orientation="horizontal" >

        <Space
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_weight="0.4" />

        <TextView
            android:id="@+id/preparation"
            android:layout_width="0dp"
            android:layout_height="match_parent"
            android:layout_weight="2"
            android:gravity="center"
            android:lines="1"
            android:paddingRight="10dp"
            app:autoSizeMaxTextSize="70sp"
            app:autoSizeMinTextSize="10sp"
            app:autoSizeTextType="uniform"
            android:text="@string/preparation" />

        <ProgressBar
            android:id="@+id/timeline"
            style="?android:attr/progressBarStyleHorizontal"
            android:max="90"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_gravity="center"
            android:layout_weight="6" />

        <TextView
            android:id="@+id/time_remaining"
            android:layout_width="0dp"
            android:layout_height="match_parent"
            android:layout_weight="1"
            android:gravity="center"
            android:paddingLeft="10dp"
            android:lines="1"
            app:autoSizeMaxTextSize="70sp"
            app:autoSizeMinTextSize="10sp"
            app:autoSizeTextType="uniform"
            android:text="" />

        <Space
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_weight="0.4" />
    </LinearLayout>
</LinearLayout>
```

## Листинг 4: Continuewatch

```kotlin
package ru.spbstu.icc.kspt.lab2.continuewatch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var secondsElapsed: Int = 0
    var run = false

    var backgroundThread = Thread {
        while (true) {
            Thread.sleep(1000)
            if (run) {
                secondsDisplay()
            }
        }
    }

    private fun secondsDisplay() {
        textSecondsElapsed.post {
            textSecondsElapsed.setText("Seconds elapsed: " + secondsElapsed++)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        backgroundThread.start()
    }

    override fun onResume() {
        run = true
        super.onResume()
    }

    override fun onPause() {
        run = false
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("seconds", secondsElapsed)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        secondsElapsed = savedInstanceState.getInt("seconds")
        secondsDisplay()
        super.onRestoreInstanceState(savedInstanceState)
    }
}
```