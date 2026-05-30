#!/bin/bash

echo "========================================"
echo "Hospital San Rafael - Sistema de Gestion"
echo "========================================"
echo ""

# Verificar Java
if ! command -v java &> /dev/null; then
    echo "ERROR: Java no esta instalado o no esta en el PATH"
    echo "Descargue Java 17 desde: https://adoptium.net/"
    exit 1
fi

echo "Java detectado correctamente"
echo ""

# Ejecutar con Maven
if [ -f "./mvnw" ]; then
    echo "Ejecutando con Maven Wrapper..."
    ./mvnw javafx:run
elif [ -f "pom.xml" ]; then
    echo "Ejecutando con Maven..."
    mvn javafx:run
else
    echo "ERROR: No se encontro pom.xml"
    exit 1
fi
