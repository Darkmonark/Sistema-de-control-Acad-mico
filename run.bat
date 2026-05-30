@echo off
echo ========================================
echo Hospital San Rafael - Sistema de Gestion
echo ========================================
echo.

REM Verificar Java
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java no esta instalado o no esta en el PATH
    echo Descargue Java 17 desde: https://adoptium.net/
    pause
    exit /b 1
)

echo Java detectado correctamente
echo.

REM Ejecutar con Maven
if exist mvnw.cmd (
    echo Ejecutando con Maven Wrapper...
    mvnw.cmd javafx:run
) else if exist pom.xml (
    echo Ejecutando con Maven...
    mvn javafx:run
) else (
    echo ERROR: No se encontro pom.xml
    pause
    exit /b 1
)

echo.
pause
