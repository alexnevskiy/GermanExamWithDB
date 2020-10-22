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

## RecyclerView

![](https://raw.githubusercontent.com/alexnevskiy/GermanExam/master/labs/images/RecyclerView.png)

Для решения данной задачи нужно подключить к проекту зависимость в gradle файле, чтобы использовать RecyclerView: `implementation 'androidx.recyclerview:recyclerview:1.2.0-alpha06'`. В activity_main.xml добавлен RecyclerView, с которым мы и будем работать. Далее создан layout, который будет отображать элемент списка RecyclerView. В нём находится 4 TextView, которые отображают тип записи, название, автора и год написания, на мой взгляд, это самый содержательный и менее ёмкий формат для отображения данных.

## MainActivity

В данном классе извлекаются наши исходный файл с данными, который размещён в `raw` ресурсах. Здесь к нашему RecyclerView подключается `LinearLayoutManager`, который позволяет располагать данные в виде списка. Далее при помощи метода `setHasFixedSize()` задаётся, что размер RecyclerView будет фиксированного размера, так как в исходном файле конечное число записей. Также к RecyclerView добавлен `DividerItemDecoration` при помощи метода `addItemDecoration()`, который позволяет разделить данные в списке для более удобного отображения. В конце подключается `BibLibAdapter`, на вход которому подаётся извлечённый нами исходный файл.

## BibLibAdapter

Данный класс реализует адаптер для RecyclerView, обеспечивающий привязку данных к View, которые отображает RecyclerView (в нашем случае это TextView). `BibLibAdapter` содержит в себе вложенный статический класс `BibLibViewHolder`, который описывает представление элемента и данные о месте в RecyclerView. 

В конструкторе класса `BibLibAdapter` считывается при помощи `InputStreamReader()` информация из файла и создаётся экземпляр класса `BibDatabase()` для работы с данными файла. 

В методе `onCreateViewHolder()` создаётся экземпляр класса `LayoutInflater`, который позволяет из содержимого layout-файла создать View-элемент, и возвращается экземпляр нашего вложенного класса `BibLibViewHolder` с созданным View на входе.

В методе `onBindViewHolder()` происходит отображение данных в указанной позиции, то есть он обновляет содержимое наших TextView. При помощи метода `getEntry()` получаем объект класса `BibEntry`, который позволяет извлекать данные из записи. Далее меняем отображаемый текст в наших TextView на нужные данные полей конкретной записи. При изменении типа записи используется конструкция switch case для разного визуального отображения записей разного типа.

В методе `getItemCount()` выводится количество элементов в списке, то есть количество записей в файле.

# 3. Бесконечный список

Сделайте список из предыдущей задачи бесконечным: после последнего элемента все записи повторяются, начиная с первой.

Для того, чтобы сделать список "бесконечным", нужно всего лишь изменить 2 строчки кода, а именно:

В методе `onBindViewHolder()` при получении экземпляра класса указать не просто позицию, а остаток от деления позиции на количество записей в файле.

В методе `getItemCount()` возвращать не количество элементов в списке, а максимальное возможное число Integer.

# Выводы:

При выполнении данной лабораторной работы произведено ознакомление с принципами работы adapter-based views: разработано приложение, выводящее все записи из `bibtex` файла на экран, используя библиотеку biblib и RecyclerView. Для отображения записей различного рода разработан адаптер `BibLibAdapter`. Все элементы из заданного файла выведены списком. Также код адаптера модифицирован так, чтобы после прокручивания последнего элемента, все записи повторялись, начиная с первой.

Для упомянутой библиотеки написаны тесты для проверки двух использующихся флагов – `strict` (обозначающий режим, при котором в памяти может храниться ограниченное количество записей из файла) и `shuffle` (перемешивание извлечённых элементов). Написанные тесты успешно проходят, что позволяет собрать объектные файлы для последующего их использования в проектах в качестве библиотеки, что и было выполнено для работы над вторым пунктом.

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

## Листинг 4: activity_main.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity" >

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/recycler_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
</LinearLayout>
```

## Листинг 5: biblib_entry.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:padding="16dp">

    <TextView
        android:id="@+id/type"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Type"
        android:textSize="28dp"
        android:gravity="center"
        android:textColor="@android:color/black"/>

    <TextView
        android:id="@+id/title"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Title"
        android:textSize="24dp"/>

    <TextView
        android:id="@+id/author"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Author"
        android:textSize="16dp"/>

    <TextView
        android:id="@+id/year"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Year"
        android:gravity="end"
        android:textSize="14dp"/>
</LinearLayout>
```

## Листинг 6: MainActivity.java

```java
public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BibLibAdapter bibLibAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InputStream publications = getResources().openRawResource(R.raw.publications_ferro_en);

        recyclerView = findViewById(R.id.recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setHasFixedSize(true);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(itemDecoration);

        try {
            bibLibAdapter = new BibLibAdapter(publications);
        } catch (IOException e) {
            e.printStackTrace();
        }
        recyclerView.setAdapter(bibLibAdapter);
    }
}
```

## Листинг 7: BibLibAdapter.java

```java
public class BibLibAdapter extends RecyclerView.Adapter<BibLibAdapter.BibLibViewHolder> {

    BibDatabase database;

    BibLibAdapter(InputStream publications) throws IOException {
        InputStreamReader reader = new InputStreamReader(publications);
        database = new BibDatabase(reader);
    }

    @NonNull
    @Override
    public BibLibViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.biblib_entry, parent, false);

        return new BibLibViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BibLibViewHolder holder, int position) {
        BibEntry entry = database.getEntry(position);
        holder.textViewType.setText(entry.getType().name());
        switch (entry.getType()) {
            case ARTICLE:
                holder.textViewType.setBackground(ResourcesCompat.getDrawable(holder.textViewType.getResources(), R.color.ARTICLE, null));
                break;
            case BOOK:
                holder.textViewType.setBackground(ResourcesCompat.getDrawable(holder.textViewType.getResources(), R.color.BOOK, null));
                break;
            case BOOKLET:
                holder.textViewType.setBackground(ResourcesCompat.getDrawable(holder.textViewType.getResources(), R.color.BOOKLET, null));
                break;
            case INBOOK:
                holder.textViewType.setBackground(ResourcesCompat.getDrawable(holder.textViewType.getResources(), R.color.INBOOK, null));
                break;
            case INCOLLECTION:
                holder.textViewType.setBackground(ResourcesCompat.getDrawable(holder.textViewType.getResources(), R.color.INCOLLECTION, null));
                break;
            case INPROCEEDINGS:
                holder.textViewType.setBackground(ResourcesCompat.getDrawable(holder.textViewType.getResources(), R.color.INPROCEEDINGS, null));
                break;
            case MANUAL:
                holder.textViewType.setBackground(ResourcesCompat.getDrawable(holder.textViewType.getResources(), R.color.MANUAL, null));
                break;
            case MASTERSTHESIS:
                holder.textViewType.setBackground(ResourcesCompat.getDrawable(holder.textViewType.getResources(), R.color.MASTERSTHESIS, null));
                break;
            case MISC:
                holder.textViewType.setBackground(ResourcesCompat.getDrawable(holder.textViewType.getResources(), R.color.MISC, null));
                break;
            case PHDTHESIS:
                holder.textViewType.setBackground(ResourcesCompat.getDrawable(holder.textViewType.getResources(), R.color.PHDTHESIS, null));
                break;
            case PROCEEDINGS:
                holder.textViewType.setBackground(ResourcesCompat.getDrawable(holder.textViewType.getResources(), R.color.PROCEEDINGS, null));
                break;
            case TECHREPORT:
                holder.textViewType.setBackground(ResourcesCompat.getDrawable(holder.textViewType.getResources(), R.color.TECHREPORT, null));
                break;
            case UNPUBLISHED:
                holder.textViewType.setBackground(ResourcesCompat.getDrawable(holder.textViewType.getResources(), R.color.UNPUBLISHED, null));
                break;
            case SOFTWARE:
                holder.textViewType.setBackground(ResourcesCompat.getDrawable(holder.textViewType.getResources(), R.color.SOFTWARE, null));
                break;
            case EDITORIAL:
                holder.textViewType.setBackground(ResourcesCompat.getDrawable(holder.textViewType.getResources(), R.color.EDITORIAL, null));
                break;
            default:
                holder.textViewType.setBackground(ResourcesCompat.getDrawable(holder.textViewType.getResources(), R.color.colorPrimaryDark, null));
        }
        holder.textViewTitle.setText(entry.getField(Keys.TITLE));
        holder.textViewAuthor.setText(entry.getField(Keys.AUTHOR));
        holder.textViewYear.setText(entry.getField(Keys.YEAR));
    }

    @Override
    public int getItemCount() {
        return database.size();
    }

    static class BibLibViewHolder extends RecyclerView.ViewHolder {

        TextView textViewType;
        TextView textViewTitle;
        TextView textViewAuthor;
        TextView textViewYear;

        public BibLibViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewType = itemView.findViewById(R.id.type);
            textViewTitle = itemView.findViewById(R.id.title);
            textViewAuthor = itemView.findViewById(R.id.author);
            textViewYear = itemView.findViewById(R.id.year);
        }
    }
}
```