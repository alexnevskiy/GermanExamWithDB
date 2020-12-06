# Цели работы

Получить практические навыки разработки многопоточных приложений:

1. Организация обработки длительных операций в background (worker) thread:
   - Запуск фоновой операции (coroutine/asynctask/thread)
   - Остановка фоновой операции (coroutine/asynctask/thread)
2. Публикация данных из background (worker) thread в main (ui) thread.

Освоить 3 основные группы API для разработки многопоточных приложений:

1. Kotlin Coroutines
2. AsyncTask
3. Java Threads

# 1. Альтернативные решения задачи "не секундомер" из Лаб. 2

Используйте приложение "не секундомер", получившееся в результате выполнения Лабораторной работы №2. Разработайте несколько альтернативных приложений "не секундомер",  отличающихся друг от друга организацией многопоточной работы. Опишите все известные Вам решения.

## Java Threads

При помощи Java Threads данную задачу можно решить двумя способами: в данном нам из 2 лабораторной работы "голом" виде, то есть ручное создание потоков в приложении, или же более продвинутом - использование `Executor`'ов. Я буду использовать Java Threads напрямую.

### Использование Java Threads напрямую

Для решения данной задачи немного перепишем получившийся в итоге выполнения 2 лабораторной работы код приложения continuewatch. 

В получившемся решении была упущена одна ключевая ошибка: при разрушении и пересоздании Activity создавался новый поток, и не уничтожался старый, то есть в системе накапливались рабочие background потоки, которые нигде не использовались, тем самым тратя драгоценные ресурсы системы. Даже если Activity не пересоздавалось, а просто находилось в состоянии `onStop` или `onPause`, например, при нажатии на системную кнопку "Домой", счётчик секунд хоть и не работал, так как у нас был написан специально для этого флаг, но сам поток был в активном состоянии, что является неправильным решением. Поэтому данная неточность также учтена при переписывании приложения.

Вынесем инициализацию потока в отдельную приватную функцию `createThread()`, которую будем вызывать при необходимости создания потока, а переменную `backgroundThread` с типом `Thread` сделаем `null` и добавим ей аннотацию `@Volatile`, которая гарантирует доступ к переменной из разных потоков. Также немного изменилось создание потока: теперь внутри цикла используется конструкция `try catch` для того, чтобы поймать исключение `InterruptedException`. Для чего мы это используем будет описано немного дальше. Теперь в методе `onCreate` вызываем написанный нами приватный метод для создания потока при создании Activity. Для того, чтобы прекратить работу потока, когда приложение не отображается на экране, в методе `onPause()` вызывается метод `interrupt()` для нашего background потока. Данный метод выставляет флаг `interuppted` внутри нашего потока в `true`, и разблокирует методы, которые знают об этом флаге для выбрасывания того самого `InterruptedException`, чтобы мы могли проконтролировать завершение потока в конструкции `try catch`. Обычно это системные методы, и одним из таких методов является использующийся в приложении метод `sleep()`, который и выбрасывает исключение. При выбрасывании исключения мы просто выходим из цикла через `break`, тем самым завершая работу потока, и присваиваем переменной `backgroundThread` значение `null`, чтобы полностью избавиться от нерабочего потока в приложении. 

Так как в методе `onPause()` поток уничтожается, то при повторном открытии приложения некому будет производить подсчёт секунд. Поэтому в методе `onResume()` делается проверка на наличие активного фонового потока: если он не наблюдается, то есть `backgroundThread == null`, то мы создаём новый поток при помощи нашего приватного метода `createThread()`. Данная конструкция позволяет нам устранить ту самую неточность, при которой мы могли просто свернуть приложение, а поток всё равно был бы активен, ведь метод `onCreate()` вместе с `createThread()` не будет вызываться, Activity же не было разрушено. Также при помощи этой проверки мы избегаем появления ещё одного потока после создания первого в `onCreate()`. Если бы он всё-таки каким-то образом создавался, то наш таймер считал бы по 2 секунды, а не по 1. Как мы помним из той же 2 лабораторной работы, порядок вызова жизненных методов Activity следующий: `setContentView(), onCreate(), onStart()` и `onResume()`. То есть теоретически мы можем создать 2 потока в одну и ту же переменную при создании Activity, но этого не происходит из-за того, что `backgroundThread` больше не является `null` после создания в методе `onCreate()`.

Также убран флаг `run`, который раньше использовался для подсчёта секунд, за ненадобностью, ведь теперь поток при отсутствии приложения на экране уничтожается и подсчёта секунд не происходит.

После выполнения работы над кодом проведён небольшой стресс-тест приложения. Всё работает правильно, секунды показываются, счётчик не сбрасывается и считает, только когда приложение активно, то есть показывается на экране устройства.

## AsyncTask

Для решения задачи при помощи уже устаревшей на данный момент `AsyncTask` возьмём за основу код на Java Threads и изменим его.

Так как с классом `AsyncTask` напрямую работать нельзя, то создадим свой класс `AsyncTaskCounter`, который наследуется от `AsyncTask`, внутри класса нашего Activity и реализуем его методы. Так как у нас нет никаких возвращаемых значений и прочего, то в качестве параметров при инициализации класса везде просто укажем `Void`. На вход нашему классу мы подаём Activity для того, чтобы в дальнейшем отображать правильное количество секунд, проведённое в приложении. При наследовании класса `AsyncTask` обязательным методом является `doInBackground()`, который нужно всегда переопределять, поэтому с него и начнём.

Метод `doInBackground()` выполняется в новом потоке и не имеет доступа к UI потоку, поэтому здесь реализована наша "тяжеловесная" операция в виде `TimeUnit.SECONDS.sleep(1)`, которая и позволяет счётчику увеличивать своё значение 1 раз в секунду. Данная операция производится в "бесконечном" цикле `while (!isCancelled)`, где `isCancelled` возвращает значение типа `Boolean` об отмене нашей задачи, то есть цикл работает до тех пор, пока мы не остановим его, например, путём закрытия окна. Далее вызывается метод `publishProgress()`, который позволяет показать промежуточные результаты в методе `onProgressUpdate()`, то есть сколько секунд приложение активно. В конце для проверки того, что поток действительно закрывается и не выполняется где-то в устройстве дальше, выводим в логи хэшкод нашего `AsyncTaskCounter`'а. Так как метод `doInBackground()` должен возвращать результат последнего типа, который указан в параметрах класса, но у нас там написано `Void`, поэтому пишем `return null`, возвращать нам нечего.

Метод `onProgressUpdate()` хоть и не обязательно переопределять, но мы же вызывали `publishProgress()` мгновением ранее, поэтому напишем его реализацию. Данный метод предназначен для отображения промежуточных результатов в главном UI потоке приложения, так как имеет к нему доступ. В нашем случае мы вызываем знакомую функцию `secondsDisplay()` для отображения времени в `TextView` приложения.

Метод `onCancelled()` переопределён лишь для того, чтобы в логах зафиксировать отмену нашей задачи.

Теперь рассмотрим код `AsyncTaskActivity`. Создаём свойство `task` типа `AsyncTaskCounter` для того, чтобы в нём хранить экземпляр нашего написанного класса. В методе `onCreate()` присваиваем `task`'у экземпляр класса и вызываем метод `execute()` для выполнения нашей задачи в background потоке. В методе `onResume()` используем проверку, похожую на проверку в решении при помощи Java Threads: если задача отменена, то присваиваем экземпляр класса и вызываем снова метод `execute()`. Данный блок кода всё также нужен для того, чтобы при сворачивании приложения и его повторном открытии без использования метода `onCreate()`, где предполагается основной запуска background потока, у нас всё дальше работало и показывался счётчик на экране. В методе `onPause()` вызываем `cancel(true)` для того, чтобы отменить выполнение задачи в потоке.

При запуске приложения, реализованного при помощи `AsyncTask` всё работает как надо, при небольшом стресс-тесте в виде поворота телефона, выключении экрана, режима Spleet Screen и прочего проблем не выявлено.

Так как в нашем случае параметры `AsyncTask`'a являются `Void`, то мы не совсем задумывались над тем, зачем они вообще нужны, и как мы их используем, поэтому опишу их для `AsyncTask<Params, Progress, Result>`:

1. `Params` - это тип параметров, отправляемых задаче при выполнении, то есть они идут на вход метода `doInBackground()`.
2. `Progress` - это тип параметров, которые отображают промежуточные результаты во время фоновых вычислений в методе `onProgressUpdate()`.
3. `Result` - это тип результата вычислений в фоновом потоке, возвращается в методе `doInBackground()` и подаётся на вход методу `onPostExecute()`, который у нас не использовался.

## Kotlin Coroutines

Чтобы Kotlin Coroutins можно было пользоваться, добавим новую зависимость в `build.gradle`:

```groovy
implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.7"
implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.2"
```

### Создание Kotlin Coroutines при помощи CoroutineScope

Для решения данной задачи создадим новый класс `CoroutineActivity`, в котором инициализируем потоки при помощи coroutine.

Начнём с того, что же вообще такое Kotlin Coroutines. Корутина или же сопрограмма - это паттерн проектирования параллелизма, который используется для упрощения кода, выполняющийся асинхронно. При определении корутины необходимо также указать её область действия (Coroutine Scope). Coroutine Scope управляет одним или несколькими связанными корутинами. Kotlin coroutines используют диспетчеры, чтобы определить, какие потоки используются для выполнения.

При описании класса создаются два свойства: `scope` - хранит в себе `CoroutineScope` с дефолтным диспетчером и `job` - в неё мы далее определим `Job` при запуске корутины. 

В методе `onCreate()` вызываем приватную функцию `startCoroutine()`, в которой создаются корутины. В самой функции запускаем работу нашего `scope` при помощи функции `launch()`, где пишем, что должна делать корутина. В бесконечном цикле вызывается `suspend` (прерывающая) функция `delay(1000)`, которая останавливает выполнение корутины в потоке на 1 секунду, не блокируя его при этом. Далее вызываем новый `launch()`, где прописываем, что нужно выполнять знакомую нам функцию `secondsDisplay()` в основном (UI) потоке при помощи `Dispatchers.Main`. В конце для проверки работоспособности также пишем в логи хэшкоды `CoroutineScope` и `Job`. 

В методе `onResume()` делаем проверку на активность `job`, чтобы создать новые корутины, если вдруг Activity было свёрнуто без уничтожения (например, нажатие на системную кнопку "Домой").

В методе `onPause()` отменяем нашу `job` при помощи функции `cancel()`, чтобы подсчёт секунд прекратился.

Приложение реализованное при помощи Kotlin Coroutins при стресс-тесте выдаёт ожидаемые результаты, подсчёт времени при открытом приложении работает исправно.

### Создание Kotlin Coroutines при помощи lifecycleScope

Чтобы мы могли пользоваться `LifecycleScope` в нашем приложении, нужно добавить добавить новую зависимость в `build.gradle`:

```groovy
implementation "androidx.lifecycle:lifecycle-runtime-ktx:2.2.0"
```

Что же такое `LifecycleScope` и почему я решил использовать его в решении данного задания? `LifecycleScope` определяется для каждого `Lifecycle` объекта, то есть для нашей Activity в частности. И любые корутины, запущенные в этом scope, отменяются при уничтожении жизненного цикла, то есть нашей Activity. Для решения данной задачи это очень даже подходит, так как мы не должны считать количество секунд, если приложение неактивно. При использовании `CoroutineScope` мы вручную отменяли работу корутин в методе `onPause()` и сами предусматривали когда их нужно запускать, то есть в методе `onCreate()` и если понадобится, то в методе `onResume()`. При использовании `LifecycleScope` этого же можно добиться при помощи различных функций типа `whenCreated()`, `whenStarted()` и `whenResumed()`. Любой запуск корутины внутри этих блоков приостанавливается, если жизненный цикл не находится по крайней мере в минимально желаемом состоянии.

Теперь рассмотрим сам код, отличия минимальные. При описании класса мы создаём одно свойство `scope`, которое хранит в себе `lifecycleScope` и `job` создавать нам не имеет никакого смысла. В методе `onCreate()` всё также вызываем нашу приватную функцию `startCoroutine()`, в которой в качестве аргумента при запуске `scope` указываем `Dispatchers.Default`, так как по умолчанию `scope` запускается в главном (UI) потоке. Внутри лямбды указываем, что данный код нужно запускать при состоянии `RESUMED` нашего `Activity` при помощи supsend-функции `whenResumed`. Переопределять `onPause()` и `onResume()` теперь не имеет никакого смысла, потому что `lifecycleScope` всё сделает за нас.

Данное решение также успешно прошло стресс-тест и потоки работают только при активном окне приложения. Код приложения уменьшился, что не может не радовать.

# 2. Загрузка картинки в фоновом потоке (AsyncTask)

Создайте приложение, которое скачивает картинку из интернета и размещает её в `ImaveView` в `Activity`. За основу возьмите [код со StackOverflow](https://stackoverflow.com/a/9288544).

Так как нам нужно создать собственное приложение, то дадим разгуляться нашей фантазии, и начнём с демонстрации интерфейса приложения:

![](https://raw.githubusercontent.com/alexnevskiy/imagesForLabs/main/PictureDondloadingStart.png)

В приложении имеется одна единственная кнопка, при нажатии на которую происходит скачивание случайной генерирующейся картинки из интернета. При скачивании картинки в верхней части экрана появляется ProgressBar в виде крутящегося красного кружочка:

![](https://raw.githubusercontent.com/alexnevskiy/imagesForLabs/main/PictureDownloadingProgressBar.png)

После того, как изображение скачано, оно появляется в центре экрана. Картинки всегда квадратные, поэтому занимают одинаковую область, выделенную в .xml файле при помощи LinearLayout:

![](https://raw.githubusercontent.com/alexnevskiy/imagesForLabs/main/PictureDownloadingImage.png)

Вид приложения в последующих заданиях никак не будет меняться, поэтому в следующих пунктах мы его опустим. Также приложение предполагает использование только в портретном режиме, поэтому в манифесте мы устанавливаем ориентацию в портретную для каждого Activity:

```xml
android:screenOrientation="portrait"
```

Начнём разбор решения данной задачи. Создан класс `AsyncTaskActivity`, который содержит в себе приватный класс `DownloadImageTask` наследующийся от `AsyncTask` и переопределённые lifecycle методы. На вход классу подаём `imageView`, где будет показываться скачанная картинка, и `progressBar`, который будет в роли индикатора загрузки. На этот раз нам понадобится переопределить функцию `onPreExecute()`, которая позволяет работать с UI потоком до начала операции в фоновом потоке. Здесь мы отображаем наш `progressBar`, то есть задаём ему `visibility = VISIBLE`, так как изначально он скрыт. Далее в методе `doInBackground()`, основную реализацию которого мы берём из данного нам кода со StackOverflow, сохраняем URL ссылку в отдельное свойство, которую мы передаём при запуске `AsyncTask`'а методом `execute()`. Затем создаём `null`'ое свойство `pictureBitMap`, в котором будет храниться `Bitmap` нашей картинки. При помощи конструкции `try catch` загружаем картинку из интернета и сохраняем её в отдельное свойство типа `inputStream` и преобразуем его в `BitMap` при помощи метода `decodeStream()` класса `BitmapFactory`. Также делаем проверку `isCancelled`, то есть если выполнение `AsyncTask` отменили, то возвращаем null. Возвращаем полученный `BitMap`, который принимается на вход в методе `onPostExecute()`. В данном методе, который также имеет доступ к UI потоку, показываем наше скачанное изображение при помощи метода `setImageBitmap()` и выключаем видимость `progressBar` при помощи `visibility = INVISIBLE`. Также переопределим метод `onCancelled()`, который делает невидимым `progressBar` и пишет в логах, что `AsyncTask` был отменён.

В методе `onCreate()` получаем и присваиваем наши `View` в свойства при помощи знакомого нам метода `findViewById()` и при нажатии на кнопку генерируем случайное число от 100 до 2000 - это размер картинки в пикселях, то есть если выпадет, например, число 500, то картинка будет 500 на 500 пикселей, соответственно чем больше разрешение, тем дольше она будет скачиваться из интернета. Далее делаем проверку на инициализацию `AsyncTask` и его статус. Если у нас ничего не выполняется в background потоке или `AsyncTask` вообще не был запущен, то в свойство `task` создаём экземпляр нашего приватного класса `DownloadImageTask` и подаём ему на вход `imageView` и `progressBar`. В конце запускаем `AsyncTask` при помощи метода `execute()` и подаём туда нашу URL ссылку и сгенерированное число. Иначе выводим `Toast`, который нам сигнализирует о том, что на данный момент картинка скачивается и поток лучше не нагружать.

В методе `onPause()` останавливаем `AsyncTask` при помощи метода `cancel()` внутри конструкции `try catch`. Данная конструкция нужна для того, чтобы при сворачивании приложения и его повторном открытии не вылетало исключение `UninitializedPropertyAccessException`, которое означает, что мы пытаемся выключить незапущенный `AsyncTask`, а запускаем мы его только по нажатию кнопки, а не при открытии окна.

# 3. Загрузка картинки в фоновом потоке (Kotlin Coroutines)

Перепишите предыдущее приложение с использованием Kotlin Coroutines.

Для возможности работы с использованием Kotlin Coroutines прописываем зависимости в `build.gradle`. В данном решении ограничимся обычным `CoroutineScope`.

Для решения данной задачи написан класс `CoroutineActivity`, который содержит в себе реализацию приложения на Kotlin Coroutines. Как и в решении 1 задания при описании класса создаются два свойства: `scope` - хранит в себе `CoroutineScope` с диспетчером IO, который рекомендуют использовать при обращении к интернет-ресурсам и `job` - в неё мы далее определим `Job` при запуске корутины. 

В методе `onCreate()` как и в предыдущем решении получаем и присваиваем наши `View` в свойства при помощи знакомого нам метода `findViewById()` и обрабатываем нажатие на кнопку. При нажатии делаем видимым `progressBar` и делаем проверку `Job`: инициализирована ли она и запущена ли. Если `job == null` или она не запущена, то вызываем приватную функцию `startCoroutine()`, в которой и происходит создание корутин. Если же по условию не проходит, то появляется `Toast`, говорящий пользователю о том, что прошлая картинка ещё грузится.

В функции `startCoroutine()` создаём корутины аналогично решению 1 задания. Вызываем функцию `launch()` определённого в начале класса `scope` и присваиваем его результат, то есть объект класса `Job`, в свойство `job`. В лямбде генерируем случайное число для нашего URL и вызываем приватную функцию `downloadImage()`, на вход которой подаём `url` + сгенерированное число. Результат функции присваиваем свойству `imageBitMap` типа `BitMap`. Далее запускаем корутину при помощи всё того же метода `launch()`, только теперь на вход подаём `Dispatchers.Main`, то есть диспетчер для корутин, который работает в основном (UI) потоке. В его лямбде устанавливаем наше скачанное приложение при помощи метода `setImageBitmap()` и делаем `progressBar` невидимым.

В функции `downloadImage()` практически полностью написан код взятый из метода `doInBackground()` предыдущего решения, поэтому нет особого смысла заново описывать всю его реализацию.

В методе `onPause()` делаем проверку на активность `job`, и если она активна, то останавливаем её при помощи функции`cancel()` и делаем `progressBar` невидимым. Данная проверка нужна для того, чтобы отменить выполнение корутины, если приложение не будет показываться на экране, тем самым мы будем экономить системные ресурсы.

# 4. Использование сторонних библиотек

Многие "стандартные" задачи имеют "стандартные" решения. Задача  скачивания изображения в фоне возникает настолько часто, что уже  сравнительно давно решение этой задачи занимает всего лишь несколько  строчек. Убедитесь в этом на примере одной (любой) библиотеки [Glide](https://github.com/bumptech/glide#how-do-i-use-glide), [picasso](https://square.github.io/picasso/) или [fresco](https://frescolib.org/docs/index.html).

Почитав про данные нам на выбор библиотеки, для решения данной задачи я выбрал `Picasso`, на мой взгляд она очень проста в использовании.

Для того, чтобы воспользоваться ею, нужно прописать зависимости в `build.gradle`:

```groovy
implementation 'com.squareup.picasso:picasso:2.71828'
```

Если верить интернету, то `Picasso` по умолчанию использует 3 потока для доступа к диску и сети. Чтобы изменить количество потоков, можно создать собственный экземпляр `Picasso` с полным контролем потоков с помощью `Picasso.Builder`. В рамках лабораторной работы, как мне кажется, это делать необязательно.

В методе `onCreate()` мы всё также инициализируем свойства для наших `View` и обрабатываем нажатие на кнопку. При нажатии на кнопку делаем наш `progressBar` видимым, генерируем случайное число для ссылки и запускаем поочерёдно методы `Picasso`, которые и творят "магию" со скачиваемой картинкой из интернета. Вообще загрузку картинки из интернета можно сделать при помощи `Picasso` всего в одну строчку, а не создавать множество методов, потоков и прочего, но тогда это будет немного не оптимизированный процесс загрузки изображения, поэтому я использовал некоторые дополнительные методы для оптимизации. Разберём код построчно:

- `get()` - Этот метод возвращает глобальный экземпляр `Picasso`, с которым мы и работаем на протяжении всего жизненного цикла приложения.
- `load()` - данный метод запускает запрос изображения с использованием указанного мути, в нашем случае - это URL. Так как `Picasso` сначала проверяет, находится ли запрошенное изображение в кэше памяти, и если оно есть, то оно отображает изображение оттуда. Поэтому я использую следующие два метода по списку.
- `memoryPolicy()` - этот метод позволяет пропускать поиск в кэше памяти при обработке запроса изображения, а также позволяет не сохранять изображение в кэше памяти вовсе. Нам как раз это и надо, так как изображения каждый раз разные и хранить их в памяти устройства нет необходимости. Достигаем мы этих возможностей при помощи `MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE`.
- `networkPolicy()` - данный метод позволяет избежать того же кэширования, только на этот раз на самом диске. Принцип работы похожий: `NetworkPolicy.NO_CACHE`: пропускает проверку кэша диска и заставляет загружать через сеть. `NetworkPolicy.NO_STORE`: пропускает сохранение результата в кэш диска.
- `tag()` - метод для маркировки запросов Picasso, используется, чтобы в дальнейшем при неактивности приложения, если идёт загрузка изображения, поставить её на паузу по тэгу.
- `into()` - этот метод помещает загруженное изображение в передаваемый ему на вход imageView. Также этому методу на вход можно подать `Callback` и реализовать 2 его метода `onSucces()` и `onError()`, так как это интерфейс. Данный `Callback` позволит нам показывать `progressBar` во время всего процесса загрузки изображения. Для этого в методе `onSucces()` сделаем `progressBar` невидимым, так как этот метод выполняется после того, как загрузка прошла успешно, а до этого мы делали `progressBar` видимым. Чтобы метод `onError()` не был пустым, будем выводить в логи, что изображение не было загружено по какой-либо причине.

В методах `onPause()` и `onResume()` мы останавливаем и возобновляем процесс загрузки изображения при помощи методов `pauseTag()` и `resumeTag()` соответственно.

# Выводы:

В процессе выполнения данной лабораторной работы в среде разработки Android Studio получены навыки разработки многопоточных приложений, а именно: организация обработки длительных операций в background (worker) thread, то есть запуск фоновой операции при помощи coroutine/asynctask/thread и остановка фоновой операции всё также при помощи coroutine/asynctask/thread; Публикация данных из background (worker) thread в main (UI) thread. Также освоены 3 основные группы API для разработки многопоточных приложений, а именно: современный и удобный в использовании Kotlin Coroutines, устаревший на данный момент AsyncTask по причине (Цитата из последнего коммита документации) "*AsyncTask был предназначен для обеспечения правильного и простого  использования UI-потока. Тем не менее, наиболее распространённым  вариантом использования стало внедрение с UI, и это могло приводить к  утечкам контекста, пропущенным коллбэкам или крешам во время изменения  конфигурации.*" и наверное единственный способ разработки многопоточных приложений на языке Java - Java Threads. Так как моё приложение для курсовой работы написано на Java, то для записи звука с устройства мне придётся использовать Java Threads, благодаря данной лабораторной работе я понял принцип работы с ними и в скором будущем с ними опять встречусь.

Если сравнивать решения одного и того же приложения в пунктах 2-4 лабораторной работы, то мне больше по душе 4 вариант - использование сторонней библиотеки `Picasso`. Да, она в каких-то местах использует больше системных ресурсов, где этого не нужно, но с ней очень удобно работать, в том плане, что не нужно продумывать где и как запускать различные задачи в потоках, если брать взаимодействие приложения с изображениями через интернет. Программист может буквально в одну строчку получить любое изображение из интернета и использовать его в своё приложении. Также данная библиотека предоставляет возможность использовать `placeholder`'ы для индикации загрузки изображения, например, на место картинки из интернета во время загрузки поместить другую картинку с большой надписью "Происходит загрузка" или что-то в этом роде. На мой взгляд, данная библиотека хорошо подойдёт для разработчиков простых приложений, которые особо не напрягаются или не хотят напрягаться с оптимизацией своего приложения, а просто хотят сделать свой код понятнее и проще.

# Приложение:

## Листинг 1: JavaThreadActivity.kt

```kotlin
class JavaThreadActivity : AppCompatActivity() {
    var secondsElapsed: Int = 0

    @Volatile
    var backgroundThread: Thread? = null

    private fun secondsDisplay() {
        textSecondsElapsed.post {
            textSecondsElapsed.setText("Seconds elapsed: " + secondsElapsed++)
        }
    }

    private fun createThread() {
        backgroundThread = Thread {
            while (true) {
                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    break
                }
                secondsDisplay()
            }
        }
        backgroundThread?.start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createThread()
    }

    override fun onResume() {
        if (backgroundThread == null) {
            createThread()
        }
        super.onResume()
    }

    override fun onPause() {
        backgroundThread?.interrupt()
        backgroundThread = null
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

## Листинг 2: AsyncTaskActivity.kt

```kotlin
class AsyncTaskActivity : AppCompatActivity() {
    var secondsElapsed: Int = 0

    private lateinit var task: AsyncTaskCounter

    private fun secondsDisplay() {
        textSecondsElapsed.post {
            textSecondsElapsed.setText("Seconds elapsed: " + secondsElapsed++)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        task = AsyncTaskCounter(this)
        task.execute()
    }

    override fun onResume() {
        if (task.isCancelled) {
            task = AsyncTaskCounter(this)
            task.execute()
        }
        super.onResume()
    }

    override fun onPause() {
        task.cancel(true)
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

    class AsyncTaskCounter(private val activity: AsyncTaskActivity) : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void?): Void? {
            while (!isCancelled) {
                TimeUnit.SECONDS.sleep(1)
                publishProgress()
                Log.d("AsyncTask", "AsyncTask is running with hashcode: " + hashCode())
            }
            return null
        }

        override fun onProgressUpdate(vararg values: Void?) {
            super.onProgressUpdate(*values)
            activity.secondsDisplay()
        }

        override fun onCancelled() {
            super.onCancelled()
            Log.d("AsyncTask", "Canceled")
        }
    }
}
```

## Листинг 3: CoroutineActivity.kt

```kotlin
class CoroutineActivity : AppCompatActivity() {
    var secondsElapsed: Int = 0

    private var scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
    private lateinit var job: Job

    private fun secondsDisplay() {
        textSecondsElapsed.post {
            textSecondsElapsed.setText("Seconds elapsed: " + secondsElapsed++)
        }
    }

    private fun startCoroutine() {
        job = scope.launch {
            while (true) {
                delay(1000)
                launch(Dispatchers.Main) { secondsDisplay() }
                Log.d("Job", "Job running with hashcode: " + job.hashCode())
                Log.d("CoroutineScope", "CoroutineScope running with hashcode: " + scope.hashCode())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startCoroutine()
    }

    override fun onResume() {
        if (!job.isActive) {
            startCoroutine()
        }
        super.onResume()
    }

    override fun onPause() {
        job.cancel()
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

## Листинг 4: CoroutineWithLifecycleActivity.kt

```kotlin
class CoroutineWithLifecycleActivity : AppCompatActivity() {
    var secondsElapsed: Int = 0

    private var scope: LifecycleCoroutineScope = lifecycleScope

    private fun secondsDisplay() {
        textSecondsElapsed.post {
            textSecondsElapsed.setText("Seconds elapsed: " + secondsElapsed++)
        }
    }

    private fun startCoroutine() {
        scope.launch(Dispatchers.Default) {
            whenResumed {
                while (true) {
                    delay(1000)
                    launch(Dispatchers.Main) { secondsDisplay() }
                    Log.d("CoroutineScope", "CoroutineScope running with hashcode: " + scope.hashCode())
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startCoroutine()
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

## Листинг 5: activity_main.xml (PictureDownloading)

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@drawable/background"
    tools:context=".AsyncTaskActivity">

    <LinearLayout
        android:id="@+id/linearLayout"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:orientation="horizontal"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintDimensionRatio="1:1"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" >

        <ImageView
            android:id="@+id/imageView"
            android:layout_width="0dp"
            android:layout_height="match_parent"
            android:layout_weight="1"
            tools:srcCompat="@tools:sample/avatars" />
    </LinearLayout>

    <Button
        android:id="@+id/button"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@string/button"
        android:textSize="24sp"
        android:textColor="#FFFFFF"
        android:background="@drawable/button_background"
        android:shadowColor="#A8A8A8"
        android:shadowDx="0"
        android:shadowDy="0"
        android:shadowRadius="5"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/linearLayout" />

    <ProgressBar
        android:id="@+id/progressBar"
        style="@android:style/Widget.Holo.Light.ProgressBar.Large.Inverse"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:indeterminateTint="@color/red"
        android:visibility="invisible"
        app:layout_constraintBottom_toTopOf="@+id/linearLayout"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

## Листинг 6: AsyncTaskActivity.kt (PictureDownloading)

```kotlin
class AsyncTaskActivity : AppCompatActivity() {
    var url = "https://picsum.photos/"

    lateinit var imageView: ImageView
    lateinit var progressBar: ProgressBar
    lateinit var button: Button

    private var task: DownloadImageTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imageView = findViewById(R.id.imageView)
        progressBar = findViewById(R.id.progressBar)
        button = findViewById(R.id.button)
        button.setOnClickListener {
            if (task?.status == Status.PENDING || task?.status == Status.FINISHED || task == null) {
                val random = Random.nextInt(100, 2000)
                task = DownloadImageTask(imageView, progressBar)
                task?.execute(url + random.toString())
            } else {
                Toast.makeText(baseContext, "Прошлая картинка ещё грузится, пожалуйста подождите", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onPause() {
        try {
            Log.d("AsyncTask", "AsyncTask status is " + task?.status)
            task?.cancel(true)
            Log.d("AsyncTask", "AsyncTask is canceled. Status is " + task?.status)
        } catch (e: UninitializedPropertyAccessException) {
            println("Task is not initialized")
        }
        super.onPause()
    }

    private class DownloadImageTask(private val imageView: ImageView, private val progressBar: ProgressBar) : AsyncTask<String?, Void?, Bitmap?>() {

        override fun onPreExecute() {
            super.onPreExecute()
            progressBar.visibility = VISIBLE
        }

        override fun doInBackground(vararg params: String?): Bitmap? {
            val urlDisplay = params[0]
            var pictureBitMap: Bitmap? = null
            try {
                val input = URL(urlDisplay).openStream()
                if (isCancelled) return null
                pictureBitMap = BitmapFactory.decodeStream(input)
            } catch (e: Exception) {
                Log.e("Error", e.message!!)
                e.printStackTrace()
            }
            return pictureBitMap
        }

        override fun onPostExecute(result: Bitmap?) {
            imageView.setImageBitmap(result)
            progressBar.visibility = INVISIBLE
        }

        override fun onCancelled() {
            super.onCancelled()
            progressBar.visibility = INVISIBLE
            Log.d("AsyncTask", "AsyncTask was canceled.")
        }
    }
}
```

## Листинг 7: CoroutineActivity.kt (PictureDownloading)

```kotlin
class CoroutineActivity : AppCompatActivity() {
    var url = "https://picsum.photos/"

    lateinit var imageView: ImageView
    lateinit var progressBar: ProgressBar
    lateinit var button: Button

    private var scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imageView = findViewById(R.id.imageView)
        progressBar = findViewById(R.id.progressBar)
        button = findViewById(R.id.button)
        button.setOnClickListener {
            progressBar.visibility = VISIBLE
            if (job == null || !job?.isActive!!) {
                startCoroutine()
            } else {
                Toast.makeText(baseContext, "Прошлая картинка ещё грузится, пожалуйста подождите", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onPause() {
        if (job?.isActive!!) {
            job?.cancel()
            progressBar.visibility = INVISIBLE
        }
        super.onPause()
    }
    
    private fun downloadImage(url: String): Bitmap? {
        var pictureBitMap: Bitmap? = null
        try {
            val input = URL(url).openStream()
            pictureBitMap = BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            Log.e("Error", e.message!!)
            e.printStackTrace()
        }
        return pictureBitMap
    }

    private fun startCoroutine() {
        job = scope.launch {
            val random = Random.nextInt(100, 2000)
            val imageBitMap = downloadImage(url + random.toString())
            launch(Dispatchers.Main) {
                imageView.setImageBitmap(imageBitMap)
                progressBar.visibility = INVISIBLE }
        }
    }
}
```

## Листинг 8: PicassoActivity.kt (PictureDownloading)

```kotlin
class PicassoActivity : AppCompatActivity() {
    var url = "https://picsum.photos/"

    lateinit var imageView: ImageView
    lateinit var progressBar: ProgressBar
    lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imageView = findViewById(R.id.imageView)
        progressBar = findViewById(R.id.progressBar)
        button = findViewById(R.id.button)
        button.setOnClickListener {
            progressBar.visibility = VISIBLE
            val random = Random.nextInt(100, 2000)
            Picasso
                .get()
                .load(url + random)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                .tag("Image")
                .into(imageView, object: Callback {
                    override fun onSuccess() {
                        progressBar.visibility = INVISIBLE
                    }

                    override fun onError(e: java.lang.Exception?) {
                        Log.d("Picasso", "Image cannot download...")
                    }
                })
        }
    }

    override fun onPause() {
        Picasso.get().pauseTag("Image")
        super.onPause()
    }

    override fun onResume() {
        Picasso.get().resumeTag("Image")
        super.onResume()
    }
}
```