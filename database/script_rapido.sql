-- ============================================
-- Script Rápido - Crear Base de Datos
-- Hospital San Rafael
-- Ejecutar en pgAdmin (PostgreSQL 13 o 18)
-- ============================================

-- Crear la base de datos si no existe
SELECT 'Creando base de datos hospital_san_rafael...' AS mensaje;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'hospital_san_rafael') THEN
        CREATE DATABASE hospital_san_rafael
            WITH OWNER = postgres
            ENCODING = 'UTF8'
            LC_COLLATE = 'Spanish_Spain.1252'
            LC_CTYPE = 'Spanish_Spain.1252'
            CONNECTION LIMIT = -1;
        
        RAISE NOTICE 'Base de datos creada exitosamente';
    ELSE
        RAISE NOTICE 'La base de datos ya existe';
    END IF;
END $$;

-- Verificar
SELECT 'Base de datos lista para usar' AS estado;
SELECT 'Ahora ejecuta el script completo: database/script.sql' AS siguiente_paso;
