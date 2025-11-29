@echo off
echo ========================================
echo   Memory Game Client
echo ========================================
echo.
echo Starting JavaFX client...
echo.

set MAVEN_OPTS=--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.nio=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED
mvn javafx:run 2>&1 | findstr /V /C:"sun.misc.Unsafe" /C:"terminally deprecated" /C:"HiddenClassDefiner"

pause
