@echo off

if "%~1"=="" (
    echo fetch fname
    exit /b
)

echo fetch %1| ncat localhost 1232