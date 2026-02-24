@echo off
set "LIB_DIR=lib\mysql-connector-j-9.6.0"
set "JAR_NAME=mysql-connector-j-9.6.0.jar"
set "CP=%LIB_DIR%\%JAR_NAME%;out"

if not exist "out" mkdir "out"

echo Compiling...
javac -d out -cp "%LIB_DIR%\%JAR_NAME%" src\disasterrelief\*.java

if %ERRORLEVEL% NEQ 0 (
    echo Compilation failed.
    pause
    exit /b %ERRORLEVEL%
)

echo Running...
java -cp "%CP%" disasterrelief.Main
pause
