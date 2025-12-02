@echo off
echo Compiling Java Memory Game...

if not exist "bin" mkdir bin
if not exist "bin\client\gui\javafx" mkdir "bin\client\gui\javafx"

set JAVAFX_PATH=lib\javafx-sdk\lib
set CLASSPATH=lib\*;%JAVAFX_PATH%\*

javac -d bin -cp "%CLASSPATH%" src/common/*.java
javac -d bin -cp "%CLASSPATH%;bin" src/server/*.java
javac -d bin -cp "%CLASSPATH%;bin" src/client/GameClient.java
javac -d bin -cp "%CLASSPATH%;bin" src/client/UDPDiscoveryClient.java
javac -d bin -cp "%CLASSPATH%;bin" src/client/gui/javafx/*.java
javac -d bin -cp "%CLASSPATH%;bin" src/Main.java

echo Compilation complete!
pause
