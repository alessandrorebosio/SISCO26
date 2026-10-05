@echo off
setlocal

set "SCRIPT_DIR=%~dp0"
set "APP_DIR=%SCRIPT_DIR%app"
set "LTL2BUCHI_JAR=%APP_DIR%\ltl2buchi.jar"
set "LTSA_JAR=%APP_DIR%\ltsa.jar"

where java >nul 2>&1
if errorlevel 1 (
    echo Error: Java is required but was not found in PATH.
    exit /b 1
)

if not exist "%LTL2BUCHI_JAR%" (
    echo Error: required file not found: %LTL2BUCHI_JAR%
    exit /b 1
)

if not exist "%LTSA_JAR%" (
    echo Error: required file not found: %LTSA_JAR%
    exit /b 1
)

rem LTL2Buchi is a runtime module of LTSA and is included in its classpath.
java -cp "%LTSA_JAR%;%LTL2BUCHI_JAR%" HPWindow

endlocal
