@echo off
setlocal

:: Check if javac is installed
where javac >nul 2>nul
if %errorlevel% neq 0 (
    echo Java (javac) is not installed.
    set /p confirm="Would you like to install it now using winget? (y/n): "
    if /i "%confirm%"=="y" (
        echo Installing Oracle Java...
        winget install -e --id Oracle.Java
        echo Please restart your command prompt and run this script again.
        exit /b 1
    ) else (
        echo Please install Java manually to build the project.
        exit /b 1
    )
)

echo Compiling the project...
javac -cp "lib\sqlite-jdbc.jar;lib\slf4j-api.jar;lib\slf4j-simple.jar" src\*.java
if %errorlevel% neq 0 (
    echo Compilation failed.
    exit /b %errorlevel%
)
echo Compilation completed successfully.
endlocal
