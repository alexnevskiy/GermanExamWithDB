# Цели работы

- Ознакомиться с принципами и получить практические навыки разработки UI тестов для Android приложений.

# 1. Простейший UI тест

Ознакомьтесь с Espresso Framework. Разработайте приложение, в котором есть одна кнопка (`Button`) и одно текстовое поле (`EditText`). При (первом) нажатии на кнопку текст на кнопке должен меняться.

Напишите Espresso тест, который проверяет, что при повороте экрана  содержимое текстового поля (каким бы оно ни было) сохраняется, а надпись на кнопке сбрасывается в исходное состояние.

![](https://raw.githubusercontent.com/alexnevskiy/GermanExam/master/labs/images/SimpleUITest.png)

Для решения данной задачи подключён Espresso Framework через зависимости в Gradle и написано простейшее приложение, в котором находятся одна кнопка с текстом "Tap on me" по центру экрана и текстовое поле с надписью "Enter anything" выше кнопки. При нажатии на кнопку текст на кнопке меняется на `counter + " tap"`, где `counter` - это количество нажатий на кнопку, то есть счётчик нажатий. Например, при первом нажатии на кнопку будет выведен текст "1 tap", при втором "2 tap", при сотом "100 tap" и так далее.

Для написания тестов создан отдельный класс `EspressoTest`, который хранит в себе 2 теста: `rotateFromPortraitToLandscape()` и `rotateFromLandscapeToPortrait()`. Оба теста различаются лишь тем, что в первом начальная ориентация экрана - `portrait`, а во втором - `landscape`. Структура каждого теста следующая:

- Поворот экрана устройства в заданное начальное положение (`portrait/landscape`)
- Проверка текста кнопки в начальном положении, то есть на неё ещё не нажимали (`"Tap on me"`)
- Нажатие на кнопку
- Проверка текста кнопки после первого нажатия (`"1 tap"`)
- Нажатие на кнопку
- Проверка текста кнопки после второго нажатия (`"2 tap"`)
- Очистка поля ввода и его редактирование (`"Espresso Test"`)
- Поворот экрана устройства в другое положение (`landscape/portrait`)
- Проверка текста кнопки после поворота экрана (`"Tap on me"`)
- Проверка текста текстового поля после поворота экрана (`"Espresso Test"`)

Все написанные тесты проходят проверку, что говорит о корректной работоспособности приложения, которой мы от него ожидаем. Значит описанное в задаче поведение наблюдается, то есть при повороте экрана содержимое текстового поля сохраняется, а надпись на кнопке сбрасывается в исходное состояние. Это связано с тем, что `EditText` автоматически сохраняет написанный текст в текстовом поле в пакет сохранённых состояний (saved state bundle). Этого автоматического сохранения помогает достичь метод `setFreezesText()`, который позволяет сохранять введённый текст при помощи `boolean` переменной. Если поставить `true` - текст сохранится, если `false` - то не будет сохраняться, по умолчанию для `EditText` стоит `true`, поэтому текст, который мы печатаем, и сохраняется. При более глубоком погружении в данный вопрос можно посмотреть в реализацию класса `TextView`, от которого наследуется используемый нами `EditText`. В родительском классе, то есть в `TextView`, переписан метод `onSaveInstanceState()`, который позволяет нам сохранять информацию при разрушении Activity. Если заглянуть внутрь реализации данного метода, то можно заметить, что при установленной переменной-флага `mFreezesText` в `true`, информация внутри `TextView` сохраняется, в противном случае не сохраняется. Именно поэтому без переписывания таких методов как `onSaveInstanceState()` и `onRestoreInstanceState()` информация внутри `EditText` сохраняется.

# 2. Тестирование навигации

Возьмите приложение из Лаб №3 о навигации (любое из решений). Напишите UI тесты, проверяющие навигацию между 4мя исходными Activity/Fragment (1-2-3-About). В отчёте опишите, что проверяет каждый тест.

Так как я делаю лабораторные работы на основе своего мобильного приложения, то и тестирование навигации будет проводиться на одном из его решений в 3 лабораторной работе, а именно на решении при помощи фрагментов, так как там функционал немного проще, чем в решении при помощи Activity, и можно спокойно проверить весь граф навигации без дополнительных нажатий на различные кнопки. Тесты будут находиться в проекте самой 3 лабораторной работы, чтобы лишний раз её не копировать и подключать, поэтому класс с тестами `EspressoTest` можно найти в директории приложения с фрагментами (`GermanExamWithFragment`).

## Тестирование навигации "вперёд"

Для тестирования работы навигации только при нажатии кнопок типа "Далее", "Настройки" и так далее, то есть мы постоянно будем двигаться "вперёд" и рано или поздно придём в тупик (конечный фрагмент в навигации), написано 4 теста, которые охватывают все возможные пути прохода по приложению из начального экрана при запуске.

Для удобства написания тестов были созданы приватные методы, которые делают проверку на наличие конкретных `View` для каждого фрагмента.

### fromHomeScreenToSettings()

Первый тест под названием `fromHomeScreenToSettings()` проходит от начального экрана до меню настроек. Структура теста следующая:

- Делается проверка всех `View` на начальном экране
- Переход к меню через кнопку "Далее"
- Делается проверка всех `View` в главном меню приложения
- Переход к настройкам через кнопку "Настройки"
- Делается проверка всех `View` в настройках приложения

### fromHomeScreenToVariants()

Второй тест `fromHomeScreenToVariants()` проходит от начального экрана до меню выбора варианта. Данный метод такой же тривиальный, как и прошлый, но при переходе в меню выбора варианта проходит проверка на наличие всех 25 кнопок, которые создаются программным путём, а не заранее прописанными в .xml файле, поэтому я решил этот тест вынести отдельно, хоть весь этот тест и используется далее в 4 тесте. Структура теста мало чем отличается от предыдущего:

- Делается проверка всех `View` на начальном экране
- Переход к меню через кнопку "Далее"
- Делается проверка всех `View` в главном меню приложения
- Переход к меню выбора варианта через кнопку "Варианты ЕГЭ"
- Делается проверка всех `View` в меню выбора варианта

### fromHomeScreenToExamThroughMenu()

Третий тест `fromHomeScreenToExamThroughMenu()` проходит от начального экрана до 1 задания ЕГЭ через меню. Структура теста следующая:

- Делается проверка всех `View` на начальном экране
- Переход к меню через кнопку "Далее"
- Делается проверка всех `View` в главном меню приложения
- Переход к стартовому экрана экзамена через кнопку "Экзамен"
- Делается проверка всех `View` на стартовом экране экзамена
- Переход к первому заданию через кнопку "Prüfung starten"
- Делается проверка всех `View` в первом задании

### fromHomeScreenToExamThroughVariants()

Последний четвёртый тест fromHomeScreenToExamThroughVariants() проходит от начального экрана до 1 задания ЕГЭ через меню выбора варианта. Структура теста следующая:

- Делается проверка всех `View` на начальном экране
- Переход к меню через кнопку "Далее"
- Делается проверка всех `View` в главном меню приложения
- Переход к меню выбора варианта через кнопку "Варианты ЕГЭ"
- Делается проверка всех `View` в меню выбора варианта
- Переход к стартовому экрана экзамена через кнопку "1" (может быть любая кнопка)
- Делается проверка всех `View` на стартовом экране экзамена
- Переход к первому заданию через кнопку "Prüfung starten"
- Делается проверка всех `View` в первом задании

## Проверка глубины BaskStack

Для тестирования глубины BackStack написаны 3 теста: простой, средний и покрывающий всё приложение, то есть он проходится по всем фрагментам.

### simpleBackStackTest()

Этот простой тест демонстрирует возможность перехода из одного фрагмента в предыдущий, который находится в BackStack, при помощи системной кнопки "Назад". Достигаем мы этой возможности при помощи статического метода `pressBack()` класса Espresso. Структура теста следующая:

- Делается проверка всех `View` на начальном экране
- Переход к меню через кнопку "Далее"
- Делается проверка всех `View` в главном меню приложения
- Возвращение к предыдущему фрагменту (начальный экран) через системную кнопку "Назад"
- Делается проверка всех `View` на начальном экране

### mediumBackStackTest()

Данный средний тест демонстрирует возможность перехода от начального экрана до 1 задания ЕГЭ через меню и обратно. Структура теста следующая:

- Делается проверка всех `View` на начальном экране
- Переход к меню через кнопку "Далее"
- Делается проверка всех `View` в главном меню приложения
- Переход к стартовому экрана экзамена через кнопку "Экзамен"
- Делается проверка всех `View` на стартовом экране экзамена
- Переход к первому заданию через кнопку "Prüfung starten"
- Делается проверка всех `View` в первом задании
- Возвращение к предыдущему фрагменту (стартовый экран экзамена) через системную кнопку "Назад"
- Делается проверка всех `View` на стартовом экране экзамена
- Возвращение к предыдущему фрагменту (главное меню) через системную кнопку "Назад"
- Делается проверка всех `View` в главном меню приложения
- Возвращение к предыдущему фрагменту (начальный экран) через системную кнопку "Назад"
- Делается проверка всех `View` на начальном экране

### fullBackStackTest()

Последний тест охватывает все фрагменты приложения, то есть он проходится по всем узлам графа навигации. Структура теста следующая:

- Делается проверка всех `View` на начальном экране
- Переход к меню через кнопку "Далее"
- Делается проверка всех `View` в главном меню приложения
- Переход к стартовому экрана экзамена через кнопку "Экзамен"
- Делается проверка всех `View` на стартовом экране экзамена
- Переход к первому заданию через кнопку "Prüfung starten"
- Делается проверка всех `View` в первом задании
- Возвращение к предыдущему фрагменту (стартовый экран экзамена) через системную кнопку "Назад"
- Делается проверка всех `View` на стартовом экране экзамена
- Возвращение к предыдущему фрагменту (главное меню) через системную кнопку "Назад"
- Делается проверка всех `View` в главном меню приложения
- Переход к меню выбора варианта через кнопку "Варианты ЕГЭ"
- Делается проверка всех `View` в меню выбора варианта
- Переход к стартовому экрана экзамена через кнопку "1" (может быть любая кнопка)
- Делается проверка всех `View` на стартовом экране экзамена
- Переход к первому заданию через кнопку "Prüfung starten"
- Делается проверка всех `View` в первом задании
- Возвращение к предыдущему фрагменту (стартовый экран экзамена) через системную кнопку "Назад"
- Делается проверка всех `View` на стартовом экране экзамена
- Возвращение к предыдущему фрагменту (меню выбора варианта) через системную кнопку "Назад"
- Делается проверка всех `View` в меню выбора варианта
- Возвращение к предыдущему фрагменту (главное меню) через системную кнопку "Назад"
- Делается проверка всех `View` в главном меню приложения
- Переход к настройкам через кнопку "Настройки"
- Делается проверка всех `View` в настройках приложения
- Возвращение к предыдущему фрагменту (главное меню) через системную кнопку "Назад"
- Делается проверка всех `View` в главном меню приложения
- Возвращение к предыдущему фрагменту (начальный экран) через системную кнопку "Назад"
- Делается проверка всех `View` на начальном экране

## Проверка нижнего меню навигации

Для тестирования работоспособности нижнего меню навигации написан один тест, который проверяет появление диалогового окна при нажатии на нижнее меню навигации, а также его закрытие при помощи кнопки "Сохранить" и системной кнопки "Назад". Для проверки появления диалогового окна, а не другого фрагмента, например, используется комбинация методов `inRoot(isDialog())`. Метод `inRoot()` делает `ViewInteraction` привязанным к корневому `View`, который мы подаём ему на вход, то есть метод `isDialog()`. Метод `isDialog()` возвращает `Matcher<Root>`, соответствующий корневым `View`, которые являются диалоговыми окнами (то есть не являются окном возобновляемой в данный момент Activity). Класс `ViewInteraction` предоставляет основной интерфейс для авторов тестов для выполнения действий или утверждений в `View`. Структура теста следующая:

- Делается проверка всех `View` на начальном экране
- Переход к меню через кнопку "Далее"
- Делается проверка всех `View` в главном меню приложения
- Переход к диалоговому окну при нажатии на нижнее навигационное меню
- Делается проверка всех `View` в диалоговом окне (3 EditText и кнопка "Сохранить")
- Закрытие диалогового окна при помощи нажатия на кнопку "Сохранить"
- Делается проверка всех `View` в главном меню приложения
- Переход к настройкам через кнопку "Настройки"
- Делается проверка всех `View` в настройках приложения
- Переход к диалоговому окну при нажатии на нижнее навигационное меню
- Делается проверка всех `View` в диалоговом окне (3 EditText и кнопка "Сохранить")
- Закрытие диалогового окна при помощи нажатия на системную кнопку "Назад"
- Делается проверка всех `View` в настройках приложения

# 3. Доработка приложения из Лаб №3

Попробуйте применить принцип TDD: у Вас в руках есть тест (из  предыдущего пункта), который описывает некоторое желаемое поведение. Три решения Лаб №3 (два с помощью Activity и одно с помощью Fragment)  должны приводить к одному и тому же результату (с т.з. пользователя).  Убедитесь, что все три варианта решения проходят разработанные тесты.  При необходимости исправьте программу или тесты (в тестах тоже бывают  ошибки и неточности).

Как я писал ранее в решении 2 задачи лабораторной работы, функционал приложения с Activity отличается от приложения с фрагментами, поэтому достичь поставленной задачи при одинаковых тестах невозможно на всех трёх решениях. Принято решение переписать тест из предыдущего пункта так, чтобы он работал на двух реализациях приложения с Activity без внесения в него правок для каждого из двух решений. Все тесты успешно проходят проверку как при решении со `startActivityForResult()`, так и при решении с флагами.

## Тестирование навигации "вперёд"

При тестировании навигации "вперёд" изменения не потребовались, так как для всех решений переход между Activity/Fragment через кнопки навигации происходит одинаково.

## Проверка глубины BackStack

На данном этапе проверки уже внесены изменения в методы, а также добавлен один вспомогательный метод для проверки всплывающего диалогового окна при нажатии системной кнопки "Назад".

### simpleBackStackTest()

Так как в решении с Activity при переходе из начального экрана в главное меню приложения доступ к начальному экрану за ненадобностью является закрытым, то пришлось изменить структуру теста. Теперь она следующая:

- Делается проверка всех `View` на начальном экране
- Переход к меню через кнопку "Далее"
- Делается проверка всех `View` в главном меню приложения
- Переход к меню выбора варианта через кнопку "Варианты ЕГЭ"
- Делается проверка всех `View` в меню выбора варианта
- Возвращение к предыдущему Activity (главное меню приложения) через системную кнопку "Назад"
- Делается проверка всех `View` в главном меню приложения

### mediumBackStackTest()

Как мы помним из реализации приложения с Activity из 3 лабораторной работы, при нажатии системной кнопки "Назад" на стартовом экране экзамена и в первом задании ЕГЭ появляется диалоговое окно, где пользователю предоставляется выбор, куда он хочет перейти: на рабочий стол, в главное меню или в меню выбора варианта. Поэтому структура теста немного изменилась, теперь она следующая:

- Делается проверка всех `View` на начальном экране
- Переход к меню через кнопку "Далее"
- Делается проверка всех `View` в главном меню приложения
- Переход к стартовому экрана экзамена через кнопку "Экзамен"
- Делается проверка всех `View` на стартовом экране экзамена
- Переход к первому заданию через кнопку "Prüfung starten"
- Делается проверка всех `View` в первом задании
- Появление диалогового окна при нажатии на системную кнопку "Назад"
- Делается проверка всех `View` в диалоговом окне (титул окна, 3 кнопки: "Выбор варианта", "Главное меню", "Рабочий стол")
- Переход к главному меню через кнопку "Главное меню" диалогового окна
- Делается проверка всех `View` в главном меню приложения

### fullBackStackTest()

Последний тест также охватывает все Activity приложения, как и в тестах для фрагментов, но с некоторыми изменениями в виде проверки диалогового окна и нажатия в нём кнопок навигации. Структура теста следующая:

- Делается проверка всех `View` на начальном экране
- Переход к меню через кнопку "Далее"
- Делается проверка всех `View` в главном меню приложения
- Переход к стартовому экрана экзамена через кнопку "Экзамен"
- Делается проверка всех `View` на стартовом экране экзамена
- Переход к первому заданию через кнопку "Prüfung starten"
- Делается проверка всех `View` в первом задании
- Появление диалогового окна при нажатии на системную кнопку "Назад"
- Делается проверка всех `View` в диалоговом окне (титул окна, 3 кнопки: "Выбор варианта", "Главное меню", "Рабочий стол")
- Переход к меню выбора варианта через кнопку "Выбор варианта" диалогового окна
- Делается проверка всех `View` в меню выбора варианта
- Возвращение к предыдущему Activity (главное меню) через системную кнопку "Назад"
- Делается проверка всех `View` в главном меню приложения
- Переход к меню выбора варианта через кнопку "Варианты ЕГЭ"
- Делается проверка всех `View` в меню выбора варианта
- Переход к стартовому экрана экзамена через кнопку "1" (может быть любая кнопка)
- Делается проверка всех `View` на стартовом экране экзамена
- Переход к первому заданию через кнопку "Prüfung starten"
- Делается проверка всех `View` в первом задании
- Появление диалогового окна при нажатии на системную кнопку "Назад"
- Делается проверка всех `View` в диалоговом окне (титул окна, 3 кнопки: "Выбор варианта", "Главное меню", "Рабочий стол")
- Переход к главному меню приложения через кнопку "Главное меню" диалогового окна
- Делается проверка всех `View` в главном меню приложения
- Переход к настройкам через кнопку "Настройки"
- Делается проверка всех `View` в настройках приложения
- Возвращение к предыдущему Activity (главное меню) через системную кнопку "Назад"
- Делается проверка всех `View` в главном меню приложения

## Проверка нижнего меню навигации

На данном этапе разработки приложения появилась возможность сохранения данных пользователя при первом включении и изменении данных через нижнее навигационное меню. Поэтому в данном тесте реализована не только проверка открытия и закрытия диалогового окна при нажатии на нижнее меню навигации, но и сохранение данных пользователя, которые можно увидеть в настройках приложения. Так как Espresso ругается на ввод текста кириллицей, пришлось вводить данные пользователя на английском языке. Структура теста следующая:

- Делается проверка всех `View` на начальном экране
- Переход к меню через кнопку "Далее"
- Делается проверка всех `View` в главном меню приложения
- Переход к диалоговому окну при нажатии на нижнее навигационное меню
- Делается проверка всех `View` в диалоговом окне (3 EditText и кнопка "Сохранить")
- Ввод данных пользователя (Alexander, Kobyzhev, 3530901/80202)
- Закрытие диалогового окна и сохранение данных при помощи нажатия на кнопку "Сохранить"
- Делается проверка всех `View` в главном меню приложения
- Переход к настройкам через кнопку "Настройки"
- Делается проверка всех `View` в настройках приложения
- Делается отдельная проверка введённых ранее данных пользователя
- Переход к диалоговому окну при нажатии на нижнее навигационное меню
- Делается проверка всех `View` в диалоговом окне (3 EditText и кнопка "Сохранить")
- Ввод данных пользователя (Ivan, Ivanov, 11A)
- Закрытие диалогового окна при помощи двойного нажатия на системную кнопку "Назад"
- Делается проверка всех `View` в настройках приложения
- Делается отдельная проверка введённых ранее данных пользователя (отображается "Ученик: Alexander Kobyzhev", "Класс: 3530901/80202", так как диалоговое окно было закрыто через системную кнопку назад, а не через кнопку "Сохранить")

# Выводы:

В процессе выполнения данной лабораторной работы в среде разработки Android Studio произведено знакомство с принципами и получены практические навыки разработки UI тестов для Android приложений. Написаны простейшие UI тесты для приложения, состоящего всего лишь из одной кнопки и одного текстового поля. Произведено тестирование навигации более простого в реализации приложения из 3 лабораторной работы с фрагментами, а также переписаны некоторые тесты для более сложного в плане функционала приложения с Activity. Всего получилось 8 UI тестов для основного приложения: как простые, демонстрирующие возможности перехода между двумя окнами, так и трудные, охватывающие всё приложение целиком. Все тесты успешно проходят проверку как при решении со `startActivityForResult()`, так и при решении с флагами.

Таким образом, можно выделить основные компоненты Espresso:

- **Espresso** – это основной класс, который является точкой входа для взаимодействия с `View` через методы `onView()` и `onData()`. Также он предоставляет методы по типу нажатия на системную кнопку "Назад" (`pressBack()`)
- **ViewMatchers** – это коллекция классов сопоставления представлений (`View`) для сопоставления и поиска элементов/представлений пользовательского интерфейса в иерархии представлений экрана активности Android.
- **ViewActions** – это коллекция объектов `ViewAction` для взаимодействия с компонентами, например нажатие (`click()`) или удаление текста (`clearText()`).
- **ViewAssertions** – это коллекция объектов `ViewAssertion`, которая позволяет утвердить состояние какого-либо `View`.

# Приложение:

## Листинг 1: activity_main для UI теста

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <Button
        android:id="@+id/button"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@string/tap_on_me"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <EditText
        android:id="@+id/editText"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="64dp"
        android:ems="10"
        android:inputType="textPersonName"
        android:text="@string/enter_anything"
        app:layout_constraintBottom_toTopOf="@+id/button"
        app:layout_constraintEnd_toEndOf="@+id/button"
        app:layout_constraintStart_toStartOf="@+id/button"
        app:layout_constraintTop_toTopOf="parent" />
</androidx.constraintlayout.widget.ConstraintLayout>
```

## Листинг 2: ActivityMain.java для UI теста

```java
public class MainActivity extends AppCompatActivity {

    int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter++;
                button.setText(counter + " tap");
            }
        });
    }
}
```

## Листинг 3: EspressoTest.java для UI теста

```java
@RunWith(AndroidJUnit4.class)
public class EspressoTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void rotateFromPortraitToLandscape() {
        activityRule.getScenario().onActivity(activity ->
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT));
        onView(withId(R.id.button)).check(matches(withText("Tap on me")));
        onView(withId(R.id.button)).perform(click());
        onView(withId(R.id.button)).check(matches(withText("1 tap")));
        onView(withId(R.id.button)).perform(click());
        onView(withId(R.id.button)).check(matches(withText("2 tap")));
        onView(withId(R.id.editText)).perform(clearText());
        onView(withId(R.id.editText)).perform(typeText("Espresso Test"));
        activityRule.getScenario().onActivity(activity ->
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE));
        onView(withId(R.id.button)).check(matches(withText("Tap on me")));
        onView(withId(R.id.editText)).check(matches(withText("Espresso Test")));
    }

    @Test
    public void rotateFromLandscapeToPortrait() {
        activityRule.getScenario().onActivity(activity ->
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE));
        onView(withId(R.id.button)).check(matches(withText("Tap on me")));
        onView(withId(R.id.button)).perform(click());
        onView(withId(R.id.button)).check(matches(withText("1 tap")));
        onView(withId(R.id.button)).perform(click());
        onView(withId(R.id.button)).check(matches(withText("2 tap")));
        onView(withId(R.id.editText)).perform(clearText());
        onView(withId(R.id.editText)).perform(typeText("Espresso Test"));
        activityRule.getScenario().onActivity(activity ->
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT));
        onView(withId(R.id.button)).check(matches(withText("Tap on me")));
        onView(withId(R.id.editText)).check(matches(withText("Espresso Test")));
    }
}
```

## Листинг 4: EspressoTest.java для приложения с фрагментами

```java
@RunWith(AndroidJUnit4.class)
public class EspressoTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    private void homeScreenCheck() {
        onView(withId(R.id.buttonStart)).check(matches(isDisplayed()));
        onView(withId(R.id.editTextTextPersonName)).check(matches(isDisplayed()));
        onView(withId(R.id.editTextTextPersonSurname)).check(matches(isDisplayed()));
        onView(withId(R.id.editTextTextPersonClass)).check(matches(isDisplayed()));
    }

    private void menuCheck() {
        onView(withId(R.id.button_exam)).check(matches(isDisplayed()));
        onView(withId(R.id.button_variants)).check(matches(isDisplayed()));
        onView(withId(R.id.button_settings)).check(matches(isDisplayed()));
        onView(withId(R.id.bottom_navigation)).check(matches(isDisplayed()));
    }

    private void settingsCheck() {
        onView(withId(R.id.microphone_test)).check(matches(isDisplayed()));
        onView(withId(R.id.change_interface)).check(matches(isDisplayed()));
        onView(withId(R.id.about_application)).check(matches(isDisplayed()));
        onView(withId(R.id.settings)).check(matches(isDisplayed()));
        onView(withId(R.id.student_name)).check(matches(isDisplayed()));
        onView(withId(R.id.person_class)).check(matches(isDisplayed()));
    }

    private void variantsCheck() {
        for (int i = 0; i < 25; i++) {
            onView(withText(Integer.toString(i + 1))).check(matches(isDisplayed()));
        }
    }

    private void variantStartPageCheck() {
        onView(withId(R.id.start_test)).check(matches(isDisplayed()));
        onView(withId(R.id.button_start_test)).check(matches(isDisplayed()));
    }

    private void taskOneCheck() {
        onView(allOf(withId(R.id.Task1), withText("Aufgabe 1"))).check(matches(isDisplayed()));
        onView(withId(R.id.clock)).check(matches(isDisplayed()));
        onView(withId(R.id.prep_ans)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.task1_logo), withText("1"))).check(matches(isDisplayed()));
        onView(withId(R.id.task1_text)).check(matches(isDisplayed()));
        onView(withId(R.id.text1)).check(matches(isDisplayed()));
        onView(withId(R.id.preparation)).check(matches(isDisplayed()));
        onView(withId(R.id.timeline)).check(matches(isDisplayed()));
        onView(withId(R.id.time_remaining)).check(matches(isDisplayed()));
    }

    private void dialogDataChangeCheck() {
        onView(withId(R.id.editTextTextPersonName))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
        onView(withId(R.id.editTextTextPersonSurname))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
        onView(withId(R.id.editTextTextPersonClass))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
        onView(withText("Сохранить"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
    }

    private void pressBack() {
        onView(isRoot()).perform(ViewActions.pressBack());
    }

    /**
    Тестирование навигации "вперёд"
     **/

    @Test
    public void fromHomeScreenToSettings() {
        homeScreenCheck();
        onView(withId(R.id.buttonStart)).perform(click());
        menuCheck();
        onView(withId(R.id.button_settings)).perform(click());
        settingsCheck();
    }

    @Test
    public void fromHomeScreenToVariants() {
        homeScreenCheck();
        onView(withId(R.id.buttonStart)).perform(click());
        menuCheck();
        onView(withId(R.id.button_variants)).perform(click());
        variantsCheck();
    }

    @Test
    public void fromHomeScreenToExamThroughMenu() {
        homeScreenCheck();
        onView(withId(R.id.buttonStart)).perform(click());
        menuCheck();
        onView(withId(R.id.button_exam)).perform(click());
        variantStartPageCheck();
        onView(withId(R.id.button_start_test)).perform(click());
        taskOneCheck();
    }

    @Test
    public void fromHomeScreenToExamThroughVariants() {
        homeScreenCheck();
        onView(withId(R.id.buttonStart)).perform(click());
        menuCheck();
        onView(withId(R.id.button_variants)).perform(click());
        variantsCheck();
        onView(withText("1")).perform(click());
        variantStartPageCheck();
        onView(withId(R.id.button_start_test)).perform(click());
        taskOneCheck();
    }

    /**
     Проверка глубины BackStack
     **/

    @Test
    public void simpleBackStackTest() {
        homeScreenCheck();
        onView(withId(R.id.buttonStart)).perform(click());
        menuCheck();
        pressBack();
        homeScreenCheck();
    }

    @Test
    public void mediumBackStackTest() {
        homeScreenCheck();
        onView(withId(R.id.buttonStart)).perform(click());
        menuCheck();
        onView(withId(R.id.button_exam)).perform(click());
        variantStartPageCheck();
        onView(withId(R.id.button_start_test)).perform(click());
        taskOneCheck();
        pressBack();
        variantStartPageCheck();
        pressBack();
        menuCheck();
        pressBack();
        homeScreenCheck();
    }

    @Test
    public void fullBackStackTest() {
        homeScreenCheck();
        onView(withId(R.id.buttonStart)).perform(click());
        menuCheck();
        onView(withId(R.id.button_exam)).perform(click());
        variantStartPageCheck();
        onView(withId(R.id.button_start_test)).perform(click());
        taskOneCheck();
        pressBack();
        variantStartPageCheck();
        pressBack();
        menuCheck();
        onView(withId(R.id.button_variants)).perform(click());
        variantsCheck();
        onView(withText("1")).perform(click());
        variantStartPageCheck();
        onView(withId(R.id.button_start_test)).perform(click());
        taskOneCheck();
        pressBack();
        variantStartPageCheck();
        pressBack();
        variantsCheck();
        pressBack();
        menuCheck();
        onView(withId(R.id.button_settings)).perform(click());
        settingsCheck();
        pressBack();
        menuCheck();
        pressBack();
        homeScreenCheck();
    }

    /**
     Проверка нижнего меню навигации
     **/

    @Test
    public void bottomNavigationTest() {
        homeScreenCheck();
        onView(withId(R.id.buttonStart)).perform(click());
        menuCheck();
        onView(withId(R.id.bottom_navigation)).perform(click());
        dialogDataChangeCheck();
        onView(withText("Сохранить"))
                .inRoot(isDialog())
                .perform(click());
        menuCheck();
        onView(withId(R.id.button_settings)).perform(click());
        settingsCheck();
        onView(withId(R.id.bottom_navigation)).perform(click());
        dialogDataChangeCheck();
        pressBack();
        settingsCheck();
    }
}
```

## Листинг 5: EspressoText.java для приложения с Activity

```java
@RunWith(AndroidJUnit4.class)
public class EspressoTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    private void homeScreenCheck() {
        onView(withId(R.id.buttonStart)).check(matches(isDisplayed()));
        onView(withId(R.id.editTextTextPersonName)).check(matches(isDisplayed()));
        onView(withId(R.id.editTextTextPersonSurname)).check(matches(isDisplayed()));
        onView(withId(R.id.editTextTextPersonClass)).check(matches(isDisplayed()));
    }

    private void menuCheck() {
        onView(withId(R.id.button_exam)).check(matches(isDisplayed()));
        onView(withId(R.id.button_variants)).check(matches(isDisplayed()));
        onView(withId(R.id.button_settings)).check(matches(isDisplayed()));
        onView(withId(R.id.bottom_navigation)).check(matches(isDisplayed()));
    }

    private void settingsCheck() {
        onView(withId(R.id.microphone_test)).check(matches(isDisplayed()));
        onView(withId(R.id.change_interface)).check(matches(isDisplayed()));
        onView(withId(R.id.about_application)).check(matches(isDisplayed()));
        onView(withId(R.id.settings)).check(matches(isDisplayed()));
        onView(withId(R.id.student_name)).check(matches(isDisplayed()));
        onView(withId(R.id.person_class)).check(matches(isDisplayed()));
    }

    private void variantsCheck() {
        for (int i = 0; i < 25; i++) {
            onView(withText(Integer.toString(i + 1))).check(matches(isDisplayed()));
        }
    }

    private void variantStartPageCheck() {
        onView(withId(R.id.start_test)).check(matches(isDisplayed()));
        onView(withId(R.id.button_start_test)).check(matches(isDisplayed()));
    }

    private void taskOneCheck() {
        onView(allOf(withId(R.id.Task1), withText("Aufgabe 1"))).check(matches(isDisplayed()));
        onView(withId(R.id.clock)).check(matches(isDisplayed()));
        onView(withId(R.id.prep_ans)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.task1_logo), withText("1"))).check(matches(isDisplayed()));
        onView(withId(R.id.task1_text)).check(matches(isDisplayed()));
        onView(withId(R.id.text1)).check(matches(isDisplayed()));
        onView(withId(R.id.preparation)).check(matches(isDisplayed()));
        onView(withId(R.id.timeline)).check(matches(isDisplayed()));
        onView(withId(R.id.time_remaining)).check(matches(isDisplayed()));
    }

    private void dialogDataChangeCheck() {
        onView(withId(R.id.editTextTextPersonName))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
        onView(withId(R.id.editTextTextPersonSurname))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
        onView(withId(R.id.editTextTextPersonClass))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
        onView(withText("Сохранить"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
    }

    private void pressBack() {
        onView(isRoot()).perform(ViewActions.pressBack());
    }

    private void dialogExamCheck() {
        onView(withText("Куда Вы хотите перейти?"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
        onView(withText("Выбор варианта"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
        onView(withText("Главное меню"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
        onView(withText("Рабочий стол"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
    }

    /**
     Тестирование навигации "вперёд"
     **/

    @Test
    public void fromHomeScreenToSettings() {
        homeScreenCheck();
        onView(withId(R.id.buttonStart)).perform(click());
        menuCheck();
        onView(withId(R.id.button_settings)).perform(click());
        settingsCheck();
    }

    @Test
    public void fromHomeScreenToVariants() {
        homeScreenCheck();
        onView(withId(R.id.buttonStart)).perform(click());
        menuCheck();
        onView(withId(R.id.button_variants)).perform(click());
        variantsCheck();
    }

    @Test
    public void fromHomeScreenToExamThroughMenu() {
        homeScreenCheck();
        onView(withId(R.id.buttonStart)).perform(click());
        menuCheck();
        onView(withId(R.id.button_exam)).perform(click());
        variantStartPageCheck();
        onView(withId(R.id.button_start_test)).perform(click());
        taskOneCheck();
    }

    @Test
    public void fromHomeScreenToExamThroughVariants() {
        homeScreenCheck();
        onView(withId(R.id.buttonStart)).perform(click());
        menuCheck();
        onView(withId(R.id.button_variants)).perform(click());
        variantsCheck();
        onView(withText("1")).perform(click());
        variantStartPageCheck();
        onView(withId(R.id.button_start_test)).perform(click());
        taskOneCheck();
    }

    /**
     Проверка глубины BackStack
     **/

    @Test
    public void simpleBackStackTest() {
        homeScreenCheck();
        onView(withId(R.id.buttonStart)).perform(click());
        menuCheck();
        onView(withId(R.id.button_variants)).perform(click());
        variantsCheck();
        pressBack();
        menuCheck();
    }

    @Test
    public void mediumBackStackTest() {
        homeScreenCheck();
        onView(withId(R.id.buttonStart)).perform(click());
        menuCheck();
        onView(withId(R.id.button_exam)).perform(click());
        variantStartPageCheck();
        onView(withId(R.id.button_start_test)).perform(click());
        taskOneCheck();
        pressBack();
        dialogExamCheck();
        onView(withText("Главное меню"))
                .inRoot(isDialog())
                .perform(click());
        menuCheck();
    }

    @Test
    public void fullBackStackTest() {
        homeScreenCheck();
        onView(withId(R.id.buttonStart)).perform(click());
        menuCheck();
        onView(withId(R.id.button_exam)).perform(click());
        variantStartPageCheck();
        onView(withId(R.id.button_start_test)).perform(click());
        taskOneCheck();
        pressBack();
        dialogExamCheck();
        onView(withText("Выбор варианта"))
                .inRoot(isDialog())
                .perform(click());
        variantsCheck();
        pressBack();
        menuCheck();
        onView(withId(R.id.button_variants)).perform(click());
        variantsCheck();
        onView(withText("1")).perform(click());
        variantStartPageCheck();
        onView(withId(R.id.button_start_test)).perform(click());
        taskOneCheck();
        pressBack();
        dialogExamCheck();
        onView(withText("Главное меню"))
                .inRoot(isDialog())
                .perform(click());
        menuCheck();
        onView(withId(R.id.button_settings)).perform(click());
        settingsCheck();
        pressBack();
        menuCheck();
    }

    /**
     Проверка нижнего меню навигации
     **/

    @Test
    public void bottomNavigationTest() {
        homeScreenCheck();
        onView(withId(R.id.buttonStart)).perform(click());
        menuCheck();
        onView(withId(R.id.bottom_navigation)).perform(click());
        dialogDataChangeCheck();
        onView(withId(R.id.editTextTextPersonName))
                .inRoot(isDialog())
                .perform(typeText("Alexander"));
        onView(withId(R.id.editTextTextPersonSurname))
                .inRoot(isDialog())
                .perform(typeText("Kobyzhev"));
        onView(withId(R.id.editTextTextPersonClass))
                .inRoot(isDialog())
                .perform(typeText("3530901/80202"));
        onView(withText("Сохранить"))
                .inRoot(isDialog())
                .perform(click());
        menuCheck();
        onView(withId(R.id.button_settings)).perform(click());
        settingsCheck();
        onView(withText("Ученик: Alexander Kobyzhev")).check(matches(isDisplayed()));
        onView(withText("Класс: 3530901/80202")).check(matches(isDisplayed()));
        onView(withId(R.id.bottom_navigation)).perform(click());
        dialogDataChangeCheck();
        onView(withId(R.id.editTextTextPersonName))
                .inRoot(isDialog())
                .perform(typeText("Ivan"));
        onView(withId(R.id.editTextTextPersonSurname))
                .inRoot(isDialog())
                .perform(typeText("Ivanov"));
        onView(withId(R.id.editTextTextPersonClass))
                .inRoot(isDialog())
                .perform(typeText("11A"));
        pressBack();
        pressBack();
        settingsCheck();
        onView(withText("Ученик: Alexander Kobyzhev")).check(matches(isDisplayed()));
        onView(withText("Класс: 3530901/80202")).check(matches(isDisplayed()));
    }
}
```