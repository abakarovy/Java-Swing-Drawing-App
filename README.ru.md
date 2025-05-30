# Программа для создания графических изображений by maga

Программа для создания графических изображений, созданное с помощью встроенной библиотеки Java Swing UI


## Использование
Установить проект с помощью ```setup.exe```, приложение окажется в ```C:\Program Files\SetupFrame```
Альтернативно, для запуска программы можно открыть исполняемый файл ```app.jar```, который находится в корневой папке проекта
***(Необходимо наличие переменной среды JAVA_HOME)***


## Ручная сборка программы

### С помощью Maven
Для сборки проекта с Maven нужно выполнить ```mvn package```, или же ```mvn clean package``` в корневой папке проекта. Эта команда генерирует .jar файл с разрешениями на исполнение (как у .exe файла) в папке `target`

### Без Maven
Для сборки проекта без Maven нужно скомпилировать java код, который находится под `src` папкой: <br>
```
javac -d [папка в которую хотите поместить скомпилированные классы] .\src\main\java\com\maga\*.java
```
После, для использования новой сборки, можно сразу выполнить эту команду на скомпилированых .class файлах: <br>

```
java -classpath "[путь к скомпилированным файлам];./src/main/resources" com.maga.DrawingApp
```
<!-- 
<pre><code><span style="color: red;">java</span> <span style="color: grey;">-classpath</span> <span style="color: cyan;">"[путь к скомпилированным файлам];./src/main/resources"</span> <span style="color: cyan;">com.maga.DrawingApp</span></code></pre> -->

либо же собрать .jar файл из этих файлов
```jar cvfe app.jar JavaSwing-DrawingApp *.class```



Для сборки jar файла в exe установщик, рекомендуется использовать ```jpackage```
```jpackage --input . --type exe --main-jar JavaSwing-DrawingApp.jar --win-dir-chooser --win-menu --win-shortcut --win-upgrade-uuid <ваш-uuid>```