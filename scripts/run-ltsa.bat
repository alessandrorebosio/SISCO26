@echo off
setlocal

set "SCRIPT_DIR=%~dp0"
set "REPO_DIR=%SCRIPT_DIR%.."
set "LTSA_DIR=%REPO_DIR%\tools\ltsa"
set "LTL2BUCHI_JAR=%LTSA_DIR%\ltl2buchi.jar"
set "LTSA_JAR=%LTSA_DIR%\ltsa.jar"

where java >nul 2>&1
if errorlevel 1 (
    echo Error: Java is required but was not found in PATH.
    goto :fail
)
if not exist "%LTL2BUCHI_JAR%" (
    echo Error: required file not found: %LTL2BUCHI_JAR%
    goto :fail
)
if not exist "%LTSA_JAR%" (
    echo Error: required file not found: %LTSA_JAR%
    goto :fail
)

if "%~1"=="" (
    java -cp "%LTSA_JAR%;%LTL2BUCHI_JAR%" HPWindow
) else (
    java -cp "%LTSA_JAR%;%LTL2BUCHI_JAR%" HPWindow "%~1"
)
exit /b %errorlevel%

:fail
pause
exit /b 1