#!/bin/bash

echo "Starting Memory Game Client..."
JAVAFX_PATH="lib/javafx-sdk-21.0.2/lib"
java --module-path "$JAVAFX_PATH" --add-modules javafx.controls,javafx.fxml,javafx.media -cp "bin:lib/*" Main client
