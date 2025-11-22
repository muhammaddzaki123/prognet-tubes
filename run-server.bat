@echo off
echo ========================================
echo   Memory Game Server - Standalone
echo ========================================
echo.
echo Starting server on port 5000...
echo UDP broadcast on port 5001
echo.
echo Server IP addresses:
for /f "tokens=2 delims=:" %%a in ('ipconfig ^| findstr /i "IPv4"') do echo   - %%a
echo.
echo Clients can connect using above IP addresses
echo Press Ctrl+C to stop server
echo ========================================
echo.

mvn exec:java "-Dexec.mainClass=prognet.ServerApp"

pause
