-- ============================================
-- Script para crear la BD si no existe
-- Ejecutar en CADA instancia de PostgreSQL (13 y 18)
-- ============================================

-- Verificar si existe
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'hospital_san_rafael') THEN
        -- No existe, crearla
        EXECUTE 'CREATE DATABASE hospital_san_rafael WITH OWNER = postgres ENCODING = ''UTF8''';
        RAISE NOTICE 'Base de datos CREADA exitosamente';
    ELSE
        RAISE NOTICE 'La base de datos YA EXISTE - No se hace nada';
    END IF;
END $$;

-- Verificar creación
SELECT 'Estado final:' as estado, 
       CASE WHEN EXISTS (SELECT 1 FROM pg_database WHERE datname = 'hospital_san_rafael') 
            THEN 'EXiste' 
            ELSE 'NO existe' 
       END as resultado;

-- Si existe, mostrar tablas
SELECT 'Tablas en hospital_san_rafael:' as info;
-- (Esto se ejecutará después de conectar a la BD)
