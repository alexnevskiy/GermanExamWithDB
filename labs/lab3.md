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

В данном окне мы видим три большие синие кнопки "Экзамен", "Варианты ЕГЭ" и "Настройки", а также Bottom Navigation, при нажатии на который мы можем изменить свои данные, которые вводили ранее. Такой же Bottom Navigation также доступен и в окне настроек приложения. При нажатии на кнопку "Экзамен" будет вызыван метод `startActivityForResult()` с `requestCode` равным 0 для перехода в окно начала экзамена. В моём приложении requestCode = 0 означает главное меню, 1 - рабочий стол телефона, 2 - меню выбора варианта. При нажатии на кнопку "Варианты ЕГЭ" также вызывается `startActivityForResult()` с `requestCode` равным 0, только на этот раз для перехода в окно выбора варианта. Если нажать на кнопку "Настройки", то будет вызван метод `startActivity()`. При нажатии на Bottom Navigation также вызывается метод `startActivity()`. При завершении Activity, которые запущены при нажатии на кнопки "Экзамен" и "Варианты ЕГЭ", получаем `resultCode`, который обрабатываем в `onActivityResult()`. Если код равен 2, то используем метод `startActivity()` для перехода в окно выбора варианта, если же код равен 1, то выполняется метод `finish()` для закрытия приложения.

## 3) Настройки (Settings.java)

![](https://raw.githubusercontent.com/alexnevskiy/GermanExam/master/labs/images/Settings.png)

Здесь мы видим введённые нами данные при запуске приложения, а также кнопки для проверки микрофона, настройки интерфейса и сведении о приложении (пока эти кнопки не работают). Также можно видеть Bottom Navigation, при нажатии на который вызывается метод `startActivity()`.

## 4) Изменение данных (Name.java)

![](https://raw.githubusercontent.com/alexnevskiy/GermanExam/master/labs/images/Name.png)

Данное окно является аналогом Activity 'About' из лабораторной работы. Здесь можно изменить введённые пользователем данные и сохранить их. Для перехода в предыдущее окно используем системную кнопку "Назад". Если бы пользователь нажимал на кнопки в том порядке, в котором идёт рассмотрение Activity, то BackStack был бы следующим: Главное меню - Настройки - Изменение данных.

## 5) Варианты ЕГЭ (Variants.java)

![](https://raw.githubusercontent.com/alexnevskiy/GermanExam/master/labs/images/Variants.png)

В этом окне находятся кнопки выбора варианта, пока что их 25 штук. При нажатии на кнопку вызывается метод `startActivityForResult()` с `requestCode` равным 2. Здесь же обрабатываем завершённые до этого Activity в методе `onActivityResult()`: `resultCode` = 1 - устанавливаем код 1 при помощи метода `setResult()`, `resultCode` = 0 - вызываем метод `finish()` и переходим в главное меню.

## 6) Стартовая страница (VariantStartPage.java)

![](https://raw.githubusercontent.com/alexnevskiy/GermanExam/master/labs/images/VariantStartPage.png)

В данное окно можно перейти по кнопке "Экзамен" в главном меню и по любой кнопке в меню выбора варианта. В данном окне одна кнопка для перехода к первому заданию, при нажатии на которую вызывается метод `startActivityForResult()` с `requestCode` равным 0. При нажатии на системную кнопку "Назад" открывается диалоговое окно с тремя кнопками: "Рабочий стол", "Главное меню" и "Выбор варианта". При нажатии на "Рабочий стол" вызывается метод `setResult()` с кодом 1 и метод `finish()`. При нажатии на "Главное меню" вызывается метод `setResult()` с кодом 0 и метод `finish()`. При нажатии на "Выбор варианта" вызывается метод `setResult()` с кодом 2 и метод `finish()`. Здесь же обрабатываем завершённые до этого Activity в методе `onActivityResult()`: `resultCode` = 2 - `setResult()` с кодом 2 и `finish()`, `resultCode` = 1 - `setResult()` с кодом 1 и `finish()`, `resultCode` = 0 - `setResult()` с кодом 0 и `finish()`.

![](https://raw.githubusercontent.com/alexnevskiy/GermanExam/master/labs/images/VariantStartPageDialog.png)

## 7) Первое задание (TaskOne.java)

![](https://raw.githubusercontent.com/alexnevskiy/GermanExam/master/labs/images/TaskOneDialog.png)

В данном окне не содержится никаких кнопок. При нажатии на системную кнопку "Назад" также открывается диалоговое окно с тремя кнопками: "Рабочий стол", "Главное меню" и "Выбор варианта". Вызываются такие же методы, что и в прошлом окне. Если бы пользователь нажимал на кнопки в следующем порядке начиная с главного меню: Экзамен - "Prüfung starten", то BackStack был бы следующим: Главное меню - Стартовая страница - Первое задание.

