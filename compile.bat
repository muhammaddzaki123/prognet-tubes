@echo off
echo Compiling Java Memory Game...

if not exist "bin" mkdir bin

javac -d bin -cp "lib/*" src/common/*.java
javac -d bin -cp "lib/*;bin" src/server/*.java
javac -d bin -cp "lib/*;bin" src/client/*.java
javac -d bin -cp "lib/*;bin" src/client/gui/*.java
javac -d bin -cp "lib/*;bin" src/Main.java

echo Compilation complete!
pause
