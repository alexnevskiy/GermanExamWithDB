# **Цели работы**

- Ознакомиться со средой разработки Android Studio
- Изучить основные принципы вёрстки layout с использованием View и ViewGroup
- Изучить основные возможности и свойства LinearLayout
- Изучить основные возможности и свойства ConstraintLayout

# 1. LinearLayout

Создайте layout ресурсы с использованием LinearLayout.

![](https://raw.githubusercontent.com/alexnevskiy/imagesForLabs/main/LinearLayout.png)

*Рис. 1. Созданное окно приложения на основе LinearLayout*

Данный LinearLayout содержит в себе 3 LinearLayout, два из которых имеют горизонтальную ориентацию `android:orientation="horizontal"` для того, чтобы элементы последовательно заполнялись по горизонтали, и один вертикальную `android:orientation="vertical"`, чтобы элементы заполнялись последовательно по вертикали. Верхний LinearLayout содержит в себе 6 элементов: два TextView, ImageView и 3 Space для выравнивания элементов по ширине экрана. У всех виджетов указано свойство `android:layout_weight`, которое позволяет задать вес для заполнения всего оставшегося пространства LinearLayout. Средний LinearLayout содержит ещё один горизонтальный LinearLayout, вмещающий в себя пару Space и два textView, а также ScrollView, который вмещает в себя TextView с двумя Space по бокам. У всех виджетов также указано свойство `android:layout_weight`. Нижний LinearLayout содержит два TextView, один ProgressBar и два Space на границах для выравнивания элементов, все виджеты также со свойством `android:layout_weight`.

**Описание атрибутов:**

`layout_weight` - атрибут, который назначает индивидуальный вес для дочернего элемента, то есть сколько места элемент должен занимать на экране. Вес по умолчанию равен нулю.

`gravity` - атрибут, который создаёт позиционирование содержимого элемента.

`layout_gravity` - позиционирование содержимого относительно родителя.

`orientation` – определяет ориентацию layout (горизонтальная или вертикальная).

`layout_height, layout_width` – определяет размер высоты и ширины. Для них существуют const: `match_parent` - представление должно быть таким же большим, как его родитель (за исключением заполнения), `wrap_content` - представление должно быть достаточно большим, чтобы вместить его содержимое (плюс отступы).

# 2. ConstraintLayout

Создайте layout ресурс с использованием ConstraintLayout.

![](https://raw.githubusercontent.com/alexnevskiy/imagesForLabs/main/ConstraintLayout.png)

*Рис. 2. Созданное окно приложения на основе ConstraintLayout*

Данный вертикальный ConstraintLayout содержит в себе кнопку «Назад» (Button) и ScrollView. Эти два виджета связаны при помощи `constraints` (ограничений) за счёт свойства `app:layout_constraintXXX_toXXXOf="XXX"`, которое позволяет привязать сторону виджета к конкретному элементу. Верхняя, правая и нижняя стороны кнопки назад привязаны к границам родителя, а левая сторона к левой границе ScrollView для выравнивания этих двух виджетов по левому краю. ScrollView в свою очередь связан левой, нижней и правой сторонами с границами родителя, а верхняя часть привязана к нижней границе кнопки для того, чтобы при различных разрешениях экрана данный виджет был всегда ниже кнопки. ScrollView содержит в себе 5 ConstraintLayout с горизонтальной ориентацией, содержащие в себе кнопки. Если рассматривать каждый ConstraintLayout, то внутри него находятся 5 кнопок, связанные между собой `constraint`'ами, кроме средней кнопки – она связана с границами layout для того, чтобы элемент всегда оставался в центре экрана и все остальные кнопки растягивались относительно неё по ширине экрана.

**Описание атрибутов:**

`layout_constraintDimensionRatio` – позволяет рассчитывать высоту или ширину view на основе заданного соотношения сторон. То есть, например, при соотношении сторон 16:9, если высота будет `900dp`, то ширина рассчитается, как `1600dp`.

`layout_constraintHorizontal_weight, layout_constraintVertical_weight` – элементы располагаются в соответствии с их весом по аналогии с тем, как работает LinearLayout только по горизонтали и по вертикали.

# 3. TableLayout

При помощи TableLayout переделано меню из части 2 с ConstraintLayout.

![](https://raw.githubusercontent.com/alexnevskiy/imagesForLabs/main/TableLayout.png)

*Рис. 3. Созданное окно приложения на основе TableLayout*

Данный TableLayout практически полностью написан программно в .java классе, в .xml файле описан только LinearLayout, в который вложен ScrollView с TableLayout. В самом TableLayout размещены 5 TableRow, с находящимися 5 кнопками (Button) внутри. Всем кнопкам задан вес равный 1 для равного распределения по ширине экрана.

# Выводы: 

В процессе выполнения данной лабораторной работы в среде разработки Android Studio изучены основы вёрстки layout с использованием View (элементы интерфейса) и ViewGroup (может содержать другие View). Также изучены основные возможности и свойства LinearLayout и ConstraintLayout. Для строгого представления элементов можно использовать LinearLayout, а для сложной вёрстки, где может понадобиться «привязывание» элементов друг к другу или родителям, необходимо использовать ConstraintLayout.

# Приложение:

## Листинг 1: LinearLayout

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
                android:lines="5"
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
            android:text="@string/timer" />

        <Space
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_weight="0.4" />
    </LinearLayout>
</LinearLayout>
```

## Листинг 2: ConstraintLayout

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:padding="10dp"
    android:orientation="vertical"
    xmlns:app="http://schemas.android.com/apk/res-auto">

    <Button
        android:id="@+id/button_back"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:background="@drawable/button_blue"
        android:paddingLeft="10dp"
        android:paddingRight="10dp"
        android:text="@string/back"
        android:textColor="#FFFFFF"
        android:textSize="20sp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.0"
        app:layout_constraintStart_toStartOf="@+id/scrollView2"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintVertical_bias="0.0" />

    <ScrollView
        android:id="@+id/scrollView2"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_marginTop="16dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/button_back">

        <LinearLayout
            android:id="@+id/variants_layout"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical">

            <androidx.constraintlayout.widget.ConstraintLayout
                android:id="@+id/line1"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:orientation="horizontal">

                <Button
                    android:id="@+id/button1"
                    android:layout_width="60dp"
                    android:layout_height="60dp"
                    android:background="@drawable/button_blue_circle"
                    android:gravity="center"
                    android:text="1"
                    android:textColor="@android:color/white"
                    android:textSize="30sp"
                    app:layout_constraintBottom_toBottomOf="parent"
                    app:layout_constraintEnd_toStartOf="@+id/button2"
                    app:layout_constraintStart_toStartOf="parent"
                    app:layout_constraintTop_toTopOf="parent" />

                <Button
                    android:id="@+id/button2"
                    android:layout_width="60dp"
                    android:layout_height="60dp"
                    android:background="@drawable/button_blue_circle"
                    android:gravity="center"
                    android:text="2"
                    android:textColor="@android:color/white"
                    android:textSize="30sp"
                    app:layout_constraintBottom_toBottomOf="parent"
                    app:layout_constraintEnd_toStartOf="@+id/button3"
                    app:layout_constraintStart_toEndOf="@+id/button1"
                    app:layout_constraintTop_toTopOf="parent" />

                <Button
                    android:id="@+id/button3"
                    android:layout_width="60dp"
                    android:layout_height="60dp"
                    android:background="@drawable/button_blue_circle"
                    android:gravity="center"
                    android:text="3"
                    android:textColor="@android:color/white"
                    android:textSize="30sp"
                    app:layout_constraintBottom_toBottomOf="parent"
                    app:layout_constraintEnd_toEndOf="parent"
                    app:layout_constraintStart_toStartOf="parent"
                    app:layout_constraintTop_toTopOf="parent" />

                <Button
                    android:id="@+id/button4"
                    android:layout_width="60dp"
                    android:layout_height="60dp"
                    android:background="@drawable/button_blue_circle"
                    android:gravity="center"
                    android:text="4"
                    android:textColor="@android:color/white"
                    android:textSize="30sp"
                    app:layout_constraintBottom_toBottomOf="parent"
                    app:layout_constraintEnd_toStartOf="@+id/button5"
                    app:layout_constraintStart_toEndOf="@+id/button3"
                    app:layout_constraintTop_toTopOf="parent" />

                <Button
                    android:id="@+id/button5"
                    android:layout_width="60dp"
                    android:layout_height="60dp"
                    android:background="@drawable/button_blue_circle"
                    android:gravity="center"
                    android:text="5"
                    android:textColor="@android:color/white"
                    android:textSize="30sp"
                    app:layout_constraintBottom_toBottomOf="parent"
                    app:layout_constraintEnd_toEndOf="parent"
                    app:layout_constraintStart_toEndOf="@+id/button4"
                    app:layout_constraintTop_toTopOf="parent" />

            </androidx.constraintlayout.widget.ConstraintLayout>

            <androidx.constraintlayout.widget.ConstraintLayout
                android:id="@+id/line2"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:layout_marginTop="20dp"
                android:orientation="horizontal">

                <Button
                    android:id="@+id/button6"
                    android:layout_width="60dp"
                    android:layout_height="60dp"
                    android:background="@drawable/button_blue_circle"
                    android:gravity="center"
                    android:text="6"
                    android:textColor="@android:color/white"
                    android:textSize="30sp"
                    app:layout_constraintBottom_toBottomOf="parent"
                    app:layout_constraintEnd_toStartOf="@+id/button7"
                    app:layout_constraintStart_toStartOf="parent"
                    app:layout_constraintTop_toTopOf="parent" />

                <Button
                    android:id="@+id/button7"
                    android:layout_width="60dp"
                    android:layout_height="60dp"
                    android:background="@drawable/button_blue_circle"
                    android:gravity="center"
                    android:text="7"
                    android:textColor="@android:color/white"
                    android:textSize="30sp"
                    app:layout_constraintBottom_toBottomOf="parent"
                    app:layout_constraintEnd_toStartOf="@+id/button8"
                    app:layout_constraintStart_toEndOf="@+id/button6"
                    app:layout_constraintTop_toTopOf="parent" />

                <Button
                    android:id="@+id/button8"
                    android:layout_width="60dp"
                    android:layout_height="60dp"
                    android:background="@drawable/button_blue_circle"
                    android:gravity="center"
                    android:text="8"
                    android:textColor="@android:color/white"
                    android:textSize="30sp"
                    app:layout_constraintBottom_toBottomOf="parent"
                    app:layout_constraintEnd_toEndOf="parent"
                    app:layout_constraintStart_toStartOf="parent"
                    app:layout_constraintTop_toTopOf="parent" />

                <Button
                    android:id="@+id/button9"
                    android:layout_width="60dp"
                    android:layout_height="60dp"
                    android:background="@drawable/button_blue_circle"
                    android:gravity="center"
                    android:text="9"
                    android:textColor="@android:color/white"
                    android:textSize="30sp"
                    app:layout_constraintBottom_toBottomOf="parent"
                    app:layout_constraintEnd_toStartOf="@+id/button10"
                    app:layout_constraintStart_toEndOf="@+id/button8"
                    app:layout_constraintTop_toTopOf="parent" />

                <Button
                    android:id="@+id/button10"
                    android:layout_width="60dp"
                    android:layout_height="60dp"
                    android:background="@drawable/button_blue_circle"
                    android:gravity="center"
                    android:text="10"
                    android:textColor="@android:color/white"
                    android:textSize="30sp"
                    app:layout_constraintBottom_toBottomOf="parent"
                    app:layout_constraintEnd_toEndOf="parent"
                    app:layout_constraintStart_toEndOf="@+id/button9"
                    app:layout_constraintTop_toTopOf="parent" />
            </androidx.constraintlayout.widget.ConstraintLayout>

            <androidx.constraintlayout.widget.ConstraintLayout
                android:id="@+id/line3"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:layout_marginTop="20dp"
                android:orientation="horizontal">

                <Button
                    android:id="@+id/button11"
                    android:layout_width="60dp"
                    android:layout_height="60dp"
                    android:background="@drawable/button_blue_circle"
                    android:gravity="center"
                    android:text="11"
                    android:textColor="@android:color/white"
                    android:textSize="30sp"
                    app:layout_constraintBottom_toBottomOf="parent"
                    app:layout_constraintEnd_toStartOf="@+id/button12"
                    app:layout_constraintStart_toStartOf="parent"
                    app:layout_constraintTop_toTopOf="parent" />

                <Button
                    android:id="@+id/button12"
                    android:layout_width="60dp"
                    android:layout_height="60dp"
                    android:background="@drawable/button_blue_circle"
                    android:gravity="center"
                    android:text="12"
                    android:textColor="@android:color/white"
                    android:textSize="30sp"
                    app:layout_constraintBottom_toBottomOf="parent"
                    app:layout_constraintEnd_toStartOf="@+id/button13"
                    app:layout_constraintStart_toEndOf="@+id/button11"
                    app:layout_constraintTop_toTopOf="parent" />

                <Button
                    android:id="@+id/button13"
                    android:layout_width="60dp"
                    android:layout_height="60dp"
                    android:background="@drawable/button_blue_circle"
                    android:gravity="center"
                    android:text="13"
                    android:textColor="@android:color/white"
                    android:textSize="30sp"
                    app:layout_constraintBottom_toBottomOf="parent"
                    app:layout_constraintEnd_toEndOf="parent"
                    app:layout_constraintStart_toStartOf="parent"
                    app:layout_constraintTop_toTopOf="parent" />

                <Button
                    android:id="@+id/button14"
                    android:layout_width="60dp"
                    android:layout_height="60dp"
                    android:background="@drawable/button_blue_circle"
                    android:gravity="center"
                    android:text="14"
                    android:textColor="@android:color/white"
                    android:textSize="30sp"
                    app:layout_constraintBottom_toBottomOf="parent"
                    app:layout_constraintEnd_toStartOf="@+id/button15"
                    app:layout_constraintStart_toEndOf="@+id/button13"
                    app:layout_constraintTop_toTopOf="parent" />

                <Button
                    android:id="@+id/button15"
                    android:layout_width="60dp"
                    android:layout_height="60dp"
                    android:background="@drawable/button_blue_circle"
                    android:gravity="center"
                    android:text="15"
                    android:textColor="@android:color/white"
                    android:textSize="30sp"
                    app:layout_constraintBottom_toBottomOf="parent"
                    app:layout_constraintEnd_toEndOf="parent"
                    app:layout_constraintStart_toEndOf="@+id/button14"
                    app:layout_constraintTop_toTopOf="parent" />
            </androidx.constraintlayout.widget.ConstraintLayout>

            <androidx.constraintlayout.widget.ConstraintLayout
                android:id="@+id/line4"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:layout_marginTop="20dp"
                android:orientation="horizontal">

                <Button
                    android:id="@+id/button16"
                    android:layout_width="60dp"
                    android:layout_height="60dp"
                    android:background="@drawable/button_blue_circle"
                    android:gravity="center"
                    android:text="16"
                    android:textColor="@android:color/white"
                    android:textSize="30sp"
                    app:layout_constraintBottom_toBottomOf="parent"
                    app:layout_constraintEnd_toStartOf="@+id/button17"
                    app:layout_constraintStart_toStartOf="parent"
                    app:layout_constraintTop_toTopOf="parent" />

                <Button
                    android:id="@+id/button17"
                    android:layout_width="60dp"
                    android:layout_height="60dp"
                    android:background="@drawable/button_blue_circle"
                    android:gravity="center"
                    android:text="17"
                    android:textColor="@android:color/white"
                    android:textSize="30sp"
                    app:layout_constraintBottom_toBottomOf="parent"
                    app:layout_constraintEnd_toStartOf="@+id/button18"
                    app:layout_constraintStart_toEndOf="@+id/button16"
                    app:layout_constraintTop_toTopOf="parent" />

                <Button
                    android:id="@+id/button18"
                    android:layout_width="60dp"
                    android:layout_height="60dp"
                    android:background="@drawable/button_blue_circle"
                    android:gravity="center"
                    android:text="18"
                    android:textColor="@android:color/white"
                    android:textSize="30sp"
                    app:layout_constraintBottom_toBottomOf="parent"
                    app:layout_constraintEnd_toEndOf="parent"
                    app:layout_constraintStart_toStartOf="parent"
                    app:layout_constraintTop_toTopOf="parent" />

                <Button
                    android:id="@+id/button19"
                    android:layout_width="60dp"
                    android:layout_height="60dp"
                    android:background="@drawable/button_blue_circle"
                    android:gravity="center"
                    android:text="19"
                    android:textColor="@android:color/white"
                    android:textSize="30sp"
                    app:layout_constraintBottom_toBottomOf="parent"
                    app:layout_constraintEnd_toStartOf="@+id/button20"
                    app:layout_constraintStart_toEndOf="@+id/button18"
                    app:layout_constraintTop_toTopOf="parent" />

                <Button
                    android:id="@+id/button20"
                    android:layout_width="60dp"
                    android:layout_height="60dp"
                    android:background="@drawable/button_blue_circle"
                    android:gravity="center"
                    android:text="20"
                    android:textColor="@android:color/white"
                    android:textSize="30sp"
                    app:layout_constraintBottom_toBottomOf="parent"
                    app:layout_constraintEnd_toEndOf="parent"
                    app:layout_constraintStart_toEndOf="@+id/button19"
                    app:layout_constraintTop_toTopOf="parent" />
            </androidx.constraintlayout.widget.ConstraintLayout>

            <androidx.constraintlayout.widget.ConstraintLayout
                android:id="@+id/line5"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:layout_marginTop="20dp"
                android:orientation="horizontal">

                <Button
                    android:id="@+id/button21"
                    android:layout_width="60dp"
                    android:layout_height="60dp"
                    android:background="@drawable/button_blue_circle"
                    android:gravity="center"
                    android:text="21"
                    android:textColor="@android:color/white"
                    android:textSize="30sp"
                    app:layout_constraintBottom_toBottomOf="parent"
                    app:layout_constraintEnd_toStartOf="@+id/button22"
                    app:layout_constraintStart_toStartOf="parent"
                    app:layout_constraintTop_toTopOf="parent" />

                <Button
                    android:id="@+id/button22"
                    android:layout_width="60dp"
                    android:layout_height="60dp"
                    android:background="@drawable/button_blue_circle"
                    android:gravity="center"
                    android:text="22"
                    android:textColor="@android:color/white"
                    android:textSize="30sp"
                    app:layout_constraintBottom_toBottomOf="parent"
                    app:layout_constraintEnd_toStartOf="@+id/button23"
                    app:layout_constraintStart_toEndOf="@+id/button21"
                    app:layout_constraintTop_toTopOf="parent" />

                <Button
                    android:id="@+id/button23"
                    android:layout_width="60dp"
                    android:layout_height="60dp"
                    android:background="@drawable/button_blue_circle"
                    android:gravity="center"
                    android:text="23"
                    android:textColor="@android:color/white"
                    android:textSize="30sp"
                    app:layout_constraintBottom_toBottomOf="parent"
                    app:layout_constraintEnd_toEndOf="parent"
                    app:layout_constraintStart_toStartOf="parent"
                    app:layout_constraintTop_toTopOf="parent" />

                <Button
                    android:id="@+id/button24"
                    android:layout_width="60dp"
                    android:layout_height="60dp"
                    android:background="@drawable/button_blue_circle"
                    android:gravity="center"
                    android:text="24"
                    android:textColor="@android:color/white"
                    android:textSize="30sp"
                    app:layout_constraintBottom_toBottomOf="parent"
                    app:layout_constraintEnd_toStartOf="@+id/button25"
                    app:layout_constraintStart_toEndOf="@+id/button23"
                    app:layout_constraintTop_toTopOf="parent" />

                <Button
                    android:id="@+id/button25"
                    android:layout_width="60dp"
                    android:layout_height="60dp"
                    android:background="@drawable/button_blue_circle"
                    android:gravity="center"
                    android:text="25"
                    android:textColor="@android:color/white"
                    android:textSize="30sp"
                    app:layout_constraintBottom_toBottomOf="parent"
                    app:layout_constraintEnd_toEndOf="parent"
                    app:layout_constraintStart_toEndOf="@+id/button24"
                    app:layout_constraintTop_toTopOf="parent" />
            </androidx.constraintlayout.widget.ConstraintLayout>
        </LinearLayout>
    </ScrollView>
</androidx.constraintlayout.widget.ConstraintLayout>
```

## Листинг 3: TableLayout (.xml)

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:padding="10dp"
    android:orientation="vertical"
    android:background="@color/greyExam">

    <ScrollView
        android:id="@+id/scrollView2"
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <TableLayout
            android:id="@+id/variants_layout"
            android:layout_width="match_parent"
            android:layout_height="wrap_content">

        </TableLayout>
    </ScrollView>
</LinearLayout>
```

## Листинг 4: TableLayout (.java)

```java
package com.example.germanexam;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

import androidx.appcompat.app.AppCompatActivity;

public class Variants extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.variants);

        defineButtons();
    }

    public void defineButtons() {
        int rows = 5;
        int columns = 5;

        TableLayout tableLayout = (TableLayout) findViewById(R.id.variants_layout);

        for (int i = 0; i < rows; i++) {

            TableRow tableRow = new TableRow(this);
            TableRow.LayoutParams paramsTable = new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            tableRow.setLayoutParams(paramsTable);

            for (int j = 0; j < columns; j++) {
                TableRow.LayoutParams paramsButton = new TableRow.LayoutParams(
                        TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                paramsButton.weight = 1.0f;
                paramsButton.topMargin = 20;
                paramsButton.leftMargin = 20;
                paramsButton.rightMargin = 20;
                paramsButton.bottomMargin = 20;
                Button button = new Button(this);
                button.setLayoutParams(paramsButton);
                button.setText("" + (j + 1 + (i * rows)));
                button.setId(j + 1 + (i * rows));
                button.setBackground(getResources().getDrawable(R.drawable.button_blue));
                button.setTextSize(30);
                button.setTextColor(Color.parseColor("#FFFFFF"));
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent intent = new Intent(Variants.this, VariantStartPage.class);
                            startActivity(intent);
                        } catch (Exception e) {

                        }
                    }
                });

                tableRow.addView(button, j);
            }

            tableLayout.addView(tableRow, i);
        }
    }
}
```