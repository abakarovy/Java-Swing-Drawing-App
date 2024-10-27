- [English documentation](README.en.md)
- [Русская документация](README.ru.md)

# Drawing application by maga

A drawing application created using Java's builtin Swing UI library, with Maven


## How to use
You can open an executable ```demo.jar``` file that resides in the project root folder to run the program
***(JAVA_HOME environment variable is required)***


## How to build

### With Maven
To build the project you have to run
```mvn package``` or ```mvn clean package``` in the project folder.
This generates a .jar file with execute permissions

### Without Maven
To build the project without using maven you have to compile the java code under `src`: <br>

```sh
javac -d [your output folder for compiled classes] .\src\main\java\com\maga\*.java
```

Then, to run the build, you can either execute this command on the compiled .class files: <br>
```sh
java -classpath "[path to compiled .class files];./src/main/resources" com.maga.DrawingApp
```

or you can build a jar file using the previously compiled .class files