-- Verificar y corregir campo apellido en tabla persona
DO $$ 
BEGIN 
    -- Verificar si la columna apellido existe
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'persona' AND column_name = 'apellido'
    ) THEN
        ALTER TABLE persona ADD COLUMN apellido VARCHAR(100);
        RAISE NOTICE 'Columna apellido agregada exitosamente';
    ELSE
        RAISE NOTICE 'La columna apellido ya existe';
    END IF;
END $$;

-- Verificar datos
SELECT id, nombre, apellido, email FROM persona LIMIT 10;
