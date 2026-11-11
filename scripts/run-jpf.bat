@echo off
setlocal

set "SCRIPT_DIR=%~dp0"
set "JPF_DIR=%SCRIPT_DIR%..\tools\jpf"
set "RUNJPF_JAR=%JPF_DIR%\build\RunJPF.jar"
set "SITE_PROPERTIES=%JPF_DIR%\site.properties"

if "%~1"=="" (
    echo Usage: %~nx0 ^<file.jpf^> [JPF options]
    exit /b 1
)
if not exist "%RUNJPF_JAR%" (
    echo Error: required file not found: %RUNJPF_JAR%
    exit /b 1
)
if not exist "%SITE_PROPERTIES%" (
    echo Error: required file not found: %SITE_PROPERTIES%
    exit /b 1
)

rem JPF runs only on Java 11: JPF_JAVA_HOME (or JAVA_HOME) must point to a JDK 11.
set "JAVA=java"
if defined JAVA_HOME set "JAVA=%JAVA_HOME%\bin\java"
if defined JPF_JAVA_HOME set "JAVA=%JPF_JAVA_HOME%\bin\java"

if not defined JVM_FLAGS set "JVM_FLAGS=-Xmx1024m -ea"

"%JAVA%" %JVM_FLAGS% -jar "%RUNJPF_JAR%" "+site=%SITE_PROPERTIES%" %*
exit /b %errorlevel%
