# Цели работы

- Ознакомиться с методом обработки жизненного цикла activity/fragment при помощи Lifecycle-Aware компонентов
- Изучить основные возможности навигации внутри приложения: создание новых activity, navigation graph

# 1. Обработка жизненного цикла с помощью Lifecycle-Aware компонентов

Ознакомьтесь с Lifecycle-Aware Components по документации и выполните codelabs.

## Step 1 - Setup Your Environment

На первом шаге нужно скачать код codelabs и запустить его конфигурацию Step 1, где находится таймер, который при повороте экрана сбрасывается.

## Step 2 - Add a ViewModel

На этом шаге используется ViewModel для сохранения состояния при поворотах экрана. Таймер сбрасывается, когда происходит изменение конфигурации, в нашем случае - это поворот экрана, который в свою очередь уничтожает Activity. Поэтому мы используем ViewModel, так как он не уничтожается, если его владелец уничтожается из-за изменения конфигурации (поворот экрана). Новый экземпляр владельца повторно подключается к существующей ViewModel. Если запустить код, то можно убедиться в этом: при повороте экрана или при переходе в другое в приложение и возвращении обратно таймер не сбрасывается.

## Step 3 - Wrap Data Using LiveData

На этом этапе заменяется таймер, который использовался в предыдущих шагах, на собственный, который использует таймер и обновляет пользовательский интерфейс каждую секунду. Данная логика реализована в классе LiveDataTimerViewModel.

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



После выполнения codelabs произведено знакомство со следующими компонентами жизненного цикла Activity:

- ViewModel - предоставляет способ создавать и извлекать объекты, привязанные к определенному жизненному циклу.
- LifecycleOwner - это интерфейс, реализованный классами AppCompatActivity и Fragment. Мы можем подписать другие компоненты на объекты-владельцы, которые реализуют данный интерфейс, чтобы наблюдать за изменениями в жизненном цикле владельца.
- LiveData - позволяет наблюдать за изменениями данных в нескольких компонентах приложения, не создавая явных жёстких путей зависимости между ними. Он учитывает сложные жизненные циклы компонентов приложения, включая действия, фрагменты, службы или любой LifecycleOwner, определённый в нашем приложении. LiveData управляет подписками наблюдателей, приостанавливая подписки на остановленные объекты LifecycleOwner и отменяя подписки на завершённые объекты LifecycleOwner.

# 2. Навигация (startActivityForResult)

