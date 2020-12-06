# Цели работы

Получить практические навыки разработки сервисов (started и bound) и Broadcast Receivers.

# 1. Started сервис для скачивания изображения

В лабораторной работе №6 был разработан код, скачивающий картинку из интернета. На основе этого кода разработайте started service, скачивающий файл из интернета. URL изображения для скачивания должен передаваться в Intent. Убедитесь (и опишите доказательство в отчете), что код для скачивания исполняется не в UI потоке.

Добавьте в разработанный сервис функцию отправки broadcast сообщения по завершении скачивания. Сообщение (Intent) должен содержать путь к  скачанному файлу.

Для решения данной задачи можно создать strarted service несколькими способами: создать класс, который наследуется от `Service`, `IntentService` или `JobIntentService`. Если бы мы наследовались от обычного `Service`, то нам надо было бы позаботиться о правильном создании потоков для каждой задачи и останавливать их в нужное время. Так как с созданием потоков мы уже достаточно напрактиковались в прошлой лабораторной работе, то хотелось бы использовать один из двух оставшихся способов. И мой выбор пал на `deprecated` в данный момент `IntentService`. Сделан такой выбор по простой причине: по `IntentService` очень много различной информации в интеренете, так как `JobIntentService` появился не так давно и не является таким распространённым, как первый. Что больше всего меня удивило, так это то, что вроде бы казалось новенький `JobIntentService`, а создаёт свои background потоки при помощи `AsyncTask`'а, который также является `deprecated`. Либо это в будущем исправят и заменят `AsyncTask` на `Executor`'ы, либо также сделают `deprecated`, будет интересно понаблюдать за этим.

## IntentService

Приложение с сервисами для всех пунктов будет одинаковым, поэтому здесь я презентую его визуальную составляющую. Основное активити практически такое же, как и в 6 лабораторной работе, только вместо `ImageView` по центру экрана теперь находится `TextView`. В 3 пункте лабораторной работы немного будет изменён интерфейс приложения, да и вообще, конкретно в этом пункте `TextView` нам не нужен, но я его оставлю как заготовку для следующего решения. Для этого задания у нас пока ещё нет Broadcast Receiver'а, чтобы отображать на экране путь до скачанной картинки, поэтому `TextView` нам об этом сигнализирует:

![](https://raw.githubusercontent.com/alexnevskiy/imagesForLabs/main/PictureDowdloadingWithTextView.png)

В классе `MainActivity` в методе `onCreate()` находим и присваиваем наши `View` в свойства для дальнейшего использования и обрабатываем нажатие на кнопку. При нажатии генерируется случайное число для размера изображения как в прошлой лабораторной работе, создаём `Intent`, в который при помощи метода `putExtra()` помещаем URL картинки и запускаем наш сервис при помощи метода `startService()`, на вход подаём ему созданный только что `Intent`.

В классе `PictureDownloadingService`, который наследуется от `deprecated` `IntentService()` реализован весь наш сервис для загрузки изображений из интернета. На вход `IntentService()` подаётся его название, это используется для имени рабочего потока, что важно только для отладки. В главной функции `onHandleIntent()`, которую мы обязаны реализовать при наследовании, происходит скачивание изображения, упаковка его в файл и отправка `broadcast` собщения по завершении скачивания для последующего получения клиенту в следующих пунктах лабораторной работы. Разберём этот код поподробнее. В начале получаем наш URL при помощи метода `getStringExtra()` из `Intent`. Далее присваиваем в свойство результат выполнения знакомой нам из прошлой лабораторной работы функции `downloadImage()`, в которой происходит скачивание изображения из интернета и преобразование его в тип `BitMap`. Разбирать данную функцию построчно я не буду, так как это делалось в прошлой лабораторной работе. Затем мы задаём уникальное имя файлу, в котором будет храниться скачанное изображение. Имя файла должно быть уникальным, чтобы при подаче нескольких `Intent` от разных пользователей, изображения не сохранялись в один и тот же файл, что привело бы к рассылке сообщений всем пользователям с одинаковой картинкой, поэтому при создании имени я использую хэшкод нашего `BitMap`, ведь хэшкод у каждого объекта уникальный. При помощи метода `openFileOutput()` для нашего `Context`, то есть для нашего же сервиса, создаём `FileOutputStream`, при помощи которого будет создан файл с нашим изображением. На вход методу подаём название изображения, по которому он ищет у себя в локальной директории файл с таким названием и при отсутствии создаёт его, и режим, обычно указывается `MODE_PRIVATE`, что означает доступ только для нашего приложения. Методом `compress()` происходит сжатие `BitMap` в PNG формат, и успешно перемещается в `FileOutputStream`. После этого `FileOutputStream` следует закрыть при помощи метода `close()`. После записи в файл получаем путь до него при помощи класса `File()`, на вход которому подаём путь до локальной директории сервиса и название сохранённого изображения. В конце создаём `Intent` с уникальным `action`, чтобы явно указать какие сообщения надо будет получать клиентам и добавляем в него путь до картинки при помощи всё того же метода `putExtra()`. Отправляем `broadcast` сообщение в самом конце метода при помощи функции `sendBroadcast()`, как нас и просили в указаниях к задаче. При завершении скачивания изображения мы не должны останавливать сервис при помощи метода `stopSelf()`, так как `IntentService` останавливается сам, когда обработает все запросы.

Для того, чтобы наш сервис мог работать, мы должны объявить его в файле манифеста нашего приложения. Главный атрибут `android:name`, который мы указываем является единственным обязательным атрибутом - он представляет имя класса сервиса. Указаны также необязательные два атрибута: `android:enabled` - позволяет создавать сервис самой системе, по умолчанию он и так включён, но я на всякий случай продублировал; `android:exported` - позволяет компонентам других приложений вызывать службу или взаимодействовать с ней, ставим тут true, так как предполагается, что несколько приложений могут одновременно использовать наш сервис.

```xml
<service android:name=".PictureDownloadingService"
            android:enabled="true"
            android:exported="true"/>
```

Чтобы убедиться в том, что код для скачивания действительно исполняется не в UI потоке, можно использовать 2 способа проверки: посылать логи с названием рабочего потока изнутри сервиса и посмотреть создание потоков в Profiler внутри AndroidStudio. Я проверил обоими способами, и, действительно, поток для сервиса использовался другой и назывался `IntentService[PictureDownloading]` - название такое же, что мы и указывали при реализации класса в самом начале. Вот окно Profiler при нескольких нажатях на кнопку загрузки изображения:

![](https://raw.githubusercontent.com/alexnevskiy/imagesForLabs/main/PictureDownloadingProfiler.png)

Внизу рисунка можно видеть, что при нажатии на кнопку каждый раз создавался поток под именем `IntentService[PictureDownloading]`, в котором и происходило скачивание картинки. Работа потока зависит от размера картинки, поэтому она занимала каждый раз разное количество времени.

# 2. Broadcast Receiver

Разработайте два приложения: первое приложение содержит 1 activity с 1 кнопкой, при нажатии на которую запускается сервис по скачиванию файла. Второе приложение содержит 1 broadcast receiver и 1 activity. Broadcast receiver по получении сообщения из сервиса инициирует отображение *пути* к изображению в `TextView` в Activity.

В качестве первого приложения для данной задачи будем использовать решение первого пункта лабораторной работы, так как там уже реализован сервис по скачиванию файла.

Для второго приложения с braodcast receiver'ом возьмём за основу код из прошлого пункта для `activity_main.xml`, и поменяем там лишь название кнопки и уберём `progressBar` за ненадобностью. Теперь она будет не отправлять запрос сервису, а очищать текстовое поле, где должен находиться путь к картинке при срабатывании braodcast receiver. Поэтому Activity внешне выглядит практически также:

![](https://raw.githubusercontent.com/alexnevskiy/imagesForLabs/main/PictureBroadcastReceiver.png)

Для реализации broadcast receiver написан класс `PictureBroadcastReceiver`, который наследуется от `BroadcastReceiver()`, внутри класса `MainActivity`. Сначала опишу главный класс нашей Activity.

В начале создаются свойства для наших `View` и не только, чтобы в дальнейшем их реализовать. В методе `onCreate()` определяем наши `View` в соответствующие свойства и делаем проверку на наличие пути в `Extra` нашего `Intent`. Данную проверку я опишу позже, когда дойдём до описания класса `PictureBroadcastReceiver`, где и кладётся путь до файла в `Intent`. Если проверка прошла, то в `TextView` отображаем путь до файла, который мы получаем при помощи broadcast receiver'а далее. Затем создаём экземпляр нашего класса `PictureBroadcastReceiver` и регистрируем его при помощи функции `registerReceiver()`, где на вход подаём сам приёмщик и `IntentFilter`, который выбирает broadcast'ы для получения. Ну и в конце чисто для функционала обрабатываем нажатие кнопки, при котором текст по центру экаран меняется на "Путь сброшен". В методе `onDestroy()` отменяем регистрацию нашего приёмщика.

Рассмотрим созданный класс `PictureBroadcastReceiver` с его единственным методом `onReceive()`. В данном методе я стартую новую Activity с вложенным внутрь `Intent` путём до файла. Сделано это для того, чтобы не переключаться между приложениями после активации сервиса, приложение с приёмщиком откроется само при получении широковещательного сообщения. Для этого мы создаём новый `Intent`, в котором указываем основной класс приложения - `MainActivity`. В этот `Intent` добавляются флаги, чтобы приложение открывалось само, когда оно не является активным. Для этого я использую комбинацию флагов: `Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK`. `FLAG_ACTIVITY_CLEAR_TASK` означает, что любая существующая задача, которая будет связана с Activity, будет очищена перед её запуском, то есть действие становится новым корнем пустой задачи, а все старые активити завершаются. `FLAG_ACTIVITY_NEW_TASK` используется, если задача уже запущена для активити, которое сейчас запускается, то новое Activity не будет запущено, вместо этого текущая задача будет просто перенесена на передний план экрана в том состоянии, в котором она была. Затем достаётся путь до файла с изображением при помощи знакомого метода `getStringExtra()` и кладётся в созданный до этого `Intent`, чтобы после запуска новой Activity отобразить его на экране. Именно для этого и делалась проверка на наличие пути в `Intent` в методе `onCreate()` класса `MainActivity`. В конце фукнции вызываем метод `startActivity()`, который запустит новую активити уже с отображённым путём до файла на экране.

Для того, чтобы наш broadcast receiver работал, нужно его объявить в файле манифеста. Также для того, чтобы приёмщик получал конкретные широковещательные сообщения, мы должны добавить `action`, по которому рассылается путь до файла сервисом:

```xml
<receiver android:name=".MainActivity$PictureBroadcastReceiver">
    <intent-filter>
        <action android:name="com.example.pictureurldownloading.PICTURE_DOWNLOAD"/>
    </intent-filter>
</receiver>
```

Вот что появляется на экране приложения, если запустить сервис из другого приложения:

![](https://raw.githubusercontent.com/alexnevskiy/imagesForLabs/main/PictureBroadcastReceiverWithPath.png)

Можно убедиться в том, что оба приложения реально работают и файлы сохраняются в директорию первого приложения и доставляются при помощи broadcast сообщений в наше второе приложение, где путь и отображается на экране. Для этого нужно зажать иконку приложения в меню телефона и нажать на кнопку "О приложении":

![](https://raw.githubusercontent.com/alexnevskiy/imagesForLabs/main/PictureURLDownloadingMemory.png)

Можно видеть, что при тестировании приложения было скачано множество картинок, и сохранялось всё под разными именами, что привело к такому большому объёму данных пользователя приложения. Чтобы очистить хранилище нужно нажать на "Хранилище и кеш", а затем нажать на кнопку "Очистить хранилище".

# 3. Bound Service для скачивания изображения

Сделайте разработанный сервис одновременно bound И started: переопределите метод `onBind`. Из тела метода возвращайте `IBinder`, полученный из класса [`Messenger`](https://developer.android.com/guide/components/bound-services?hl=ru#Messenger). Убедитесь (доказательство опишите в отчете), что код скачивания файла исполняется не в UI потоке.

Измените способ запуска сервиса в первом приложении: вместо `startService` используйте `bindService`. При нажатии на кнопку отправляйте сообщение [`Message`](https://developer.android.com/reference/android/os/Message.html?hl=ru), используя класс `Messenger`, полученный из интерфейса `IBinder` в методе [`onServiceConnected`](https://developer.android.com/reference/android/content/ServiceConnection.html?hl=ru#onServiceConnected(android.content.ComponentName, android.os.IBinder)).

Добавьте в первое приложение `TextView`, а в сервис отправку [обратного](https://developer.android.com/reference/android/os/Message.html?hl=ru#replyTo) сообщения с местоположением скачанного файла. При получении сообщения от сервиса приложение должно отобразить путь к файлу на экране.

Обратите внимание, что разработанный сервис должен быть одновременно bound И started. Если получен интент через механизм started service, то сервис скачивает файл и отправляет broadcast (started service не знает своих клиентов и не предназначен для двухсторонней коммуникации). Если получен message через механизм bound service, то скачивается файл и  результат отправляется тому клиенту, который запросил этот файл (т.к. bound service знает всех своих клиентов и может им отвечать).

Для решения данной задачи изменён интерфейс приложения, а именно изменено название старой кнопки на "Вызвать Started Service" и добавлена новая кнопка "Вызвать Bound Service", которая высылает сервису сообщение с URL картинки, а затем мы получаем путь до файла с картинкой и отображаем его на экране. Теперь окно приложения выглядит следующим образом:

![](https://raw.githubusercontent.com/alexnevskiy/imagesForLabs/main/PictureURLDownloading2Buttons.png)

Для того, чтобы разработанный сервис мог быть одновременно и bound и started, нужно было добавить и переопределить несколько методов и немного подправить уже реализованные в 1 пункте. Начнём с главного класса - `MainActivity`.

Здесь в начале добавлены поля, которые дальше будут определены и добавлены константы, которые нужны для отправки и получений сообщений между клиентом и сервисом. Также создаётся сопутствующий объект, в котором находится флаг `isWaiting`, он нужен для того, чтобы клиент не отправлял много запросов на сервер, пока ждёт результат предыдущего. В методе `onCreate()` также определяем переменные и определяем `Messenger` со стороны клиента, которому передаём на вход `ClientHandler` (рассмотрим его далее), для возможности обратной связи между клиентом, который делает запрос, и сервисом. Далее обрабатываем нажатие на кнопку для started service, здесь ничего не изменилось. Затем обрабатываем нажатие на вторую кнопку для того, чтобы отправить сообщение сервису о том, что мы хотим получить путь до файла с картинкой, которую этот же сервис и скачает. Делаем проверку на `isWaiting`, если же проверка прошла, то для этого мы генерируем случайное число, которое будет отправлено вместе с первой частью ссылки на изображение, и создаём сообщение, то есть объект класса `Message`, при помощи функции `obtain`, где на вход подаём нашу константу `MSG_TO_MESSENGER`, которая сигнализирует сервису о скачивании картинки из интернета, и наш URL, который подаётся в виде `Object`. Также мы подаём в свойство `replyTo` наш мессенджер, чтобы сервис мог отправить нам ответ на наш запрос. В конце при помощи функции `send()` отправляем наше сообщение мессенджеру сервиса, объект которого мы достали через реализованный интефейс `ServiceConnection`. Если же проверка не прошла, то появляется Toast, который сигнализирует о том, что нужно подождать окончания загрузки прошлой картинки. Теперь мы рассмотрим `ServiceConnection`. 

Для того, чтобы наладить связь с сервисом и получить экземпляр мессенджера через возвращаемый интерфейс `IBinder` в методе `onBind` сервиса, мы обязаны реализовать интерфейс `ServiceConnection`, так как мы используем `Messenger` для связи, а не реализуем интерфейс `IBinder` сами. Для этого в функции `onServiceConnected()` присваиваем в заранее отведённое для этого свойство объект класса `Messenger()` через конструктор, который принимает на вход этот самый `IBinder`, и ставим флаг `isConnected` в `true` для того, чтобы в дальнешем успешно отвязаться от сервиса. В методе `onServiceDisconnected()` делаем обратное - присваиваем свойству `pictureMessenger` `null` и ставим флаг в `false`, а также ставим `progressBar` невидимым, если вдруг на стороне сервиса произошла ошибка, а мы в это время ждали от него сообщения с путём до файла картинки.

Теперь рассмотрим реализованный класс `ClientHandler`, который находится внутри нашего `MainActivity`. Его реализация необходима для обработки сообщений, которые наш `myMessenger` получает со стороны сервиса. В нашем случае он меняет `View`, отображаемые на экране. Для полноценной работы класса нужно переопределить единственный обязательный метод `handleMessage()`, который предоставляет доступ к полученному сообщению. Для этого при помощи конструкции `when` мы смотрим что за код сообщения мы получили, чтобы мы могли определить о чём оно. Мы ожидаем от сервиса только единственный код, который у нас определён константой - `MSG_TO_CLIENT`. Если же полученный код присутствует, то отображаем путь до файла в `TextView`, который хранится в поле `obj` сообщения, и делаем `progressBar` невидимым, что является сигналом о получении сообщения.

В методе `onStart()` создаём `Intent` с вложенным туда `PictureDownloadingService` и отправляем его при помощи функции `bindService()`, где на вход подаём этот `Intent`, реализованный интерфейс в виде объекта `serviceConnection` и часто используемый флаг `BIND_AUTO_CREATE`, который означает автоматическое создание сервиса, пока существует привязка к нему. Стоит обратить внимание на то, что хоть при этом и будет создан сервис, но метод `onStartCommand()` (в нашем случае для `IntentService` его реализация не является обязательной, так как переопределяем `onHandleIntent()`) по-прежнему будет вызываться только при явном вызове метода `startService()`, который мы используем при созданнии started service при нажатии на первую кнопку. Однако даже без этого сервис предоставляет нам доступ к объекту сервиса во время его создания.

В методе `onStop()` делаем проверку на подключение к сервису, если же подключение существует, то отписываемся от сервиса с помощью функции `unbindService()`, где на вход подаём реализованный интерфейс в виде объекта `serviceConnection`. Затем ставим наш флаг `isConnected` в `false` и делаем `progressBar` невидимым, если вдруг в момент отвязывания от сервиса в нём ещё происходила отправка сообщения.

Теперь рассмотрим изменения в классе сервиса `PictureDownloadingService`. В начале добавлены константы для сообщений и свойства, которые далее будут определены, а также присваивается `CoroutineScope` с `Dispatchers.IO`, так как обработка и выполнения кода сообщений не предусмотрена в отдельном потоке, а исполняется в основном UI потоке приложения, далее рассмотрим эту ситуацию подробнее. Естественно для работы корутин нужно вписать зависимость в `build.gradle`. Функции `onCreate()`, `onHandleIntent()`, `onDestroy()` и `downloadImage()` остались без изменений, единственное - была произведена структуризация кода, а именно вынесена часть кода по занесению полученного изображения в файл в функцию `createFile()`, для удобства использования в другой функции без повтороного копипаста.

В методе `onBind()` мы обязаны вернуть тот самый `IBinder`, который рекомендуется не реализовывать самому, поэтому мы создаём объект класса `Messenger` и подаём ему на вход реализованный класс `MessageHandler`, рассмотрим его реализацию далее. Возвращаем в методе требуемый `IBinder` через геттер только что определённого свойства `pictureMessenger`.

Рассмотрим реализованный класс `MessageHandler`, который находится внутри класса нашего сервиса. На вход мы ему подаём наш сервис, чтобы получить доступ к его методам по созданию корутин, загрузки изображения и сохранения его в файл. Переопределяем обязательную функцию `handleMessage()`, где также при помощи конструкции `when` смотрим что за код сообщения мы получили, чтобы определить о чём оно. Так как мы обрабатываем сообщение на стороне сервиса, то, соответственно,  ожидаем код `MSG_TO_MESSENGER` и при его соответствии выводим в логи поток, в котором запускается данный код, чтобы удостовериться, что всё работает в главном UI потоке, и далее выполняем функцию `startCoroutine()`, где на вход подаётся мессенджер, которому мы должны отправить ответ, то есть это мессенджер клиента, и URL, который находился в поле `obj` сообщения.

В новой функции под названием `startCoroutine()` происходит создание корутин, где будет выполняться скачивание из интернета и создание файла с картинкой. Создание корутины происходит точно также, как и в прошлой лабораторной работе. Запускаем корутину при помощи функции `launch`, и её результат, то есть `Job`, помещаем в одноимённое свойство. Внутри функции `launch` посылаем лог о том, что мы работаем в корутине, а не в UI потоке. Далее при помощи знакомой функции `downloadImage()` получаем `BitMap` скачанного изображения. Так как корутину остановить принудительно достаточно трудно и не рекомендуется вообще этого делать, то следовало бы ждать завершения всего кода в корутине и показывать в Activity путь до файла, но по проведённым тестам работы сервиса при его выключении отправка широковещательного сообщения не происходит, то есть принудительно завершается поток, и поэтому сделав запрос started service и при этом выключив сервис, например, отписавшись всем клиентам от него, рассылка не произойдёт. Хочется добиться такого же функционала и со стороны bound service, поэтому после самой длительной операции в корутине делаем проверку на её принудительную остановку. Если же проверка прошла, то выключаем корутину вызвав `return` с аннотацией `@launch`. Далее происходит сохранение картинки в файл при помощи функции `createFile()` и создание нового сообщения при помощи того же метода `obtain()`, подав ему на вход код `MSG_TO_CLIENT` и путь до файла в поле `obj`. В конце отправляем мессенджеру клиента созданное сообщение при помощи метода `send()`.

В функции `onUnbind()` происходит отвязка клиента от сервиса, соответственно здесь же и должна принудительно завершаться работы корутины, поэтому делаем проверку на её работу, и если она активна, то останавливаем её при помощи функции `cancel()` и пишем в логи, что корутина отменена. После проверки для контроля выводим в логах, что сервис отвязан клиентом.

После нажатия на кнопку "Вызвать Bound Service" ждём несколько секунд и получаем путь до файла по центру экрана в `TextView`:

![](https://raw.githubusercontent.com/alexnevskiy/imagesForLabs/main/PictureURLDownloadingWithPath.png)

Разработанный в итоге сервис является и started, и bound, то есть всё работает как и написано в указаниях к решению задачи. При нажатии на верхнюю кнопку посылается запрос на сервис, где отсылаются широковещательные сообщения всем, кто настроен на их получение, то есть приложение из 2 задания, а первое приложение при этом ничего не получает. При нажатии на 2 кнопку посылается bound запрос сервису через сообщение при помощи класса `Messenger`, сервис его обрабатывает, делает все те же манипуляции с картинкой и отправляет его обратно клиенту, после чего у него на экране появляется путь до файла.

# Выводы:

В процессе выполнения данной лабораторной работы в среде разработки Android Studio получены навыки разработки сервисов (started и bound) и Broadcast Receivers, а именно: создание своего сервиса для скачивания изображения из интернета, а также создание файла с ним в локальной директории приложения и возможность отправки пути широковещательно, если пришёл started запрос, и отправка сообщения конкретному клиенту при помощи `Messenger`, если пришёл bound запрос со стороны клиента. Также разработано приложение, в котором находится один broadcast receiver, то есть широковещательный приёмщик, который принимает конкретные `Intent`'ы с заданным `action` равным `"com.example.pictureurldownloading.PICTURE_DOWNLOAD"`. Данный ресивер создаёт новое Activity с отображаемым в нём путём до файла, когда получает широковещательное сообщение, при этом не сохраняя в backstack прошлые Activity.

Также в конце хотелось бы обратить отдельно внимание на краткое описание того, как нужно использовать `Messenger` в сервисе, так как, по-моему, это самый важный и трудный момент в реализации bound service. Вот и само краткое описание по пунктам:

1. Сервис должен реализовать обработчик, то есть `Handler`, который получает обратный вызов для каждого сообщения от клиента.
2. Сервис использует обработчик, то есть `Handler`, для создания объекта `Messenger`, который в свою очередь является ссылкой на обработчик.
3. `Messenger` создаёт `IBinder`, который сервис возвращает клиентам из метода `onBind()`. Это наверное самая главная причина, почему мы используем `Messenger` в нашем сервисе, так как интерфейс `IBinder` рекомендуется самому не реализовать.
4. Клиенты используют `IBinder` для того, чтобы с их помощью создать экземпляр `Messenger` (который в свою очередь ссылается на `Handler` сервиса), который клиент использует для отправки сообщений, то есть объектов `Message`, сервису.
5. Сервис получает каждый `Message` в своём обработчике (`Handler`), а именно в методе `handleMessage()`, где и происходит его обработка и дальнейшие действия в сервисе при обработке поля `what` сообщения.

# Приложение:

## Листинг 1: activity_main.xml (PictureURLDownloading) для 1 задачи

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@drawable/background"
    tools:context=".MainActivity">

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

        <TextView
            android:id="@+id/textView"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:layout_gravity="center"
            android:gravity="center"
            android:text="@string/url_path"
            android:textSize="28sp"
            android:textColor="#FFFFFF"
            android:shadowColor="#A8A8A8"
            android:shadowDx="0"
            android:shadowDy="0"
            android:shadowRadius="5"
            android:background="@drawable/text_background"/>
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

## Листинг 2: MainActivity.kt для 1 задачи

```kotlin
class MainActivity : AppCompatActivity() {
    var url = "https://picsum.photos/"

    lateinit var textView: TextView
    lateinit var progressBar: ProgressBar
    lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView = findViewById(R.id.textView)
        progressBar = findViewById(R.id.progressBar)
        button = findViewById(R.id.button)
        button.setOnClickListener {
            val random = Random.nextInt(100, 2000)
            val intent = Intent(this, PictureDownloadingService::class.java)
                .putExtra("url", url + random.toString())
            startService(intent)
        }
    }
}
```

## Листинг 3: PictureDownloadingService.kt для 1 задачи

```kotlin
class PictureDownloadingService : IntentService("PictureDownloading") {
    private val intentAction = "com.example.pictureurldownloading.PICTURE_DOWNLOAD"

    override fun onCreate() {
        Log.i("Service", "Service is started")
        super.onCreate()
    }

    override fun onHandleIntent(intent: Intent?) {
        Log.i("Thread", Thread.currentThread().name)
        val url = intent?.getStringExtra("url").toString()
        val imageBitMap = downloadImage(url)
        val filename = "image" + imageBitMap.hashCode()

        val fileStream = this.openFileOutput(filename, Context.MODE_PRIVATE)
        imageBitMap?.compress(Bitmap.CompressFormat.PNG, 100, fileStream)
        fileStream.close()

        val imagePath = File(this.filesDir, filename).absolutePath

        Intent().also { intent ->
            intent.action = intentAction
            intent.putExtra("url", imagePath)
            sendBroadcast(intent)
        }
    }

    override fun onDestroy() {
        Log.i("Service", "Service is destroyed")
        super.onDestroy()
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
}
```

## Листинг 4: activity_main.xml (PictureBroadcastReceiver)

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@drawable/background"
    tools:context=".MainActivity">

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

        <TextView
            android:id="@+id/textView"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:layout_gravity="center"
            android:gravity="center"
            android:text="@string/url_path"
            android:textSize="28sp"
            android:textColor="#FFFFFF"
            android:shadowColor="#A8A8A8"
            android:shadowDx="0"
            android:shadowDy="0"
            android:shadowRadius="5"
            android:background="@drawable/text_background"/>
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

</androidx.constraintlayout.widget.ConstraintLayout>
```

## Листинг 5: MainActivity (PictureBroadcastReceiver)

```kotlin
class MainActivity : AppCompatActivity() {
    val intentAction = "com.example.pictureurldownloading.PICTURE_DOWNLOAD"
    lateinit var button: Button
    lateinit var textView: TextView
    private lateinit var broadcastReceiver: PictureBroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView = findViewById(R.id.textView)
        button = findViewById(R.id.button)
        if (intent.getStringExtra("path") != null) {
            Log.i("Activity", "Text was switched")
            textView.text = intent?.getStringExtra("path").toString()
        }

        broadcastReceiver = PictureBroadcastReceiver()
        registerReceiver(broadcastReceiver, IntentFilter(intentAction))

        button.setOnClickListener {
            textView.text = "Путь сброшен"
        }
    }

    class PictureBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i("Broadcast Receiver", "Message was receive")
            val intentMainActivity = Intent(context, MainActivity::class.java)
            intentMainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                    or Intent.FLAG_ACTIVITY_NEW_TASK)
            val path = intent?.getStringExtra("url").toString()
            intentMainActivity.putExtra("path", path)
            context?.startActivity(intentMainActivity)
        }
    }

    override fun onDestroy() {
        unregisterReceiver(broadcastReceiver)
        super.onDestroy()
    }
}
```

## Листинг 6: activity_main.xml (PictureURLDownloading) для 2 задачи

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@drawable/background"
    tools:context=".MainActivity">

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

        <TextView
            android:id="@+id/textView"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:layout_gravity="center"
            android:gravity="center"
            android:text="@string/url_path"
            android:textSize="28sp"
            android:textColor="#FFFFFF"
            android:shadowColor="#A8A8A8"
            android:shadowDx="0"
            android:shadowDy="0"
            android:shadowRadius="5"
            android:background="@drawable/text_background"/>
    </LinearLayout>

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

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:orientation="vertical"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/linearLayout">

        <Button
            android:id="@+id/button_started_service"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:background="@drawable/button_background"
            android:shadowColor="#A8A8A8"
            android:shadowDx="0"
            android:shadowDy="0"
            android:shadowRadius="5"
            android:text="@string/started_service"
            android:textColor="#FFFFFF"
            android:textSize="24sp"
            android:layout_gravity="center"
            android:layout_weight="1"
            android:layout_marginBottom="5dp"
            android:layout_marginTop="10dp"/>

        <Button
            android:id="@+id/button_bound_service"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/bound_service"
            android:background="@drawable/button_background"
            android:shadowColor="#A8A8A8"
            android:shadowDx="0"
            android:shadowDy="0"
            android:shadowRadius="5"
            android:textColor="#FFFFFF"
            android:textSize="24sp"
            android:layout_gravity="center"
            android:layout_weight="1"
            android:layout_marginBottom="10dp"
            android:layout_marginTop="5dp"/>
    </LinearLayout>

</androidx.constraintlayout.widget.ConstraintLayout>
```

## Листинг 7: MainActivity.kt для 2 задачи

```kotlin
private const val MSG_TO_MESSENGER = 1
private const val MSG_TO_CLIENT = 2

class MainActivity : AppCompatActivity() {
    companion object {
        private var isWaiting = false
    }

    var url = "https://picsum.photos/"
    private var isConnected = false
    private var pictureMessenger: Messenger? = null

    lateinit var textView: TextView
    lateinit var progressBar: ProgressBar
    lateinit var buttonStartedService: Button
    lateinit var buttonBoundService: Button
    lateinit var myMessenger: Messenger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView = findViewById(R.id.textView)
        progressBar = findViewById(R.id.progressBar)
        buttonStartedService = findViewById(R.id.button_started_service)
        buttonBoundService = findViewById(R.id.button_bound_service)

        myMessenger = Messenger(ClientHandler(textView, progressBar))

        buttonStartedService.setOnClickListener {
            val random = Random.nextInt(100, 2000)
            val intent = Intent(this, PictureDownloadingService::class.java)
                .putExtra("url", url + random.toString())
            startService(intent)
        }

        buttonBoundService.setOnClickListener {
            if (!isWaiting) {
                progressBar.visibility = VISIBLE
                val random = Random.nextInt(100, 2000)
                val message = Message.obtain(null, MSG_TO_MESSENGER,
                    url + random.toString()).apply {
                    replyTo = myMessenger
                }
                pictureMessenger?.send(message)
                isWaiting = true
            } else {
                Toast.makeText(baseContext, "Прошлая картинка ещё грузится, пожалуйста подождите", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        val intent = Intent(this, PictureDownloadingService::class.java)
        bindService(intent, serviceConnection, BIND_AUTO_CREATE)
        super.onStart()
    }

    override fun onStop() {
        if (isConnected) {
            unbindService(serviceConnection)
            isConnected = false
            progressBar.visibility = INVISIBLE
        }
        super.onStop()
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            pictureMessenger = Messenger(service)
            isConnected = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            pictureMessenger = null
            isConnected = false
            progressBar.visibility = INVISIBLE
        }
    }

    internal class ClientHandler(private val textView: TextView,
                                 private val progressBar: ProgressBar) : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_TO_CLIENT -> {
                    textView.text = msg.obj.toString()
                    progressBar.visibility = INVISIBLE
                    isWaiting = false
                }
            }
        }
    }
}
```

## Листинг 8: PictureDownloadingService.kt для 2 задачи

```kotlin
private const val MSG_TO_MESSENGER = 1
private const val MSG_TO_CLIENT = 2

class PictureDownloadingService : IntentService("PictureDownloading") {
    private val intentAction = "com.example.pictureurldownloading.PICTURE_DOWNLOAD"

    private lateinit var pictureMessenger: Messenger
    private var scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    private var job: Job? = null

    internal class MessageHandler(private val service: PictureDownloadingService) : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_TO_MESSENGER -> {
                    Log.i("Thread Message", Thread.currentThread().name)
                    service.startCoroutine(msg.replyTo, msg.obj as String)
                }
            }
        }
    }

    override fun onCreate() {
        Log.i("Service", "Service is started")
        super.onCreate()
    }

    override fun onHandleIntent(intent: Intent?) {
        Log.i("Thread Service", Thread.currentThread().name)
        val url = intent?.getStringExtra("url").toString()
        val imageBitMap = downloadImage(url)
        val imagePath = createFile(imageBitMap)
        Log.i("Service", "Started service create file")

        Intent().also { intent ->
            intent.action = intentAction
            intent.putExtra("url", imagePath)
            sendBroadcast(intent)
            Log.i("Service", "Sending broadcast message")
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        pictureMessenger = Messenger(MessageHandler(this))
        return pictureMessenger.binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        if (job != null && job?.isActive!!) {
            job?.cancel()
            Log.i("Coroutine", "Job is canceled")
        }
        Log.i("Service", "Service is unbind by client")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        Log.i("Service", "Service is destroyed")
        super.onDestroy()
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

    private fun createFile(imageBitMap: Bitmap?): String {
        val filename = "image" + imageBitMap.hashCode()

        val fileStream = this.openFileOutput(filename, Context.MODE_PRIVATE)
        imageBitMap?.compress(Bitmap.CompressFormat.PNG, 100, fileStream)
        fileStream.close()

        return File(this.filesDir, filename).absolutePath
    }

    private fun startCoroutine(messenger: Messenger, url: String) {
        job = scope.launch {
            Log.i("Thread Coroutine", Thread.currentThread().name)
            val imageBitMap = downloadImage(url)
            if (!isActive) return@launch
            val imagePath = createFile(imageBitMap)
            val message = Message.obtain(null, MSG_TO_CLIENT, imagePath)
            messenger.send(message)
        }
    }
}
```