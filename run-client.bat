@echo off
echo Starting Memory Game Client...
set JAVAFX_PATH=lib\javafx-sdk\lib
java --module-path "%JAVAFX_PATH%" --add-modules javafx.controls,javafx.fxml -cp "bin;lib/*;%JAVAFX_PATH%/*" Main client
pause
