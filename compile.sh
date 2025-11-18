#!/bin/bash

echo "Compiling Java Memory Game..."

# Create bin directory if not exists
if [ ! -d "bin" ]; then
    mkdir bin
fi

# Compile common classes
javac -d bin -cp "lib/*" src/common/*.java

# Compile server classes
javac -d bin -cp "lib/*:bin" src/server/*.java

# Compile client classes
javac -d bin -cp "lib/*:bin" src/client/*.java

# Compile client GUI classes
javac -d bin -cp "lib/*:bin" src/client/gui/*.java

# Compile Main class
javac -d bin -cp "lib/*:bin" src/Main.java

echo "Compilation complete!"
