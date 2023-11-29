@echo off

if "%~2"=="" (
    echo publish lname fname
    exit /b
)

set "destination_folder=C:\Users\Admin\Desktop\BT_MMT\Client\repository"  

if not exist "%1" (
    echo File "%1" does not exist in the client's file system.
    exit /b
)

copy "%1" "%destination_folder%\%2" > nul

echo publish %1 %2| ncat localhost 1232
