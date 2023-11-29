@echo off
if "%~1"=="" (
    echo check hostname
    exit /b
)
echo ping %1| ncat localhost 1233