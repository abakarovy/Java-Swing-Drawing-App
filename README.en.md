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
<code>
    <span style="color: red;">javac</span>
    <span style="color: grey;">-d</span>
    <span style="color: cyan;">[your output folder for compiled classes]</span>
    <span style="color: cyan;">.\src\main\java\com\maga\\*.java</span>
</code>

Then, to run the build, you can either execute this command on the compiled .class files: <br>
<code>
    <span style="color: red;">java</span>
    <span style="color: grey;">-classpath</span>
    <span style="color: cyan;">"[path to compiled .class files];./src/main/resources"</span>
    <span style="color: cyan;">com.maga.DrawingApp</span>
</code>

or you can build a jar file using the previously compiled .class files