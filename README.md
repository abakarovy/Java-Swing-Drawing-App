# Drawing application created by maga

A drawing application created using Java's builtin Swing UI library, with Maven


## How to use
You can open ```demo.jar``` that resides in the project folder to run the program 
***(JAVA_HOME environment variable has to be present)***

## How to build
### With maven
To build the project you have to run
```mvn package``` or ```mvn clean package``` in the project folder

### Without maven
To build the project without using maven you have to compile the java code under `src`
```console
javac -d <your folder for compiler classes> .\src\main\java\com\maga\*.java
```
then run this command
```console
java -classpath "./build;./src/main/resources" com.maga.DrawingApp
```