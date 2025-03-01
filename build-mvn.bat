@echo off
REM Build the project using Maven
echo Building project...
mvn clean package

REM Check if build was successful
IF %ERRORLEVEL% NEQ 0 (
    echo Build failed! Exiting...
    exit /b 1
)

