#!/bin/bash

echo "Compiling Java Memory Game..."

# Create bin directory if not exists
if [ ! -d "bin" ]; then
    mkdir bin
fi

# Set JavaFX path
JAVAFX_PATH="lib/javafx-sdk-21.0.2/lib"

# Compile common classes
javac -d bin -cp "lib/*" src/common/*.java

# Compile server classes
javac -d bin -cp "lib/*:bin" src/server/*.java

# Compile client classes (excluding gui/javafx first to avoid errors if dependencies are mixed)
javac -d bin -cp "lib/*:bin:$JAVAFX_PATH/*" --module-path "$JAVAFX_PATH" --add-modules javafx.controls,javafx.fxml,javafx.media src/client/*.java

# Compile client GUI classes
javac -d bin -cp "lib/*:bin:$JAVAFX_PATH/*" --module-path "$JAVAFX_PATH" --add-modules javafx.controls,javafx.fxml,javafx.media src/client/gui/javafx/*.java

# Compile Main class
javac -d bin -cp "lib/*:bin:$JAVAFX_PATH/*" --module-path "$JAVAFX_PATH" --add-modules javafx.controls,javafx.fxml,javafx.media src/Main.java

echo "Compilation complete!"
