@echo off
echo Compiling the project...
javac -cp "lib\sqlite-jdbc.jar;lib\slf4j-api.jar;lib\slf4j-simple.jar" src\*.java
if %errorlevel% neq 0 (
    echo Compilation failed.
    exit /b %errorlevel%
)
echo Compilation completed successfully.
