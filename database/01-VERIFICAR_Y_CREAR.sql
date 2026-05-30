-- ============================================
-- Paso 1: Verificar si existe la BD
-- Ejecutar ESTE primero en pgAdmin
-- ============================================

-- Verificar si existe
SELECT 'La base de datos hospital_san_rafael ' || 
    CASE 
        WHEN EXISTS (SELECT 1 FROM pg_database WHERE datname = 'hospital_san_rafael') 
        THEN 'YA EXISTE - Ir al Paso 2' 
        ELSE 'NO EXISTE - Crear ahora' 
    END AS estado;

-- Si no existe, crearla (ejecutar por separado si es necesario)
-- CREATE DATABASE hospital_san_rafael WITH OWNER = postgres ENCODING = 'UTF8';

-- Ver tablas existentes
SELECT tablename FROM pg_tables WHERE schemaname = 'public' ORDER BY tablename;

-- Contar registros
SELECT 'persona' AS tabla, COUNT(*) FROM persona
UNION ALL SELECT 'estudiante', COUNT(*) FROM estudiante
UNION ALL SELECT 'doctor', COUNT(*) FROM doctor;
