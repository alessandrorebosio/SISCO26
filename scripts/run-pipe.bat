@echo off
setlocal

set "SCRIPT_DIR=%~dp0"
set "PIPE_JAR=%SCRIPT_DIR%..\tools\pipe\pipe.jar"

where java >nul 2>&1
if errorlevel 1 (
    echo Error: Java is required but was not found in PATH.
    exit /b 1
)

if not exist "%PIPE_JAR%" (
    echo Error: required file not found: %PIPE_JAR%
    exit /b 1
)

java -jar "%PIPE_JAR%" %*
exit /b %errorlevel%
