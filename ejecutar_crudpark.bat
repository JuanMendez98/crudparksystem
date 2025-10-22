@echo off
title CRUDPARK - Sistema de Parqueadero
echo ========================================
echo    CRUDPARK - Sistema de Parqueadero
echo ========================================
echo.
echo Iniciando aplicacion...
echo.

java -jar target\crudpark-1.0.0.jar

if %errorlevel% neq 0 (
    echo.
    echo ERROR: La aplicacion no pudo iniciarse
    echo Verifique que:
    echo - Java esté instalado
    echo - PostgreSQL esté ejecutándose
    echo - El archivo JAR esté presente
)

echo.
echo Presione cualquier tecla para salir...
pause >nul