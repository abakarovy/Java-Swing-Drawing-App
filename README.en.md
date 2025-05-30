# Drawing application by maga

A drawing application created using Java's builtin Swing UI library, with Maven


## How to use
Install the app using ```setup.exe```, the installation will be located in ```C:\Program Files\SetupFrame```
You can open an executable ```app.jar``` file that resides in the project root folder to run the program
***(JAVA_HOME environment variable is required)***


## How to build

### With Maven
To build the project you have to run
```mvn package``` or ```mvn clean package``` in the project folder.
This generates a .jar file with execute permissions

### Without Maven
To build the project without using maven you have to compile the java code under `src`: <br>
<!-- 
<pre><code><span style="color: red;">javac</span> <span style="color: grey;">-d</span> <span style="color: cyan;">[your output folder for compiled classes]</span> <span style="color: cyan;">.\src\main\java\com\maga\*.java</span></code></pre> -->
```
javac -d [your output folder for compiled classes] .\src\main\java\com\maga\*.java
```



Then, to run the build, you can either execute this command on the compiled .class files: <br>
```
java -classpath "[path to compiled .class files];./src/main/resources" com.maga.DrawingApp
```
<!-- <pre><code><span style="color: red;">java</span> <span style="color: grey;">-classpath</span> <span style="color: cyan;">"[path to compiled .class files];./src/main/resources"</span> <span style="color: cyan;">com.maga.DrawingApp</span></code></pre> -->

or you can build a jar file using the previously compiled .class files
```jar cvfe app.jar JavaSwing-DrawingApp *.class```


To bundle the jar file into an exe installer file, you can use ```jpackage```, using the following command (recommended)
```jpackage --input . --type exe --main-jar JavaSwing-DrawingApp.jar --win-dir-chooser --win-menu --win-shortcut --win-upgrade-uuid <your-uuid-string>```