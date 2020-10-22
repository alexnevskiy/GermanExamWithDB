# Цели работы

- Ознакомиться с принципами работы adapter-based views
- Получить практические навки разработки адаптеров для view

# 1. Знакомсотво с библиотекой (unit test)

Ознакомьтесь со strict mode библиотеки, проиллюстрировав его работу unit-тестом.

Библиотека имеет 2 режима работы: normal и strict. В strict mode работает искусственное ограничение: в памяти нельзя хранить более `name.ank.lab4.BibConfig#maxValid=20` записей одновременно. При извлечении `maxValid+1`-ой записи 1-ая извелеченная запись становится невалидной (при доступе к полям кидаются исключения `IllegalStateException` с сообщениями типа: `This object has already been invalidated. myOrder=%d, latestOrder=%d`).

В классе `BibDatabaseTest.java`, в котором находятся тесты для библиотеки, в реализованных тестовых методах запрашиваются экземпляры класса `BibEntry`, которые хранят информацию о записях в формате bibtex. Данные экземпляры класса подсчитывают свой порядковый номер и сравнивают его при запросе (например `getField()`). Если же номер уже не является валидным, то есть он не находится в том самом исскуственном количестве записей, которые могут храниться в памяти одновременно (`name.ank.lab4.BibConfig#maxValid=20`), то бросается `IllegalStateException`, который мы и должны обработать.

## Тест strictModeThrowsException()

Метод `strictModeThrowsException()` реализован практически как и метод `normalModeDoesNotThrowException()`, но с некоторыми изменениями. Так как у нас стоит ограничение в 20 записей, а при вызове первой `BibEntry` вне цикла мы присваиваем ей 1 порядковый номер, то и количество повторений в цикле сокращено до `cfg.maxValid - 1`.  Таким образом, при вызове последнего `BibEntry` его порядковый номер будет равняться 20, и исключение не будет выброшено. Далее в блоке кода `try` создаётся ещё один новый `BibEntry` уже с порядковым номером 21 и запрашивается метод `getType()` для нашей самой первой `BibEntry`. При вызове метода выбрасывается ошибка `IllegalStateException`, которая в свою очередь обрабатывается в блоке `catch`. Таким образом, тест проходит проверку.

## Тест shuffleFlag()

Для проверки работы флага `shuffle` создан новый файл `shuffle.bib` с двумя записями разного типа, так как файл `mixed.bib` содержит около 130 записей (достаточно трудно проверить схожесть всех 130 записей), а `references.bib` всего лишь одну. Первой записью в новом файле всегда хранится тип ARTICLE, поэтому мы открываем файл `shuffle.bib`, ставим флаг `shuffle = false` и достаём первую `BibEntry`, тип записи которой всегда является ARTICLE. Так мы ещё и проверяем рабоспособность выключенного флага `shuffle`, так как по умолчанию он включён. Из курса теории вероятностей мы знаем, что для приближения вероятности правильного срабатывания теста к 1 нам нужно увеличить количество записей и количество повторений в цикле. Ограничимся увеличением количеством повторений в цикле. Для этого мы в цикле из 10 повторений открываем файл `shuffle.bib` с флагом `shuffle = true` и сравниваем типы нового первого `BibEntry` и изначального. Если их типы различаются, то присваиваем переменной `check = true` и после окончания цикла делаем проверку `assertTrue(check)`. Таким образом, тест проходит проверку.

## Сборка biblib.jar

Все тесты пройдены, поэтому мы можем собрать .jar файл библиотеки. Для этого через консоль Windows открываем папку с проектом и запускаем `gradlew.bat build`. Результаты сборки доступны по пути `build/libs/biblib.jar`.

# 2. Знакомство с RecyclerView (Неоднородный список)

Напишите Android приложение, которое выводит все записи из bibtex файла на экран, используя предложенную библиотеку и `RecyclerView`.

## Изменения в biblib

В качестве исходных данных должен использоваться файл `publications_ferro.en.bib`, который содержит два неизвестных типа записи (@EDITORIAL и @SOFTWARE) для библиотеки `jbibtex`, которая и используется в данной нам библиотеке `biblib`. Так как данных двух типов нет в библиотеке, то и работа приложения с данным файлом невозможна, потому что при попытке чтения данных типов вылетает ошибка, и приложение закрывается. Для решения этой проблемы нужно добавить эти два типа в библиотеку `jbibtex` (а именно в класс `BibTeXEntry`), а затем и в данную нам библиотеку `biblib`.  Так как у нас нет исходников библиотеки `jbibtex`, то пришлось искать её в интернете. Как и у всех сторонних библиотек, код есть на [GitHub](https://github.com/jbibtex/jbibtex), но почему-то в нём не оказалось половины классов, которые нужны для полной работоспособности библиотеки. Для этого было принято решение: добавить (создать) недостающие классы вручную, и добавить в них код из импортированной библиотеки в `biblib`, так как в ней присутствуют все нужные классы. После полной переработки библиотеки `jbibtex` она скомпилирована в .jar файл и подключена к `biblib`. После подключения библиотеки в классе Types.java добавлены недостающие два типа записи:

```java
EDITORIAL(BibTeXEntry.TYPE_EDITORIAL),
SOFTWARE(BibTeXEntry.TYPE_SOFTWARE);
```

После этого библиотека `biblib` снова собрана в .jar файл и добавлена в основной проект приложения.

## Создание приложения с использованием RecyclerView

![]()

# Приложение:

## Листинг 1: strictModeThrowsException()

```java
@Test
public void strictModeThrowsException() throws IOException {
  BibDatabase database = openDatabase("/mixed.bib");
  BibConfig cfg = database.getCfg();
  cfg.strict = true;

  BibEntry first = database.getEntry(0);
  for (int i = 0; i < cfg.maxValid - 1; i++) {
    BibEntry unused = database.getEntry(0);
    assertNotNull("Should not throw any exception @" + i, first.getType());
  }

  try {
    BibEntry unused = database.getEntry(0);
    first.getType();
  } catch (IllegalStateException e) {
    System.out.println("Throw IllegalStateException with message: " + e.getMessage());
  }
}
```

## Листинг 2: shuffleFlag()

```java
@Test
public void shuffleFlag() throws IOException{
  boolean check = false;
  BibDatabase firstDatabase = openDatabase("/shuffle.bib");
  BibConfig cfg = firstDatabase.getCfg();
  cfg.shuffle = false;
  BibEntry first = firstDatabase.getEntry(0); // always ARTICLE

  for (int i = 0; i < 10; i++) {
    BibDatabase database = openDatabase("/shuffle.bib");
    BibConfig databaseCfg = database.getCfg();
    databaseCfg.shuffle = true;
    if (database.getEntry(0).getType() != first.getType()) check = true;
  }
  assertTrue(check);
}
```

## Листинг 3: shuffle.bib

```
@ARTICLE{MFP_62_ARTICLE_2019,
  AUTHOR = {Marzi, C. and Ferro, M. and Pirrelli, V.},
  TITLE = {A processing-oriented investigation of inflectional complexity},
  PDF = {http://www.ilc.cnr.it/~ferro/publications/A_processing_oriented_investigation_of_inflectional_complexity.pdf},
  JOURNAL = {Frontiers in Communication},
  VOLUME = {4(48)},
  PAGES = {1-23},
  DOI = {10.3389/fcomm.2019.00048},
  ISSN = {2297-900X},
  EDITOR = {Jarema, G.},
  YEAR = {2019},
  CLS = {IOA},
  SCH = {4},
  URL = {https://www.frontiersin.org/articles/10.3389/fcomm.2019.00048/full},
  AREA = {LINCOM},
}
@UNPUBLISHED{PCCDFGMNT_50_UNPUBLISHED_2020__submitted,
  AUTHOR = {Pirrelli, V. and Cappa, C. and Crepaldi, D. and Del Pinto, V. and Ferro, M. and Giulivi, S. and Marzi, C. and Nadalini, A. and Taxitari, L.},
  TITLE = {Tracking the pace of reading with finger movements},
  BOOKTITLE = {International Conference Words in the World (WOW'20)},
  YEAR = {2020, submitted},
  URL = {http://wordsintheworld.ca/wow-conference-2020/conference-schedule/},
  AREA = {LINCOM},
  TYPE = {INPROCEEDINGS},
}
```