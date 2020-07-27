@echo off

%~d0
cd "%~dp0"


::set JAVA_HOME=C:\Program_Files\java\jdk8

if exist ".\_props.bat" call ".\_props.bat"

set JAVA=java
if not "%JAVA_HOME%"=="" (
  set JAVA="%JAVA_HOME%\bin\java"
)



setlocal EnableDelayedExpansion
set CP=.\GraphSamples.jar
set LIB_DIR=.\lib
for /f %%I in ('dir /b "%LIB_DIR%\*.jar"') do (
  set CP=!CP!;!LIB_DIR!\%%I
)
setlocal DisableDelayedExpansion


start "GraphSamples" %JAVA% -cp "%CP%" ru.vsu.cs.course1.graph.demo.Program %*
