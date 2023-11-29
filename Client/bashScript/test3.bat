@echo off
if "%~1"=="" (
    echo check hostname
    exit /b
)
echo test3 %1 | ncat localhost 4444