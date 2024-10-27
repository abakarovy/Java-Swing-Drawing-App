# Программа для создания графических изображений by maga

Программа для создания графических изображений, созданное с помощью встроенной библиотеки Java Swing UI


## Использование
Для запуска программы можно открыть исполняемый файл ```demo.jar```, который находится в корневой папке проекта
***(Необходимо наличие переменной среды JAVA_HOME)***


## Ручная сборка программы

### С помощью Maven
Для сборки проекта с Maven нужно выполнить ```mvn package```, или же ```mvn clean package``` в корневой папке проекта. Эта команда генерирует .jar файл с разрешениями на исполнение (как у .exe файла) в папке `target`

### Без Maven
Для сборки проекта без Maven нужно скомпилировать java код, который находится под `src` папкой: <br>
<pre><code><span style="color: red;">javac</span> <span style="color: grey;">-d</span> <span style="color: cyan;">[папка в которую хотите поместить скомпилированные файлы]</span> <span style="color: cyan;">.\src\main\java\com\maga\*.java</span></code></pre>

После, для использования новой сборки, можно сразу выполнить эту команду на скомпилированых .class файлах: <br>

<pre><code><span style="color: red;">java</span> <span style="color: grey;">-classpath</span> <span style="color: cyan;">"[путь к скомпилированным файлам];./src/main/resources"</span> <span style="color: cyan;">com.maga.DrawingApp</span></code></pre>

либо же собрать .jar файл из этих файлов