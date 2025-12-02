@echo off
echo ===================================
echo    Memory Game Server
echo ===================================
echo.
echo Starting server on port 5000...
echo UDP broadcast on port 5001
echo.
echo Server IP addresses:
ipconfig | findstr /R /C:"IPv4 Address"
echo.
echo Share above IP with clients to connect
echo Press Ctrl+C to stop server
echo ===================================
echo.
set JAVAFX_PATH=lib\javafx-sdk\lib
java --module-path "%JAVAFX_PATH%" --add-modules javafx.controls,javafx.fxml -cp "bin;lib/*;%JAVAFX_PATH%/*" Main server
pause
