# Цели работы

- Ознакомиться с методом обработки жизненного цикла activity/fragment при помощи Lifecycle-Aware компонентов
- Изучить основные возможности навигации внутри приложения: создание новых activity, navigation graph

# 1. Обработка жизненного цикла с помощью Lifecycle-Aware компонентов

Ознакомьтесь с Lifecycle-Aware Components по документации и выполните codelabs.

## Step 1 - Setup Your Environment

На первом шаге нужно скачать код codelabs и запустить его конфигурацию Step 1, где находится таймер, который при повороте экрана сбрасывается.

## Step 2 - Add a ViewModel

На этом шаге используется `ViewModel` для сохранения состояния при поворотах экрана. Таймер сбрасывается, когда происходит изменение конфигурации, в нашем случае - это поворот экрана, который в свою очередь уничтожает Activity. Поэтому мы используем `ViewModel`, так как он не уничтожается, если его владелец уничтожается из-за изменения конфигурации (поворот экрана). Новый экземпляр владельца повторно подключается к существующей `ViewModel`. Если запустить код, то можно убедиться в этом: при повороте экрана или при переходе в другое в приложение и возвращении обратно таймер не сбрасывается.

## Step 3 - Wrap Data Using LiveData

На этом этапе заменяется таймер, который использовался в предыдущих шагах, на собственный, который использует таймер и обновляет пользовательский интерфейс каждую секунду. Данная логика реализована в классе `LiveDataTimerViewModel`.

Нас просят обновить ChronoActivity, для этого мы в классе `ChronoActivity3` в методе `subscribe()` создаём подписку:

```java
mLiveDataTimerViewModel.getElapsedTime().observe(this, elapsedTimeObserver);
```

Далее устанавливаем новое значение времени в классе `LiveDataTimerViewModel`:

```java
mElapsedTime.postValue(newValue);
```

Теперь при запуске приложения в Logcat журнал обновляется каждую секунду, если мы не перейдём в другое приложение, при этом таймер всё также будет работать и продолжать считать количество секунд.

## Step 4 - Subscribe to Lifecycle Events

На этом шаге нужно обновить класс под названием `BoundLocationManager`,  чтобы он учитывал жизненный цикл: он будет связываться, наблюдать и  реагировать на изменения в `LifecycleOwner`. Чтобы класс мог наблюдать за жизненным циклом Activity, мы должны добавить его в качестве наблюдателя. Для этого даём объекту `BoundLocationManager` команду наблюдать за жизненным циклом:

```java
lifecycleOwner.getLifecycle().addObserver(this);
```

Чтобы вызвать метод при изменении жизненного цикла, мы используем аннотацию `@OnLifecycleEvent`:

```java
@OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
void addLocationListener() {
    ...
}
@OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
void removeLocationListener() {
    ...
}
```

Теперь при запуске приложения в Logcat мы можем видеть, что наблюдатель добавлен или удалён.

## Step 5 - Share a ViewModel between Fragments

На данном этапе нужно разрешить обмен данными между фрагментами, используя `ViewModel`. Нашей задачей является соедниение фрагментов с `ViewModel`, чтобы при изменении одного `SeekBar` обновлялся другой `SeekBar`.

Для этого мы получаем SeekBarViewModel по Activity:

```java
mSeekBarViewModel = new ViewModelProvider(requireActivity()).get(SeekBarViewModel.class);
```

Далее устанавливаем значение для `ViewModel`, когда изменения происходят со стороны пользователя:

```java
mSeekBarViewModel.seekbarValue.setValue(progress);
```

И в итоге обновляем `SeekBar`, если `ViewModel` изменился:

```java
mSeekBarViewModel.seekbarValue.observe(
        requireActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer value) {
                if (value != null) {
                    mSeekBar.setProgress(value);
                }
            }
        });
```

После проделанных действий при изменении одного из `SeekBar`, своё значение меняет и другой `SeekBar`.

## Step 6 - Persist ViewModel state across process recreation (beta)

На заключительном шаге нашей задачей является сохранить состояние приложения, чтобы информация не была потеряна в случае остановки процесса. При запуске исходного кода приложения мы можем сохранить информацию в `ViewModel`, и если мы завершим процесс принудительно, то при перезапуске приложения мы увидим, что значение в `ViewModel` не сохранилось, однако `EditText` восстановил своё состояние. Это связано с тем, что некоторые элементы пользовательского интерфейса, включая `EditText`,  сохраняют своё состояние, используя собственную реализацию  `onSaveInstanceState`. Это состояние восстанавливается после завершения  процесса так же, как оно восстанавливается после изменения конфигурации.

Для сохранения информации в `ViewModel` нам нужно в файле `SavedStateViewModel.java` добавить новый конструктор, который принимает `SavedStateHandle` и сохраняет состояние в приватном поле:

```java
private SavedStateHandle mState;

public SavedStateViewModel(SavedStateHandle savedStateHandle) {
   mState = savedStateHandle;
}
```

Так как теперь мы используем поддержку модуля `LiveData`, то есть нам больше не нужно хранить и предоставлять `LiveData` в нашей `ViewModel`, то заменим наш геттер и `saveNewName`:

```java
private static final String NAME_KEY = "name";

LiveData<String> getName() {
    return mState.getLiveData(NAME_KEY);
}

void saveNewName(String newName) {
    mState.set(NAME_KEY, newName);
}
```

Теперь после проделанных шагов можно запустить приложение, сохранить информацию, завершить процесс с помощью `$ adb shell am kill com.example.android.codelabs.lifecycle`, и убедиться в том, что после повторного запуска приложения состояние в `ViewModel` сохранено.

После выполнения codelabs произведено знакомство со следующими компонентами жизненного цикла Activity:

- `ViewModel` - предоставляет способ создавать и извлекать объекты, привязанные к определенному жизненному циклу.
- `LifecycleOwner` - это интерфейс, реализованный классами `AppCompatActivity` и `Fragment`. Мы можем подписать другие компоненты на объекты-владельцы, которые реализуют данный интерфейс, чтобы наблюдать за изменениями в жизненном цикле владельца.
- `LiveData` - позволяет наблюдать за изменениями данных в нескольких компонентах приложения, не создавая явных жёстких путей зависимости между ними. Он учитывает сложные жизненные циклы компонентов приложения, включая действия, фрагменты, службы или любой `LifecycleOwner`, определённый в нашем приложении. `LiveData` управляет подписками наблюдателей, приостанавливая подписки на остановленные объекты `LifecycleOwner` и отменяя подписки на завершённые объекты `LifecycleOwner`.

# 2. Навигация (startActivityForResult)

Реализуйте навигацию между экранами одного приложения согласно изображению ниже с помощью Activity, Intent и метода `startActivityForResult`.

Так как я делаю лабораторные работы на основе своего курсового проекта, то Navigation Drawer по моему 4 варианту не совсем подходит для моего приложения, поэтому я решил использовать Bottom Navigation, который отлично вписывается в дизайн проекта.

## 1) Окно входа (MainActivity.java)

![](https://raw.githubusercontent.com/alexnevskiy/GermanExam/master/labs/images/MainActivity.png)

Данное окно появляется при входе в приложение, где пользователя просят ввести свои данные. При нажатии на кнопку "Далее" вызывается метод `startActivity()` для перехода в следующее окно (главное меню приложения) и метод `finish()`, чтобы данное окно не находилось в BackStack, так как оно использоваться больше не будет.

## 2) Главное меню (Menu.java)

![](https://raw.githubusercontent.com/alexnevskiy/GermanExam/master/labs/images/Menu.png)

В данном окне мы видим три большие синие кнопки "Экзамен", "Варианты ЕГЭ" и "Настройки", а также Bottom Navigation, при нажатии на который мы можем изменить свои данные, которые вводили ранее. Такой же Bottom Navigation также доступен и в окне настроек приложения. При нажатии на кнопку "Экзамен" будет вызыван метод `startActivityForResult()` с `requestCode` равным 0 для перехода в окно начала экзамена. В моём приложении requestCode = 0 означает главное меню, 1 - рабочий стол телефона, 2 - меню выбора варианта. При нажатии на кнопку "Варианты ЕГЭ" также вызывается `startActivityForResult()` с `requestCode` равным 0, только на этот раз для перехода в окно выбора варианта. Если нажать на кнопку "Настройки", то будет вызван метод `startActivity()`. При завершении Activity, которые запущены при нажатии на кнопки "Экзамен" и "Варианты ЕГЭ", получаем `resultCode`, который обрабатываем в `onActivityResult()`. Если код равен 2, то используем метод `startActivity()` для перехода в окно выбора варианта, если же код равен 1, то выполняется метод `finish()` для закрытия приложения.

Отдельное внимание стоит уделить Bottom Navigation, которое является аналогом Activity 'About'. В задании предполагается, что при нажатии на Bottom Navigation должно открыться другое Activity, в приложении же открывается диалоговое окно с возможностью изменения внесённых при первом запуске приложения данных. Использование диалогового окна для достаточно простой задачи (как в нашем случае) позволяет сократить число классов Activity в приложении, тем самым экономя ресурсы памяти.

![](https://raw.githubusercontent.com/alexnevskiy/GermanExam/master/labs/images/Name_Dialog.png)

## 3) Настройки (Settings.java)

![](https://raw.githubusercontent.com/alexnevskiy/GermanExam/master/labs/images/Settings.png)

Здесь мы видим введённые нами данные при запуске приложения, а также кнопки для проверки микрофона, настройки интерфейса и сведении о приложении (пока эти кнопки не работают). Также можно видеть Bottom Navigation, который ничем не отличается от описанного выше.

## 4) Варианты ЕГЭ (Variants.java)

![](https://raw.githubusercontent.com/alexnevskiy/GermanExam/master/labs/images/Variants.png)

В этом окне находятся кнопки выбора варианта, пока что их 25 штук. При нажатии на кнопку вызывается метод `startActivityForResult()` с `requestCode` равным 2. Здесь же обрабатываем завершённые до этого Activity в методе `onActivityResult()`: `resultCode` = 1 - устанавливаем код 1 при помощи метода `setResult()`, `resultCode` = 0 - вызываем метод `finish()` и переходим в главное меню.

## 5) Стартовая страница (VariantStartPage.java)

![](https://raw.githubusercontent.com/alexnevskiy/GermanExam/master/labs/images/VariantStartPage.png)

В данное окно можно перейти по кнопке "Экзамен" в главном меню и по любой кнопке в меню выбора варианта. В данном окне одна кнопка для перехода к первому заданию, при нажатии на которую вызывается метод `startActivityForResult()` с `requestCode` равным 0. При нажатии на системную кнопку "Назад" открывается диалоговое окно с тремя кнопками: "Рабочий стол", "Главное меню" и "Выбор варианта". При нажатии на "Рабочий стол" вызывается метод `setResult()` с кодом 1 и метод `finish()`. При нажатии на "Главное меню" вызывается метод `setResult()` с кодом 0 и метод `finish()`. При нажатии на "Выбор варианта" вызывается метод `setResult()` с кодом 2 и метод `finish()`. Здесь же обрабатываем завершённые до этого Activity в методе `onActivityResult()`: `resultCode` = 2 - `setResult()` с кодом 2 и `finish()`, `resultCode` = 1 - `setResult()` с кодом 1 и `finish()`, `resultCode` = 0 - `setResult()` с кодом 0 и `finish()`.

![](https://raw.githubusercontent.com/alexnevskiy/GermanExam/master/labs/images/VariantStartPageDialog.png)

## 6) Первое задание (TaskOne.java)

![](https://raw.githubusercontent.com/alexnevskiy/GermanExam/master/labs/images/TaskOneDialog.png)

В данном окне не содержится никаких кнопок. При нажатии на системную кнопку "Назад" также открывается диалоговое окно с тремя кнопками: "Рабочий стол", "Главное меню" и "Выбор варианта". Вызываются такие же методы, что и в прошлом окне. Если бы пользователь нажимал на кнопки в следующем порядке начиная с главного меню: Экзамен - "Prüfung starten", то BackStack был бы следующим: Главное меню - Стартовая страница - Первое задание.

# 3. Навигация (флаги Intent/атрибуты Activity)

Решите предыдущую задачу с помощью Activity, Intent и флагов Intent либо атрибутов Activity.

Дополните граф навигации новым(-и) переходом(-ами) с целью демонстрации  какого-нибудь (на свое усмотрение) атрибута Activity или флага Intent,  который еще не использовался для решения задачи. Поясните пример и  работу флага/атрибута.

В этом пункте объединены задачи 3 и 4. Изменения коснулись не всех Activity, а только "Menu", "Variants", "VariantStartPage" и "TaskOne".

## Главное меню (Menu.java)

Здесь убраны все `startActivityForResult()` и заменены на обычные `startActivity()`, но с добавлением флага Intent. Теперь при нажатии на кнопку "Экзамен" в Intent при помощи метода `addFlags()` добавляется флаг `FLAG_ACTIVITY_NO_HISTORY`, который означает, что новое открытое окно не будет добавляться в историю, то есть в BackStack, и при открытии следующего нового Activity предыдущем в стеке будет "Главное меню", а не "Стартовая страница". Данный флаг прописан из-за того, что наличие Activity "Стартовая страница" просто не нужно, так как при нажатии системной кнопки "Назад" пользователь всё равно в него никогда больше не обратится, тем самым икономя системные ресурсы.

## Варианты ЕГЭ (Variants.java)

В данном Activity также заменён `startActivityForResult()` на `startActivity()` с добавленным флагом `FLAG_ACTIVITY_NO_HISTORY` в Intent. Флаг прописан всё по той же причине - отсутствие надобности окна "Стартовая страница" в BackStack.

## Стартовая страница (VariantStartPage.java)

При нажатии на кнопку для перехода к первому заданию также вызывается метод `startActivity()` с добавленным флагом `FLAG_ACTIVITY_NO_HISTORY` в Intent. Причина добавления флага всё та же. В диалоговом окне, которое вызывается при нажатии на системную кнопку "Назад", остались всё те же три кнопки. При нажатии на "Главное меню" вызывается метод `startActivity()` с добавленным флагом `FLAG_ACTIVITY_CLEAR_TOP` в Intent. Данный флаг означает, что если экземпляр данной Activity уже существует в BackStack, то все Activity, находящиеся поверх неё, разрушаются, и этот экземпляр становится вершиной стека, то есть пользователь снова возвращается в главное меню. При нажатии на "Рабочий стол" вызывается метод `finishAffinity()`, который завершает текущее Activity, а также все Activity, расположенные под ним в BackStack. Если нажать на "Выбор варианта", то происходит всё тоже самое, как и при нажатии на кнопку "Главное меню", только происходит переход в Activity "Варианты ЕГЭ".

## Первое задание (TaskOne.java)

Здесь при нажатии на системную кнопку "Назад" также всплывается диалоговое окно с тремя кнопками. При нажатии на кнопки происходит всё тоже самое, как описано выше.

# 4. Навигация (Fragments, Navigation Graph)

Решите предыдущую задачу (с расширенным графом) с использованием  navigation graph. Все Activity должны быть заменены на фрагменты, кроме Activity 'About', которая должна остаться самостоятельной Activity. В отчете сравните все решения.

![](https://raw.githubusercontent.com/alexnevskiy/GermanExam/master/labs/images/navigation_graph.png)

![](https://raw.githubusercontent.com/alexnevskiy/GermanExam/master/labs/images/navigation_exam_graph.png)

Для выполнения данного задания создан отдельный проект, где все Activity заменены на фрагменты, Activity 'About' всё также осталось диалоговым окном. Для того, чтобы layout отображался в fragment, используется метод `onCreateView()`, а для регистрации нажатий на кнопки используетсяся метод `onActivityCreated()`. Навигация осуществлялась при помощи метода `Navigation.findNavController(view).navigate()` при нажатии на кнопки. Так как в приложении в меню предусмотрена ориентация `"portrait"`, а во время самого экзамена `"userLandscape"`, то в графе при переходе на стартовую страницу экзамена стоит не фрагмент, а Activity, у которой в AndroidManifest прописана ориентация `"userLandscape"`, в то время как у стартовой MainActivity - `"portrait"`. У Activity для экзамена предусмотрен свой собственный граф, который состоит из двух фрагментов.

К сожалению не удолось реализовать выбор выхода из окна экзамена, как это было сделано при помощи флагов в Intent.

# Выводы:

В процессе выполнения данной лабораторной работы в среде разработки Android Studio изучены: методы обработки жизненного цикла activity/fragment при помощи Lifecycle-Aware компонентов, а также основные возможности навигации внутри приложения: созданиие новых activity, navigation graph. Если сравнивать все решения между собой, то можно придти к выводу, что самым практичным является решение с использованием navigation graph. Он достаточно удобен для приложений за счёт того, что разработчик использует фрагменты, которые можно спокойно комбинировать между собой и заново их использовать, однако мне больше по душе использование Intent с флагами.

# Приложение:

## Листинг 1: MainActivity.java

```java
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button buttonStart = findViewById(R.id.buttonStart);

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Menu.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
```

## Листинг 2: Menu.java

```java
public class Menu extends AppCompatActivity {

    private long backPressedTime;
    private Toast backToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_map:
                        AlertDialog.Builder builder = new AlertDialog.Builder(Menu.this);
                        LayoutInflater inflater = Menu.this.getLayoutInflater();
                        builder.setView(inflater.inflate(R.layout.name_dialog, null))
                                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        // Сохранение данных пользователя
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                }
                return true;
            }
        });

        Button buttonExam = findViewById(R.id.button_exam);

        buttonExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Menu.this, VariantStartPage.class);
                startActivityForResult(intent, 0);
            }
        });

        Button buttonVariants = findViewById(R.id.button_variants);

        buttonVariants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Menu.this, Variants.class);
                startActivityForResult(intent, 0);
            }
        });

        Button buttonSettings = findViewById(R.id.button_settings);

        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Menu.this, Settings.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            return;
        } else {
            backToast = Toast.makeText(getBaseContext(), "Нажмите ещё раз, чтобы выйти", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 2) {
            Intent intent = new Intent(Menu.this, Variants.class);
            startActivity(intent);
        }
        if (resultCode == 1) {
            finish();
        }
    }
}
```

## Листинг 3: Settings.java

```java
public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_map:
                        AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
                        LayoutInflater inflater = Settings.this.getLayoutInflater();
                        builder.setView(inflater.inflate(R.layout.name_dialog, null))
                                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        // Сохранение данных пользователя
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                }
                return true;
            }
        });
    }
}
```

## Листинг 4: Variants.java

```java
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

        TableLayout tableLayout = findViewById(R.id.variants_layout);

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
                        Intent intent = new Intent(Variants.this, VariantStartPage.class);
                        startActivityForResult(intent, 2);
                    }
                });

                tableRow.addView(button, j);
            }

            tableLayout.addView(tableRow, i);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            setResult(1);
            finish();
        }
        if (resultCode == 0) {
            finish();
        }
    }
}
```

## Листинг 5: VariantStartPage.java

```java
public class VariantStartPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.variant_one_start);
        Button buttonVariants = findViewById(R.id.button_start_test);

        buttonVariants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VariantStartPage.this, TaskOne.class);
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_window_title);
        builder.setNegativeButton(R.string.menu, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                setResult(0);
                finish();
            }
        });
        builder.setNeutralButton(R.string.desktop, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                setResult(1);
                finish();
            }
        });
        builder.setPositiveButton(R.string.variants_menu, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                setResult(2);
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 2) {
            setResult(2);
            finish();
        }
        if (resultCode == 1) {
            setResult(1);
            finish();
        }
        if (requestCode == 0) {
            setResult(0);
            finish();
        }
    }
}
```

## Листинг 6: TaskOne.java

```java
public class TaskOne extends AppCompatActivity {

    long timeLeft = 90000;
    int counter = 0;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task1);
        final TextView timeRemaining = findViewById(R.id.time_remaining);
        final ProgressBar timeline = findViewById(R.id.timeline);
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
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_window_title);
        builder.setNegativeButton(R.string.menu, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                setResult(0);
                finish();
            }
        });
        builder.setNeutralButton(R.string.desktop, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                setResult(1);
                finish();
            }
        });
        builder.setPositiveButton(R.string.variants_menu, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                setResult(2);
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
```

## Листинг 7: Menu.java с флагами

```java
public class Menu extends AppCompatActivity {

    private long backPressedTime;
    private Toast backToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_map:
                        AlertDialog.Builder builder = new AlertDialog.Builder(Menu.this);
                        LayoutInflater inflater = Menu.this.getLayoutInflater();
                        builder.setView(inflater.inflate(R.layout.name_dialog, null))
                                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        // Сохранение данных пользователя
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                }
                return true;
            }
        });

        Button buttonExam = findViewById(R.id.button_exam);

        buttonExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Menu.this, VariantStartPage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });

        Button buttonVariants = findViewById(R.id.button_variants);

        buttonVariants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Menu.this, Variants.class);
                startActivity(intent);
            }
        });

        Button buttonSettings = findViewById(R.id.button_settings);

        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Menu.this, Settings.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            return;
        } else {
            backToast = Toast.makeText(getBaseContext(), "Нажмите ещё раз, чтобы выйти", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }
}
```

## Листинг 8: Variants.java с флагами

```java
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

        TableLayout tableLayout = findViewById(R.id.variants_layout);

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
                        Intent intent = new Intent(Variants.this, VariantStartPage.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                    }
                });

                tableRow.addView(button, j);
            }

            tableLayout.addView(tableRow, i);
        }
    }
}
```

## Листинг 9: VariantStartPage.java с флагами

```java
public class VariantStartPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.variant_one_start);
        Button buttonVariants = findViewById(R.id.button_start_test);

        buttonVariants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VariantStartPage.this, TaskOne.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_window_title);
        builder.setNegativeButton(R.string.menu, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(VariantStartPage.this, Menu.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        builder.setNeutralButton(R.string.desktop, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finishAffinity();
            }
        });
        builder.setPositiveButton(R.string.variants_menu, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(VariantStartPage.this, Variants.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
```

## Листинг 10: TaskOne.java с флагами

```java
public class TaskOne extends AppCompatActivity {

    long timeLeft = 90000;
    int counter = 0;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task1);
        final TextView timeRemaining = findViewById(R.id.time_remaining);
        final ProgressBar timeline = findViewById(R.id.timeline);
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
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_window_title);
        builder.setNegativeButton(R.string.menu, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(TaskOne.this, Menu.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        builder.setNeutralButton(R.string.desktop, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finishAffinity();
            }
        });
        builder.setPositiveButton(R.string.variants_menu, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(TaskOne.this, Variants.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
```

## Листинг 11: activity_main.xml с фрагментом

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <fragment
        android:id="@+id/activity_main_fragment"
        android:name="androidx.navigation.fragment.NavHostFragment"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:defaultNavHost="true"
        app:navGraph="@navigation/navigation_graph"
        tools:layout_editor_absoluteX="1dp"
        tools:layout_editor_absoluteY="1dp" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

## Листинг 12: fragment_exam.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".ExamActivity">

    <fragment
        android:id="@+id/fragment"
        android:name="androidx.navigation.fragment.NavHostFragment"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:defaultNavHost="true"
        app:navGraph="@navigation/navigation_exam_graph"
        tools:layout_editor_absoluteX="1dp"
        tools:layout_editor_absoluteY="1dp" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

## Листинг 13: navigation_graph.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<navigation
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/navigation_graph"
    app:startDestination="@id/mainFragment">

    <fragment
        android:id="@+id/mainFragment"
        android:name="com.example.germanexamwithfragment.MainFragment"
        android:label="fragment_main"
        tools:layout="@layout/fragment_main" >
        <action
            android:id="@+id/main_to_menu"
            app:destination="@id/menuFragment" />
    </fragment>
    <fragment
        android:id="@+id/menuFragment"
        android:name="com.example.germanexamwithfragment.MenuFragment"
        android:label="fragment_menu"
        tools:layout="@layout/fragment_menu" >
        <action
            android:id="@+id/menu_to_settings"
            app:destination="@id/settingsFragment" />
        <action
            android:id="@+id/menu_to_variants"
            app:destination="@id/variantsFragment" />
        <action
            android:id="@+id/menu_to_exam"
            app:destination="@id/examActivity2" />
    </fragment>
    <fragment
        android:id="@+id/settingsFragment"
        android:name="com.example.germanexamwithfragment.SettingsFragment"
        android:label="settings_menu"
        tools:layout="@layout/fragment_settings" />
    <fragment
        android:id="@+id/variantsFragment"
        android:name="com.example.germanexamwithfragment.VariantsFragment"
        android:label="fragment_variants"
        tools:layout="@layout/fragment_variants" >
        <action
            android:id="@+id/variants_to_exam"
            app:destination="@id/examActivity2" />
    </fragment>
    <activity
        android:id="@+id/examActivity2"
        android:name="com.example.germanexamwithfragment.ExamActivity"
        android:label="fragment_variant_start_page"
        tools:layout="@layout/fragment_variant_start_page" />
</navigation>
```

## Листинг 14: navigation_exam_graph.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<navigation
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/navigation_exam_graph"
    app:startDestination="@id/variantStartPageFragment">
    <fragment
        android:id="@+id/variantStartPageFragment"
        android:name="com.example.germanexamwithfragment.VariantStartPageFragment"
        android:label="fragment_variant_start_page"
        tools:layout="@layout/fragment_variant_start_page">
        <action
            android:id="@+id/start_page_to_task_one"
            app:destination="@id/taskOneFragment" />
    </fragment>
    <fragment
        android:id="@+id/taskOneFragment"
        android:name="com.example.germanexamwithfragment.TaskOneFragment"
        android:label="fragment_task_one"
        tools:layout="@layout/fragment_task_one" />
</navigation>
```

## Листинг 15: MainActivity.java для фрагментов

```java
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
```

## Листинг 16: ExamActivity.java

```java
public class ExamActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_exam);
    }
}
```

## Листинг 17: MainFragment.java

```java
public class MainFragment extends Fragment {

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button button = getView().findViewById(R.id.buttonStart);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.main_to_menu);
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }
}
```

## Листинг 18: MenuFragment.java

```java
public class MenuFragment extends Fragment {
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        BottomNavigationView bottomNavigationView = getView().findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_map:
                        AlertDialog.Builder builder = new AlertDialog.Builder(MenuFragment.this.getContext());
                        LayoutInflater inflater = MenuFragment.this.getLayoutInflater();
                        builder.setView(inflater.inflate(R.layout.name_dialog, null))
                                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        // Сохранение данных пользователя
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                }
                return true;
            }
        });

        Button buttonExam = getView().findViewById(R.id.button_exam);

        buttonExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.menu_to_exam);
            }
        });

        Button buttonVariants = getView().findViewById(R.id.button_variants);

        buttonVariants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.menu_to_variants);
            }
        });

        Button buttonSettings = getView().findViewById(R.id.button_settings);

        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.menu_to_settings);
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }
}
```

## Листинг 19: SettingsFragment.java

```java
public class SettingsFragment extends Fragment {

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        BottomNavigationView bottomNavigationView = getView().findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_map:
                        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsFragment.this.getContext());
                        LayoutInflater inflater = SettingsFragment.this.getLayoutInflater();
                        builder.setView(inflater.inflate(R.layout.name_dialog, null))
                                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        // Сохранение данных пользователя
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                }
                return true;
            }
        });
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }
}
```

## Листинг 20: VariantsFragment.java

```java
public class VariantsFragment extends Fragment {

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        defineButtons();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_variants, container, false);
    }

    public void defineButtons() {
        int rows = 5;
        int columns = 5;

        TableLayout tableLayout = getView().findViewById(R.id.variants_layout);

        for (int i = 0; i < rows; i++) {

            TableRow tableRow = new TableRow(this.getContext());
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
                Button button = new Button(this.getContext());
                button.setLayoutParams(paramsButton);
                button.setText("" + (j + 1 + (i * rows)));
                button.setId(j + 1 + (i * rows));
                button.setBackground(getResources().getDrawable(R.drawable.button_blue));
                button.setTextSize(30);
                button.setTextColor(Color.parseColor("#FFFFFF"));
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Navigation.findNavController(view).navigate(R.id.variants_to_exam);
                    }
                });

                tableRow.addView(button, j);
            }

            tableLayout.addView(tableRow, i);
        }
    }
}
```

## Листинг 21: VariantStartPageFragment.java

```java
public class VariantStartPageFragment extends Fragment {

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button button = getView().findViewById(R.id.button_start_test);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.start_page_to_task_one);
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_variant_start_page, container, false);
    }
}
```

## Листинг 22: TaskOneFragment.java

```java
public class TaskOneFragment extends Fragment {

    long timeLeft = 90000;
    int counter = 0;
    CountDownTimer countDownTimer;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final TextView timeRemaining = getView().findViewById(R.id.time_remaining);
        final ProgressBar timeline = getView().findViewById(R.id.timeline);
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task_one, container, false);
    }
}
```