@echo off
if "%1"=="" (
    echo Usage: sfss-cli.bat ^<command^> [filename]
    exit /b 1
)

set CMD=com.example.sfss.Main

mvn exec:java -q -Dexec.mainClass=%CMD% -Dexec.args="%*"
