@echo off
if "%~1"=="" (
    echo discover hostname
    exit /b
)
echo discover %1| ncat localhost 1233
